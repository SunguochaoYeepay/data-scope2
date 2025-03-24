package com.datascope.domain.query.service;

import com.datascope.domain.query.model.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * 查询结果过滤服务接口
 */
public interface QueryResultFilterService {

    /**
     * 对查询结果进行过滤
     *
     * @param result      原始查询结果
     * @param filterGroup 过滤条件组
     * @return 过滤后的查询结果
     */
    QueryResult filter(QueryResult result, FilterGroup filterGroup);

    /**
     * 对查询结果进行过滤（单个条件）
     *
     * @param result    原始查询结果
     * @param condition 过滤条件
     * @return 过滤后的查询结果
     */
    default QueryResult filter(QueryResult result, FilterCondition condition) {
        return filter(result, new FilterGroup(List.of(condition), FilterLogic.AND));
    }

    /**
     * 对查询结果进行过滤（多个条件，默认AND逻辑）
     *
     * @param result     原始查询结果
     * @param conditions 过滤条件列表
     * @return 过滤后的查询结果
     */
    default QueryResult filter(QueryResult result, List<FilterCondition> conditions) {
        return filter(result, new FilterGroup(conditions, FilterLogic.AND));
    }

    /**
     * 对数据行列表进行过滤
     *
     * @param rows        数据行列表
     * @param filterGroup 过滤条件组
     * @return 过滤后的数据行列表
     */
    List<Map<String, Object>> filterRows(List<Map<String, Object>> rows, FilterGroup filterGroup);

    /**
     * 对数据行列表进行过滤（单个条件）
     *
     * @param rows      数据行列表
     * @param condition 过滤条件
     * @return 过滤后的数据行列表
     */
    default List<Map<String, Object>> filterRows(List<Map<String, Object>> rows, FilterCondition condition) {
        return filterRows(rows, new FilterGroup(List.of(condition), FilterLogic.AND));
    }

    /**
     * 对数据行列表进行过滤（多个条件，默认AND逻辑）
     *
     * @param rows       数据行列表
     * @param conditions 过滤条件列表
     * @return 过滤后的数据行列表
     */
    default List<Map<String, Object>> filterRows(List<Map<String, Object>> rows, List<FilterCondition> conditions) {
        return filterRows(rows, new FilterGroup(conditions, FilterLogic.AND));
    }

    /**
     * 创建行过滤谓词
     *
     * @param filterGroup 过滤条件组
     * @return 行过滤谓词
     */
    Predicate<Map<String, Object>> createRowPredicate(FilterGroup filterGroup);

    /**
     * 过滤条件类型枚举
     */
    enum FilterOperator {
        EQUALS,             // 等于
        NOT_EQUALS,         // 不等于
        GREATER_THAN,       // 大于
        GREATER_THAN_EQUALS, // 大于等于
        LESS_THAN,          // 小于
        LESS_THAN_EQUALS,   // 小于等于
        CONTAINS,           // 包含
        NOT_CONTAINS,       // 不包含
        STARTS_WITH,        // 以...开始
        ENDS_WITH,          // 以...结束
        IS_NULL,            // 为空
        IS_NOT_NULL,        // 不为空
        IN,                 // 在列表中
        NOT_IN              // 不在列表中
    }

    /**
     * 过滤条件组合逻辑枚举
     */
    enum FilterLogic {
        AND,    // 与
        OR      // 或
    }

    /**
     * 过滤条件定义
     */
    class FilterCondition {
        private final String fieldName;
        private final FilterOperator operator;
        private final Object value;

        public FilterCondition(String fieldName, FilterOperator operator, Object value) {
            this.fieldName = fieldName;
            this.operator = operator;
            this.value = value;
        }

        public FilterCondition(String fieldName, FilterOperator operator) {
            this(fieldName, operator, null);
        }

        public String getFieldName() {
            return fieldName;
        }

        public FilterOperator getOperator() {
            return operator;
        }

        public Object getValue() {
            return value;
        }
    }

    /**
     * 过滤条件组
     */
    class FilterGroup {
        private final List<FilterCondition> conditions;
        private final FilterLogic logic;

        public FilterGroup(List<FilterCondition> conditions, FilterLogic logic) {
            this.conditions = conditions;
            this.logic = logic;
        }

        public List<FilterCondition> getConditions() {
            return conditions;
        }

        public FilterLogic getLogic() {
            return logic;
        }
    }
}
