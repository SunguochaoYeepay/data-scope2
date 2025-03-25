package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultSortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 增强版查询结果排序服务实现
 * 支持更多数据类型和排序选项
 */
@Slf4j
@Service
@Primary
public class EnhancedQueryResultSortServiceImpl implements QueryResultSortService {

    // 常用日期时间格式
    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_DATE,
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
    );

    private static final List<DateTimeFormatter> TIME_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ISO_LOCAL_TIME,
        DateTimeFormatter.ISO_TIME,
        DateTimeFormatter.ofPattern("HH:mm:ss"),
        DateTimeFormatter.ofPattern("HH:mm"),
        DateTimeFormatter.ofPattern("hh:mm:ss a"),
        DateTimeFormatter.ofPattern("hh:mm a")
    );

    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = Arrays.asList(
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_DATE_TIME,
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
    );

    @Override
    public QueryResult sort(QueryResult result, List<SortField> sortFields) {
        if (result == null || sortFields == null || sortFields.isEmpty()) {
            return result;
        }

        // 记录排序字段信息
        String sortFieldsInfo = sortFields.stream()
            .map(f -> f.getFieldName() + " " + f.getDirection())
            .collect(Collectors.joining(", "));
        // 在生产环境中，可以使用 log.debug() 记录详细信息

        // 创建新的QueryResult对象
        QueryResult sortedResult = new QueryResult();

        // 复制原始QueryResult的属性
        sortedResult.setColumns(result.getColumns());
        sortedResult.setTotalRows(result.getTotalRows());
        sortedResult.setHasMore(result.isHasMore());
        sortedResult.setExecutionTime(result.getExecutionTime());

        // 对行数据进行排序
        List<Map<String, Object>> sortedRows = sortRows(result.getRows(), sortFields);
        sortedResult.setRows(sortedRows);

        // 在生产环境中，可以使用 log.debug() 记录排序完成信息
        return sortedResult;
    }

    @Override
    public List<Map<String, Object>> sortRows(List<Map<String, Object>> rows, List<SortField> sortFields) {
        if (rows == null || rows.isEmpty() || sortFields == null || sortFields.isEmpty()) {
            return rows;
        }

        // 创建行数据的副本
        List<Map<String, Object>> sortedRows = new ArrayList<>(rows);

        // 创建比较器
        Comparator<Map<String, Object>> comparator = createComparator(sortFields);

        // 排序
        sortedRows.sort(comparator);

        return sortedRows;
    }

    /**
     * 创建多字段比较器
     */
    private Comparator<Map<String, Object>> createComparator(List<SortField> sortFields) {
        Comparator<Map<String, Object>> comparator = null;

        for (SortField sortField : sortFields) {
            Comparator<Map<String, Object>> fieldComparator = createFieldComparator(sortField);

            if (comparator == null) {
                comparator = fieldComparator;
            } else {
                comparator = comparator.thenComparing(fieldComparator);
            }
        }

        return comparator != null ? comparator : (o1, o2) -> 0;
    }

    /**
     * 创建单字段比较器
     */
    private Comparator<Map<String, Object>> createFieldComparator(SortField sortField) {
        String fieldName = sortField.getFieldName();
        SortDirection direction = sortField.getDirection();

        Comparator<Map<String, Object>> comparator = (row1, row2) -> {
            Object value1 = row1.get(fieldName);
            Object value2 = row2.get(fieldName);

            // 处理null值
            if (value1 == null && value2 == null) {
                return 0;
            } else if (value1 == null) {
                return -1; // null值排在前面
            } else if (value2 == null) {
                return 1;
            }

            // 根据值类型进行比较
            int result = compareValues(value1, value2);

            // 根据排序方向调整比较结果
            return direction == SortDirection.ASC ? result : -result;
        };

        return comparator;
    }

    /**
     * 比较两个值
     */
    @SuppressWarnings("unchecked")
    private int compareValues(Object value1, Object value2) {
        // 如果类型相同且实现了Comparable接口
        if (value1.getClass() == value2.getClass() && value1 instanceof Comparable) {
            return ((Comparable<Object>) value1).compareTo(value2);
        }

        // 尝试数值比较
        if (isNumeric(value1) && isNumeric(value2)) {
            try {
                BigDecimal num1 = new BigDecimal(value1.toString());
                BigDecimal num2 = new BigDecimal(value2.toString());
                return num1.compareTo(num2);
            } catch (NumberFormatException e) {
                // 不是数值，继续尝试其他类型
                // 在生产环境中，可以使用 log.trace() 记录详细信息
            }
        }

        // 尝试日期时间比较
        if (isDateLike(value1) && isDateLike(value2)) {
            try {
                Comparable<?> date1 = parseAsDateTime(value1.toString());
                Comparable<?> date2 = parseAsDateTime(value2.toString());
                if (date1 != null && date2 != null && date1.getClass() == date2.getClass()) {
                    // 确保类型相同，然后进行比较
                    if (date1 instanceof LocalDateTime) {
                        return ((LocalDateTime) date1).compareTo((LocalDateTime) date2);
                    } else if (date1 instanceof LocalDate) {
                        return ((LocalDate) date1).compareTo((LocalDate) date2);
                    } else if (date1 instanceof LocalTime) {
                        return ((LocalTime) date1).compareTo((LocalTime) date2);
                    }
                }
            } catch (Exception e) {
                // 不是日期时间，继续尝试其他类型
                // 在生产环境中，可以使用 log.trace() 记录详细信息
            }
        }

        // 尝试布尔值比较
        if (isBoolean(value1) && isBoolean(value2)) {
            Boolean bool1 = parseBoolean(value1.toString());
            Boolean bool2 = parseBoolean(value2.toString());
            return bool1.compareTo(bool2);
        }

        // 默认使用字符串比较
        return value1.toString().compareTo(value2.toString());
    }

    /**
     * 判断对象是否可能是数值
     */
    private boolean isNumeric(Object value) {
        if (value instanceof Number) {
            return true;
        }
        if (value instanceof String) {
            String str = (String) value;
            return str.matches("-?\\d+(\\.\\d+)?");
        }
        return false;
    }

    /**
     * 判断对象是否可能是日期
     */
    private boolean isDateLike(Object value) {
        if (value instanceof Date || value instanceof LocalDate ||
            value instanceof LocalDateTime || value instanceof LocalTime) {
            return true;
        }
        if (value instanceof String) {
            String str = (String) value;
            // 简单检查是否包含日期分隔符和数字
            return (str.contains("-") || str.contains("/")) &&
                str.matches(".*\\d+.*");
        }
        return false;
    }

    /**
     * 判断对象是否可能是布尔值
     */
    private boolean isBoolean(Object value) {
        if (value instanceof Boolean) {
            return true;
        }
        if (value instanceof String) {
            String str = ((String) value).toLowerCase();
            return str.equals("true") || str.equals("false") ||
                str.equals("yes") || str.equals("no") ||
                str.equals("1") || str.equals("0") ||
                str.equals("y") || str.equals("n");
        }
        return false;
    }

    /**
     * 解析布尔值
     */
    private Boolean parseBoolean(String value) {
        String str = value.toLowerCase();
        return str.equals("true") || str.equals("yes") ||
            str.equals("1") || str.equals("y");
    }

    /**
     * 尝试将字符串解析为日期时间对象
     */
    private Comparable<?> parseAsDateTime(String value) {
        // 尝试解析为LocalDateTime
        for (DateTimeFormatter formatter : DATETIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }

        // 尝试解析为LocalDate
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }

        // 尝试解析为LocalTime
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalTime.parse(value, formatter);
            } catch (DateTimeParseException e) {
                // 继续尝试下一个格式
            }
        }

        return null;
    }
}
