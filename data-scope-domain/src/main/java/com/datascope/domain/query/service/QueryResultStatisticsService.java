package com.datascope.domain.query.service;

import com.datascope.domain.query.model.QueryResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 查询结果统计服务接口
 */
public interface QueryResultStatisticsService {

    /**
     * 对查询结果进行统计
     *
     * @param result    查询结果
     * @param fieldName 字段名
     * @param function  统计函数
     * @return 统计结果
     */
    StatisticsResult calculate(QueryResult result, String fieldName, StatisticsFunction function);

    /**
     * 对查询结果进行多项统计
     *
     * @param result    查询结果
     * @param fieldName 字段名
     * @param functions 统计函数列表
     * @return 统计结果列表
     */
    List<StatisticsResult> calculate(QueryResult result, String fieldName, List<StatisticsFunction> functions);

    /**
     * 对查询结果进行分组统计
     *
     * @param result       查询结果
     * @param groupByField 分组字段
     * @param fieldName    统计字段
     * @param function     统计函数
     * @return 分组统计结果，键为分组值，值为统计结果
     */
    Map<Object, StatisticsResult> calculateGroupBy(QueryResult result, String groupByField, String fieldName, StatisticsFunction function);

    /**
     * 对查询结果进行多项分组统计
     *
     * @param result       查询结果
     * @param groupByField 分组字段
     * @param fieldName    统计字段
     * @param functions    统计函数列表
     * @return 分组统计结果，键为分组值，值为统计结果列表
     */
    Map<Object, List<StatisticsResult>> calculateGroupBy(QueryResult result, String groupByField, String fieldName, List<StatisticsFunction> functions);

    /**
     * 计算数值字段的基本统计信息（计数、总和、平均值、最小值、最大值）
     *
     * @param result    查询结果
     * @param fieldName 字段名
     * @return 基本统计信息
     */
    List<StatisticsResult> calculateBasicStatistics(QueryResult result, String fieldName);

    /**
     * 计算数值字段的高级统计信息（中位数、众数、方差、标准差）
     *
     * @param result    查询结果
     * @param fieldName 字段名
     * @return 高级统计信息
     */
    List<StatisticsResult> calculateAdvancedStatistics(QueryResult result, String fieldName);

    /**
     * 计算数值字段的完整统计信息（基本 + 高级）
     *
     * @param result    查询结果
     * @param fieldName 字段名
     * @return 完整统计信息
     */
    List<StatisticsResult> calculateFullStatistics(QueryResult result, String fieldName);

    /**
     * 统计函数枚举
     */
    enum StatisticsFunction {
        COUNT,      // 计数
        SUM,        // 求和
        AVG,        // 平均值
        MIN,        // 最小值
        MAX,        // 最大值
        MEDIAN,     // 中位数
        MODE,       // 众数
        VARIANCE,   // 方差
        STDDEV      // 标准差
    }

    /**
     * 统计结果
     */
    class StatisticsResult {
        private final String fieldName;
        private final StatisticsFunction function;
        private final Object value;

        public StatisticsResult(String fieldName, StatisticsFunction function, Object value) {
            this.fieldName = fieldName;
            this.function = function;
            this.value = value;
        }

        public String getFieldName() {
            return fieldName;
        }

        public StatisticsFunction getFunction() {
            return function;
        }

        public Object getValue() {
            return value;
        }

        public BigDecimal getNumericValue() {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            throw new IllegalStateException("Statistics result is not numeric: " + value);
        }

        @Override
        public String toString() {
            return function + "(" + fieldName + ") = " + value;
        }
    }
}
