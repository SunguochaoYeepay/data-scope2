package com.datascope.domain.query.service;

import com.datascope.domain.query.model.QueryExecution;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 查询执行服务接口
 */
public interface QueryExecutionService {
    /**
     * 执行SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param parameters   查询参数
     * @return 查询执行记录
     */
    QueryExecution executeSql(String dataSourceId, String sql, Map<String, Object> parameters);

    /**
     * 执行自然语言查询
     *
     * @param dataSourceId 数据源ID
     * @param text         自然语言文本
     * @return 查询执行记录
     */
    QueryExecution executeNaturalLanguage(String dataSourceId, String text);

    /**
     * 获取查询执行记录
     *
     * @param id 查询执行记录ID
     * @return 查询执行记录
     */
    Optional<QueryExecution> getById(String id);

    /**
     * 获取用户最近的查询记录
     *
     * @param userId 用户ID
     * @param limit  返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> getRecentByUserId(String userId, int limit);

    /**
     * 取消查询执行
     *
     * @param id 查询执行记录ID
     */
    void cancel(String id);

    /**
     * 导出查询结果
     *
     * @param id     查询执行记录ID
     * @param format 导出格式
     * @return 导出文件路径
     */
    String exportResult(String id, String format);
}
