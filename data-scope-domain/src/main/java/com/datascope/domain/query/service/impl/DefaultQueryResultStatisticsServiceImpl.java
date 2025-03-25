package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 默认查询结果统计服务实现
 */
@Slf4j
@Service
public class DefaultQueryResultStatisticsServiceImpl implements QueryResultStatisticsService {

    // 统计计算的精度
    private static final int DECIMAL_SCALE = 4;

    @Override
    public StatisticsResult calculate(QueryResult result, String fieldName, StatisticsFunction function) {
        if (result == null || fieldName == null || function == null) {
            throw new IllegalArgumentException("Result, fieldName, and function cannot be null");
        }

        List<Map<String, Object>> rows = result.getRows();
        if (rows == null || rows.isEmpty()) {
            return new StatisticsResult(fieldName, function, null);
        }

        // 提取字段值
        List<Object> fieldValues = rows.stream()
            .map(row -> row.get(fieldName))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (fieldValues.isEmpty()) {
            return new StatisticsResult(fieldName, function, null);
        }

        // 执行统计计算
        Object result1 = calculateStatistics(fieldValues, function);
        return new StatisticsResult(fieldName, function, result1);
    }

    @Override
    public List<StatisticsResult> calculate(QueryResult result, String fieldName, List<StatisticsFunction> functions) {
        if (result == null || fieldName == null || functions == null) {
            throw new IllegalArgumentException("Result, fieldName, and functions cannot be null");
        }

        return functions.stream()
            .map(function -> calculate(result, fieldName, function))
            .collect(Collectors.toList());
    }

    @Override
    public Map<Object, StatisticsResult> calculateGroupBy(QueryResult result, String groupByField, String fieldName, StatisticsFunction function) {
        if (result == null || groupByField == null || fieldName == null || function == null) {
            throw new IllegalArgumentException("Result, groupByField, fieldName, and function cannot be null");
        }

        List<Map<String, Object>> rows = result.getRows();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }

        // 按分组字段分组
        Map<Object, List<Map<String, Object>>> groupedRows = rows.stream()
            .collect(Collectors.groupingBy(row -> row.get(groupByField)));

        // 对每个分组执行统计计算
        Map<Object, StatisticsResult> results = new HashMap<>();
        for (Map.Entry<Object, List<Map<String, Object>>> entry : groupedRows.entrySet()) {
            Object groupValue = entry.getKey();
            List<Map<String, Object>> groupRows = entry.getValue();

            // 创建临时QueryResult对象
            QueryResult groupResult = new QueryResult();
            groupResult.setRows(groupRows);

            // 计算统计结果
            StatisticsResult statResult = calculate(groupResult, fieldName, function);
            results.put(groupValue, statResult);
        }

