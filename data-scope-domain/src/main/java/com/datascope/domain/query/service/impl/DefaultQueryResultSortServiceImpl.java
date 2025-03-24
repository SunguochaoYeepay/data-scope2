package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultSortService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 默认查询结果排序服务实现
 */
@Slf4j
@Service
public class DefaultQueryResultSortServiceImpl implements QueryResultSortService {

    @Override
    public QueryResult sort(QueryResult result, List<SortField> sortFields) {
        if (result == null || sortFields == null || sortFields.isEmpty()) {
            return result;
        }

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
                return -1;
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
        try {
            BigDecimal num1 = new BigDecimal(value1.toString());
            BigDecimal num2 = new BigDecimal(value2.toString());
            return num1.compareTo(num2);
        } catch (NumberFormatException e) {
            // 不是数值，继续尝试其他类型
        }

        // 尝试日期时间比较
        try {
            LocalDateTime date1 = parseDateTime(value1.toString());
            LocalDateTime date2 = parseDateTime(value2.toString());
            return date1.compareTo(date2);
        } catch (DateTimeParseException e) {
            // 不是日期时间，继续尝试其他类型
        }

        // 默认使用字符串比较
        return value1.toString().compareTo(value2.toString());
    }

    /**
     * 尝试解析日期时间字符串
     */
    private LocalDateTime parseDateTime(String value) {
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException e1) {
            try {
                return LocalDate.parse(value).atStartOfDay();
            } catch (DateTimeParseException e2) {
                throw e1; // 抛出原始异常
            }
        }
    }
}
