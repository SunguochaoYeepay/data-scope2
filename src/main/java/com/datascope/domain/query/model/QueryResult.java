package com.datascope.domain.query.model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 查询结果
 */
@Getter
@Builder
public class QueryResult {
    /**
     * 列定义
     */
    private final List<ColumnDefinition> columns;

    /**
     * 数据行
     */
    private final List<Map<String, Object>> rows;

    /**
     * 总行数
     */
    private final long totalRows;

    /**
     * 是否有更多数据
     */
    private final boolean hasMore;

    /**
     * 执行时间（毫秒）
     */
    private final long executionTime;

    /**
     * 列定义
     */
    @Getter
    @Builder
    public static class ColumnDefinition {
        /**
         * 列名
         */
        private final String name;

        /**
         * 列标签
         */
        private final String label;

        /**
         * 数据类型
         */
        private final String dataType;

        /**
         * 是否可为空
         */
        private final boolean nullable;

        /**
         * 是否自增
         */
        private final boolean autoIncrement;

        /**
         * 是否主键
         */
        private final boolean primaryKey;
    }
}