        return results;
    }

    @Override
    public Map<Object, List<StatisticsResult>> calculateGroupBy(QueryResult result, String groupByField, String fieldName, List<StatisticsFunction> functions) {
        if (result == null || groupByField == null || fieldName == null || functions == null) {
            throw new IllegalArgumentException("Result, groupByField, fieldName, and functions cannot be null");
        }

        List<Map<String, Object>> rows = result.getRows();
        if (rows == null || rows.isEmpty()) {
            return Collections.emptyMap();
        }

        // 按分组字段分组
        Map<Object, List<Map<String, Object>>> groupedRows = rows.stream()
            .collect(Collectors.groupingBy(row -> row.get(groupByField)));

        // 对每个分组执行统计计算
        Map<Object, List<StatisticsResult>> results = new HashMap<>();
        for (Map.Entry<Object, List<Map<String, Object>>> entry : groupedRows.entrySet()) {
            Object groupValue = entry.getKey();
            List<Map<String, Object>> groupRows = entry.getValue();

            // 创建临时QueryResult对象
            QueryResult groupResult = new QueryResult();
            groupResult.setRows(groupRows);

            // 计算统计结果
            List<StatisticsResult> statResults = calculate(groupResult, fieldName, functions);
            results.put(groupValue, statResults);
        }

        return results;
    }

    @Override
    public List<StatisticsResult> calculateBasicStatistics(QueryResult result, String fieldName) {
        return calculate(result, fieldName, Arrays.asList(
            StatisticsFunction.COUNT,
            StatisticsFunction.SUM,
            StatisticsFunction.AVG,
            StatisticsFunction.MIN,
            StatisticsFunction.MAX
        ));
    }

    @Override
    public List<StatisticsResult> calculateAdvancedStatistics(QueryResult result, String fieldName) {
        return calculate(result, fieldName, Arrays.asList(
            StatisticsFunction.MEDIAN,
            StatisticsFunction.MODE,
            StatisticsFunction.VARIANCE,
            StatisticsFunction.STDDEV
        ));
    }

    @Override
    public List<StatisticsResult> calculateFullStatistics(QueryResult result, String fieldName) {
        return calculate(result, fieldName, Arrays.asList(
            StatisticsFunction.COUNT,
            StatisticsFunction.SUM,
            StatisticsFunction.AVG,
            StatisticsFunction.MIN,
            StatisticsFunction.MAX,
            StatisticsFunction.MEDIAN,
            StatisticsFunction.MODE,
            StatisticsFunction.VARIANCE,
            StatisticsFunction.STDDEV
        ));
    }

    /**
     * 执行统计计算
     */
    private Object calculateStatistics(List<Object> values, StatisticsFunction function) {
        switch (function) {
            case COUNT:
                return values.size();
            case SUM:
                return calculateSum(values);
            case AVG:
                return calculateAverage(values);
            case MIN:
                return calculateMin(values);
            case MAX:
                return calculateMax(values);
            case MEDIAN:
                return calculateMedian(values);
            case MODE:
                return calculateMode(values);
            case VARIANCE:
                return calculateVariance(values);
            case STDDEV:
                return calculateStandardDeviation(values);
            default:
                throw new IllegalArgumentException("Unsupported statistics function: " + function);
        }
    }

    /**
     * 计算总和
     */
    private BigDecimal calculateSum(List<Object> values) {
        return values.stream()
            .map(value -> toNumber(value))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 计算平均值
     */
    private BigDecimal calculateAverage(List<Object> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = calculateSum(values);
        return sum.divide(new BigDecimal(values.size()), DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 计算最小值
     */
    private Object calculateMin(List<Object> values) {
        return values.stream()
            .min((o1, o2) -> compareValues(o1, o2))
            .orElse(null);
    }

    /**
     * 计算最大值
     */
    private Object calculateMax(List<Object> values) {
        return values.stream()
            .max((o1, o2) -> compareValues(o1, o2))
            .orElse(null);
    }

    /**
     * 计算中位数
     */
    private BigDecimal calculateMedian(List<Object> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 转换为数值并排序
        List<BigDecimal> sortedValues = values.stream()
            .map(value -> toNumber(value))
            .sorted()
            .collect(Collectors.toList());

        int size = sortedValues.size();
        if (size % 2 == 0) {
            // 偶数个元素，取中间两个的平均值
            BigDecimal value1 = sortedValues.get(size / 2 - 1);
            BigDecimal value2 = sortedValues.get(size / 2);
            return value1.add(value2).divide(new BigDecimal(2), DECIMAL_SCALE, RoundingMode.HALF_UP);
        } else {
            // 奇数个元素，取中间值
            return sortedValues.get(size / 2);
        }
    }

    /**
     * 计算众数
     */
    private Object calculateMode(List<Object> values) {
        if (values.isEmpty()) {
            return null;
        }

        // 计算每个值的出现次数
        Map<Object, Long> valueCounts = values.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // 找出出现次数最多的值
        return valueCounts.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    /**
     * 计算方差
     */
    private BigDecimal calculateVariance(List<Object> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal avg = calculateAverage(values);

        // 计算每个值与平均值的差的平方的和
        BigDecimal sumOfSquaredDifferences = values.stream()
            .map(value -> toNumber(value))
            .map(value -> value.subtract(avg).pow(2))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 除以元素个数得到方差
        return sumOfSquaredDifferences.divide(new BigDecimal(values.size()), DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 计算标准差
     */
    private BigDecimal calculateStandardDeviation(List<Object> values) {
        if (values.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal variance = calculateVariance(values);
        return new BigDecimal(Math.sqrt(variance.doubleValue()))
            .setScale(DECIMAL_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 将对象转换为BigDecimal
     */
    private BigDecimal toNumber(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }

        if (value instanceof Number) {
            return new BigDecimal(value.toString());
        }

        try {
            return new BigDecimal(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to convert value to number: {}", value);
            return BigDecimal.ZERO;
        }
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
            BigDecimal num1 = toNumber(value1);
            BigDecimal num2 = toNumber(value2);
            return num1.compareTo(num2);
        } catch (Exception e) {
            // 默认使用字符串比较
            return value1.toString().compareTo(value2.toString());
        }
    }
}
