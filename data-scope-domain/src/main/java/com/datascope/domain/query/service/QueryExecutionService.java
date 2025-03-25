package com.datascope.domain.query.service;

import com.datascope.domain.query.model.PagedQueryResult;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultFilterService.FilterCondition;
import com.datascope.domain.query.service.QueryResultFilterService.FilterGroup;
import com.datascope.domain.query.service.QueryResultSortService.SortDirection;
import com.datascope.domain.query.service.QueryResultSortService.SortField;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsFunction;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsResult;

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
     * @param sql SQL查询语句
     * @param parameters 查询参数
     * @return 查询执行结果
     */
    QueryExecution executeSql(String dataSourceId, String sql, Map<String, Object> parameters);

    /**
     * 执行自然语言查询
     *
     * @param dataSourceId 数据源ID
     * @param text 自然语言文本
     * @return 查询执行结果
     */
    QueryExecution executeNaturalLanguage(String dataSourceId, String text);

    /**
     * 执行分页SQL查询
     *
     * @param dataSourceId 数据源ID
     * @param sql SQL查询语句
     * @param parameters 查询参数
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @return 查询执行结果
     */
    QueryExecution executePagedSql(String dataSourceId, String sql, Map<String, Object> parameters, int pageNumber, int pageSize);

    /**
     * 执行分页SQL查询（带排序）
     *
     * @param dataSourceId 数据源ID
     * @param sql SQL查询语句
     * @param parameters 查询参数
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param sortFields 排序字段
     * @return 查询执行结果
     */
    QueryExecution executePagedSql(String dataSourceId, String sql, Map<String, Object> parameters, int pageNumber, int pageSize, List<SortField> sortFields);

    /**
     * 根据ID获取查询执行
     *
     * @param id 查询执行ID
     * @return 查询执行
     */
    Optional<QueryExecution> getById(String id);

    /**
     * 获取用户最近的查询执行
     *
     * @param userId 用户ID
     * @param limit  限制数量
     * @return 查询执行列表
     */
    List<QueryExecution> getRecentByUserId(String userId, int limit);

    /**
     * 取消正在执行的查询
     *
     * @param executionId 查询执行ID
     */
    void cancel(String executionId);

    /**
     * 导出查询结果
     *
     * @param id     查询执行ID
     * @param format 导出格式
     * @return 导出文件路径
     */
    String exportResult(String id, String format);

    /**
     * 获取分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @return 分页查询结果
     */
    PagedQueryResult getPagedResult(String id, int pageNumber, int pageSize);

    /**
     * 获取排序查询结果
     *
     * @param id 查询执行ID
     * @param sortFields 排序字段
     * @return 排序查询结果
     */
    QueryResult getSortedResult(String id, List<SortField> sortFields);

    /**
     * 获取排序查询结果
     *
     * @param id 查询执行ID
     * @param fieldName 字段名
     * @param direction 排序方向
     * @return 排序查询结果
     */
    QueryResult getSortedResult(String id, String fieldName, SortDirection direction);

    /**
     * 获取排序分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param sortFields 排序字段
     * @return 排序分页查询结果
     */
    PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, List<SortField> sortFields);

    /**
     * 获取排序分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param fieldName 字段名
     * @param direction 排序方向
     * @return 排序分页查询结果
     */
    PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, String fieldName, SortDirection direction);

    /**
     * 缓存查询结果
     *
     * @param id 查询执行ID
     * @param ttlSeconds 缓存时间（秒）
     * @return 是否成功
     */
    boolean cacheResult(String id, int ttlSeconds);

    /**
     * 从缓存中获取查询结果
     *
     * @param id 查询执行ID
     * @return 查询结果
     */
    Optional<QueryResult> getResultFromCache(String id);

    /**
     * 移除缓存的查询结果
     *
     * @param id 查询执行ID
     */
    void removeCachedResult(String id);

    /**
     * 获取过滤查询结果
     *
     * @param id 查询执行ID
     * @param filterGroup 过滤组
     * @return 过滤查询结果
     */
    QueryResult getFilteredResult(String id, FilterGroup filterGroup);

    /**
     * 获取过滤查询结果
     *
     * @param id 查询执行ID
     * @param condition 过滤条件
     * @return 过滤查询结果
     */
    QueryResult getFilteredResult(String id, FilterCondition condition);

    /**
     * 获取过滤分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param filterGroup 过滤组
     * @return 过滤分页查询结果
     */
    PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, FilterGroup filterGroup);

    /**
     * 获取过滤分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param condition 过滤条件
     * @return 过滤分页查询结果
     */
    PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, FilterCondition condition);

    /**
     * 获取过滤排序查询结果
     *
     * @param id 查询执行ID
     * @param filterGroup 过滤组
     * @param sortFields 排序字段
     * @return 过滤排序查询结果
     */
    QueryResult getFilteredAndSortedResult(String id, FilterGroup filterGroup, List<SortField> sortFields);

    /**
     * 获取过滤排序分页查询结果
     *
     * @param id 查询执行ID
     * @param pageNumber 页码
     * @param pageSize 每页大小
     * @param filterGroup 过滤组
     * @param sortFields 排序字段
     * @return 过滤排序分页查询结果
     */
    PagedQueryResult getFilteredAndSortedPagedResult(String id, int pageNumber, int pageSize, FilterGroup filterGroup, List<SortField> sortFields);

    /**
     * 计算基本统计信息
     *
     * @param id 查询执行ID
     * @param fieldName 字段名
     * @return 统计结果列表
     */
    List<StatisticsResult> calculateBasicStatistics(String id, String fieldName);

    /**
     * 计算完整统计信息
     *
     * @param id 查询执行ID
     * @param fieldName 字段名
     * @return 统计结果列表
     */
    List<StatisticsResult> calculateFullStatistics(String id, String fieldName);

    /**
     * 计算统计信息
     *
     * @param id 查询执行ID
     * @param fieldName 字段名
     * @param function 统计函数
     * @return 统计结果
     */
    StatisticsResult calculateStatistics(String id, String fieldName, StatisticsFunction function);

    /**
     * 计算多个统计信息
     *
     * @param id 查询执行ID
     * @param fieldName 字段名
     * @param functions 统计函数列表
     * @return 统计结果列表
     */
    List<StatisticsResult> calculateStatistics(String id, String fieldName, List<StatisticsFunction> functions);

    /**
     * 计算过滤后的统计信息
     *
     * @param id 查询执行ID
     * @param filterGroup 过滤组
     * @param fieldName 字段名
     * @param function 统计函数
     * @return 统计结果
     */
    StatisticsResult calculateFilteredStatistics(String id, FilterGroup filterGroup, String fieldName, StatisticsFunction function);
}
