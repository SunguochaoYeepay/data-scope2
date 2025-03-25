package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultFilterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 默认查询结果过滤服务实现
 */
@Slf4j
@Service
public class DefaultQueryResultFilterServiceImpl implements QueryResultFilterService {

    @Override
    public QueryResult filter(QueryResult result, FilterGroup filterGroup) {
        if (result == null || filterGroup == null || filterGroup.getConditions().isEmpty()) {
            return result;
        }

        // 创建新的QueryResult对象
        QueryResult filteredResult = new QueryResult();

        // 复制原始QueryResult的属性
        filteredResult.setColumns(result.getColumns());
        filteredResult.setExecutionTime(result.getExecutionTime());

        // 对行数据进行过滤
        List<Map<String, Object>> filteredRows = filterRows(result.getRows(), filterGroup);
        filteredResult.setRows(filteredRows);

        // 更新总行数和是否有更多数据
        filteredResult.setTotalRows(filteredRows.size());
        filteredResult.setHasMore(false); // 过滤后的结果已经包含所有符合条件的数据

        return filteredResult;
    }

    @Override
    public List<Map<String, Object>> filterRows(List<Map<String, Object>> rows, FilterGroup filterGroup) {
        if (rows == null || rows.isEmpty() || filterGroup == null || filterGroup.getConditions().isEmpty()) {
            return rows;
        }

        // 创建行过滤谓词
        Predicate<Map<String, Object>> rowPredicate = createRowPredicate(filterGroup);

        // 过滤行数据
        return rows.stream()
            .filter(rowPredicate)
            .collect(Collectors.toList());
    }

    @Override
    public Predicate<Map<String, Object>> createRowPredicate(FilterGroup filterGroup) {
        List<Predicate<Map<String, Object>>> predicates = filterGroup.getConditions().stream()
            .map(this::createConditionPredicate)
            .collect(Collectors.toList());

        if (predicates.isEmpty()) {
            return row -> true; // 没有条件，返回接受所有行的谓词
        }

        // 根据逻辑组合谓词
        if (filterGroup.getLogic() == FilterLogic.AND) {
            return row -> predicates.stream().allMatch(p -> p.test(row));
        } else {
            return row -> predicates.stream().anyMatch(p -> p.test(row));
        }
    }

    /**
     * 创建条件谓词
     */
    private Predicate<Map<String, Object>> createConditionPredicate(FilterCondition condition) {
        String fieldName = condition.getFieldName();
        FilterOperator operator = condition.getOperator();
        Object value = condition.getValue();

        return row -> {
            Object fieldValue = row.get(fieldName);

            // 处理空值条件
            if (operator == FilterOperator.IS_NULL) {
                return fieldValue == null;
            }
            if (operator == FilterOperator.IS_NOT_NULL) {
                return fieldValue != null;
            }

            // 如果字段值为null，其他操作符都返回false
            if (fieldValue == null) {
                return false;
            }

            // 根据操作符比较值
            switch (operator) {
                case EQUALS:
                    return compareValues(fieldValue, value) == 0;
                case NOT_EQUALS:
                    return compareValues(fieldValue, value) != 0;
                case GREATER_THAN:
                    return compareValues(fieldValue, value) > 0;
                case GREATER_THAN_EQUALS:
                    return compareValues(fieldValue, value) >= 0;
                case LESS_THAN:
                    return compareValues(fieldValue, value) < 0;
                case LESS_THAN_EQUALS:
                    return compareValues(fieldValue, value) <= 0;
                case CONTAINS:
                    return fieldValue.toString().toLowerCase().contains(value.toString().toLowerCase());
                case NOT_CONTAINS:
                    return !fieldValue.toString().toLowerCase().contains(value.toString().toLowerCase());
                case STARTS_WITH:
                    return fieldValue.toString().toLowerCase().startsWith(value.toString().toLowerCase());
                case ENDS_WITH:
                    return fieldValue.toString().toLowerCase().endsWith(value.toString().toLowerCase());
                case IN:
                    if (value instanceof Collection) {
                        return ((Collection<?>) value).stream()
                            .anyMatch(v -> compareValues(fieldValue, v) == 0);
                    }
                    return false;
                case NOT_IN:
                    if (value instanceof Collection) {
                        return ((Collection<?>) value).stream()
                            .noneMatch(v -> compareValues(fieldValue, v) == 0);
                    }
                    return true;
                default:
                    return false;
            }
        };
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
