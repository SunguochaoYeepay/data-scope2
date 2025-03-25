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
     * 列定义
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
     * 执行时间（毫秒）
     */
    private long executionTime;

    /**
     * 是否有更多数据
     */
    private boolean hasMore;

    /**
     * 查询ID
     */
    private String queryId;

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * SQL语句
     */
    private String sql;

    /**
     * 查询参数
     */
    private Map<String, Object> parameters;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 是否成功
     */
    private boolean success = true;
}
