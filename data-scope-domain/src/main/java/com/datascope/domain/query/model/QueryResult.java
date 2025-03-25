package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 查询结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {

    /**
     * 列信息
     */
    private List<ColumnDefinition> columns;

    /**
     * 数据行
     */
    private List<Map<String, Object>> rows;

    /**
     * 总行数
     */
    private long totalRows;

    /**
     * 是否有更多数据
     */
    private boolean hasMore;

    /**
     * 执行时间（毫秒）
     */
    private long executionTime;

    /**
     * 查询ID
     */
    private String queryId;

    // 移除内部类 ColumnInfo，使用外部的 ColumnDefinition 类
}
