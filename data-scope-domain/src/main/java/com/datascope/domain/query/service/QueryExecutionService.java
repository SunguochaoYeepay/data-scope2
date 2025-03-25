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
     * 执行分页SQL查询，支持排序
     *
     * @param dataSourceId 数据源ID
     * @param sql          SQL语句
     * @param parameters   查询参数
     * @param pageNumber   页码（从1开始）
     * @param pageSize     每页大小
     * @param sortFields   排序字段列表
     * @return 分页查询执行记录
     */
    QueryExecution executePagedSql(String dataSourceId, String sql, Map<String, Object> parameters,
                                   int pageNumber, int pageSize, List<QueryResultSortService.SortField> sortFields);

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

    /**
     * 获取过滤后的查询结果
     *
     * @param id          查询执行记录ID
     * @param filterGroup 过滤条件组
     * @return 过滤后的查询结果
     */
    QueryResult getFilteredResult(String id, QueryResultFilterService.FilterGroup filterGroup);

    /**
     * 获取过滤后的查询结果（单个条件）
     *
     * @param id        查询执行记录ID
     * @param condition 过滤条件
     * @return 过滤后的查询结果
     */
    QueryResult getFilteredResult(String id, QueryResultFilterService.FilterCondition condition);

    /**
     * 获取过滤后的分页查询结果
     *
     * @param id          查询执行记录ID
     * @param pageNumber  页码（从1开始）
     * @param pageSize    每页大小
     * @param filterGroup 过滤条件组
     * @return 过滤后的分页查询结果
     */
    PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, QueryResultFilterService.FilterGroup filterGroup);

    /**
     * 获取过滤后的分页查询结果（单个条件）
     *
     * @param id         查询执行记录ID
     * @param pageNumber 页码（从1开始）
     * @param pageSize   每页大小
     * @param condition  过滤条件
     * @return 过滤后的分页查询结果
     */
    PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, QueryResultFilterService.FilterCondition condition);

    /**
     * 获取过滤并排序后的查询结果
     *
     * @param id          查询执行记录ID
     * @param filterGroup 过滤条件组
     * @param sortFields  排序字段列表
     * @return 过滤并排序后的查询结果
     */
    QueryResult getFilteredAndSortedResult(String id, QueryResultFilterService.FilterGroup filterGroup, List<QueryResultSortService.SortField> sortFields);

    /**
     * 获取过滤并排序后的分页查询结果
     *
     * @param id          查询执行记录ID
     * @param pageNumber  页码（从1开始）
     * @param pageSize    每页大小
     * @param filterGroup 过滤条件组
     * @param sortFields  排序字段列表
     * @return 过滤并排序后的分页查询结果
     */
    PagedQueryResult getFilteredAndSortedPagedResult(String id, int pageNumber, int pageSize, QueryResultFilterService.FilterGroup filterGroup, List<QueryResultSortService.SortField> sortFields);

    /**
     * 计算查询结果的统计信息
     *
     * @param id        查询执行记录ID
     * @param fieldName 字段名
     * @param function  统计函数
     * @return 统计结果
     */
    QueryResultStatisticsService.StatisticsResult calculateStatistics(String id, String fieldName, QueryResultStatisticsService.StatisticsFunction function);

    /**
     * 计算查询结果的多项统计信息
     *
     * @param id        查询执行记录ID
     * @param fieldName 字段名
     * @param functions 统计函数列表
     * @return 统计结果列表
     */
    List<QueryResultStatisticsService.StatisticsResult> calculateStatistics(String id, String fieldName, List<QueryResultStatisticsService.StatisticsFunction> functions);

    /**
     * 计算查询结果的基本统计信息（计数、总和、平均值、最小值、最大值）
     *
     * @param id        查询执行记录ID
     * @param fieldName 字段名
     * @return 基本统计信息
     */
    List<QueryResultStatisticsService.StatisticsResult> calculateBasicStatistics(String id, String fieldName);

    /**
     * 计算查询结果的完整统计信息
     *
     * @param id        查询执行记录ID
     * @param fieldName 字段名
     * @return 完整统计信息
     */
    List<QueryResultStatisticsService.StatisticsResult> calculateFullStatistics(String id, String fieldName);

    /**
     * 计算过滤后查询结果的统计信息
     *
     * @param id          查询执行记录ID
     * @param filterGroup 过滤条件组
     * @param fieldName   字段名
     * @param function    统计函数
     * @return 统计结果
     */
    QueryResultStatisticsService.StatisticsResult calculateFilteredStatistics(String id, QueryResultFilterService.FilterGroup filterGroup, String fieldName, QueryResultStatisticsService.StatisticsFunction function);
}
