package com.datascope.domain.query.service;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.QueryResult;

import java.util.Map;

/**
 * SQL执行引擎接口
 */
public interface SqlExecutionEngine {

    /**
     * 执行SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param parameters   查询参数
     * @return 查询结果
     */
    QueryResult execute(DataSourceId dataSourceId, String sql, Map<String, Object> parameters);

    /**
     * 取消正在执行的查询
     *
     * @param executionId 执行ID
     */
    void cancel(String executionId);

    /**
     * 验证SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @return 是否有效
     */
    boolean validate(DataSourceId dataSourceId, String sql);

    /**
     * 获取SQL查询的元数据
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @return 元数据
     */
    Object getMetadata(DataSourceId dataSourceId, String sql);

    /**
     * 估算查询结果行数
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @return 估算的行数
     */
    long estimateRowCount(DataSourceId dataSourceId, String sql);
}
