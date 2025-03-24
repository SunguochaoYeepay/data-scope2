package com.datascope.domain.query.service;

import com.datascope.domain.query.model.PagedQueryResult;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;

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

    /**
     * 执行分页SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param parameters   查询参数
     * @param pageNumber   页码（从1开始）
     * @param pageSize     每页大小
     * @return 分页查询执行记录
     */
    QueryExecution executePagedSql(String dataSourceId, String sql, Map<String, Object> parameters, int pageNumber, int pageSize);

    /**
     * 获取分页查询结果
     *
     * @param id         查询执行记录ID
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页大小
     * @return 分页查询结果
     */
    PagedQueryResult getPagedResult(String id, int pageNumber, int pageSize);

    /**
     * 获取排序后的查询结果
     *
     * @param id         查询执行记录ID
     * @param sortFields 排序字段列表
     * @return 排序后的查询结果
     */
    QueryResult getSortedResult(String id, List<QueryResultSortService.SortField> sortFields);

    /**
     * 获取排序后的查询结果（单字段排序）
     *
     * @param id        查询执行记录ID
     * @param fieldName 排序字段名
     * @param direction 排序方向
     * @return 排序后的查询结果
     */
    QueryResult getSortedResult(String id, String fieldName, QueryResultSortService.SortDirection direction);

    /**
     * 获取排序后的分页查询结果
     *
     * @param id         查询执行记录ID
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页大小
     * @param sortFields 排序字段列表
     * @return 排序后的分页查询结果
     */
    PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, List<QueryResultSortService.SortField> sortFields);

    /**
     * 获取排序后的分页查询结果（单字段排序）
     *
     * @param id         查询执行记录ID
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页大小
     * @param fieldName  排序字段名
     * @param direction  排序方向
     * @return 排序后的分页查询结果
     */
    PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, String fieldName, QueryResultSortService.SortDirection direction);

    /**
     * 将查询结果缓存
     *
     * @param id         查询执行记录ID
     * @param ttlSeconds 缓存过期时间（秒）
     * @return 是否缓存成功
     */
    boolean cacheResult(String id, int ttlSeconds);

    /**
     * 从缓存中获取查询结果
     *
     * @param id 查询执行记录ID
     * @return 查询结果（如果存在）
     */
    Optional<QueryResult> getResultFromCache(String id);

    /**
     * 从缓存中删除查询结果
     *
     * @param id 查询执行记录ID
     */
    void removeCachedResult(String id);
}
