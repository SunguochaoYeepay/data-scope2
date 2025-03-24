package com.datascope.domain.query.service.impl;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.PagedQueryResult;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.domain.query.service.*;
import com.datascope.domain.query.service.QueryResultFilterService.FilterCondition;
import com.datascope.domain.query.service.QueryResultFilterService.FilterGroup;
import com.datascope.domain.query.service.QueryResultFilterService.FilterLogic;
import com.datascope.domain.query.service.QueryResultSortService.SortDirection;
import com.datascope.domain.query.service.QueryResultSortService.SortField;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsFunction;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsResult;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 查询执行服务实现
 */
@Service
@RequiredArgsConstructor
public class QueryExecutionServiceImpl implements QueryExecutionService {

    private static final Logger log = LoggerFactory.getLogger(QueryExecutionServiceImpl.class);

    private final QueryExecutionRepository queryExecutionRepository;
    private final SqlExecutionEngine sqlExecutionEngine;
    private final QueryResultCacheService queryResultCacheService;
    private final QueryResultSortService queryResultSortService;
    private final QueryResultFilterService queryResultFilterService;
    private final QueryResultStatisticsService queryResultStatisticsService;

    // 查询超时时间（秒）
    private static final int QUERY_TIMEOUT_SECONDS = 60;

    // 调度器用于处理查询超时
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Override
    @Transactional
    public QueryExecution executeSql(String dataSourceId, String sql, Map<String, Object> parameters) {
        log.info("Executing SQL query on dataSource: {}, SQL: {}", dataSourceId, sql);

        // 创建查询执行记录
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            sql,
            parameters
        );

        // 保存执行记录
        execution = queryExecutionRepository.save(execution);

        // 创建查询超时任务
        ScheduledFuture<?> timeoutTask = null;

        try {
            // 标记为执行中
            execution.markAsStarted();

            // 设置查询超时任务
            final String executionId = execution.getId();
            timeoutTask = scheduleQueryTimeout(executionId);

            // 验证SQL语句
            boolean isValid = sqlExecutionEngine.validate(DataSourceId.of(dataSourceId), sql);
            if (!isValid) {
                throw new IllegalArgumentException("SQL validation failed: " + sql);
            }

            // 实际执行SQL查询
            QueryResult queryResult = sqlExecutionEngine.execute(
                DataSourceId.of(dataSourceId),
                sql,
                parameters
            );

            // 更新执行记录
            execution.markAsCompleted(queryResult.getTotalRows());
            execution.setResult(queryResult);

            log.info("SQL execution completed successfully, rows: {}", queryResult.getTotalRows());

        } catch (Exception e) {
            log.error("SQL execution failed: {}", e.getMessage(), e);
            execution.markAsFailed(e.getMessage());
        } finally {
            // 取消超时任务
            if (timeoutTask != null) {
                timeoutTask.cancel(false);
            }
        }

        return queryExecutionRepository.save(execution);
    }

    /**
     * 调度查询超时任务
     *
     * @param executionId 查询执行ID
     * @return 调度任务
     */
    private ScheduledFuture<?> scheduleQueryTimeout(String executionId) {
        return scheduler.schedule(() -> {
            log.warn("Query execution timed out: {}", executionId);
            try {
                cancel(executionId);
            } catch (Exception e) {
                log.error("Failed to cancel timed out query: {}", executionId, e);
            }
        }, QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @Override
    @Transactional
    public QueryExecution executeNaturalLanguage(String dataSourceId, String text) {
        log.info("Processing natural language query: {}", text);

        // 创建查询执行记录
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "NATURAL_LANGUAGE: " + text,
            Map.of()
        );

        // 保存执行记录
        execution = queryExecutionRepository.save(execution);

        // 确保执行记录不为null
        if (execution == null) {
            log.error("Failed to save query execution record");
            throw new IllegalStateException("Failed to save query execution record");
        }

        // 创建查询超时任务
        ScheduledFuture<?> timeoutTask = null;

        try {
            // 标记为执行中
            execution.markAsStarted();

            // 将自然语言转换为SQL
            String sql = convertNaturalLanguageToSql(dataSourceId, text);
            log.info("Converted natural language to SQL: {}", sql);

            // 验证生成的SQL
            boolean isValid = sqlExecutionEngine.validate(DataSourceId.of(dataSourceId), sql);
            if (!isValid) {
                throw new IllegalArgumentException("Generated SQL is not valid: " + sql);
            }

            // 执行SQL查询
            QueryResult queryResult = sqlExecutionEngine.execute(
                DataSourceId.of(dataSourceId),
                sql,
                Map.of()
            );

            // 更新执行记录
            execution.markAsCompleted(queryResult.getTotalRows());
            execution.setResult(queryResult);
            execution.setSql(sql); // 更新为实际执行的SQL

        } catch (Exception e) {
            log.error("Natural language query execution failed", e);
            execution.markAsFailed(e.getMessage());
        }

        return queryExecutionRepository.save(execution);
    }

    private String convertNaturalLanguageToSql(String dataSourceId, String text) {
        // 这是一个简单的实现，实际项目中需要集成LLM服务
        // 临时返回一个简单的SQL查询
        return "SELECT 1 AS result";
    }

    @Override
    public Optional<QueryExecution> getById(String id) {
        return queryExecutionRepository.findById(id);
    }

    @Override
    public List<QueryExecution> getRecentByUserId(String userId, int limit) {
        return queryExecutionRepository.findRecentByUserId(userId, limit);
    }

    @Override
    @Transactional
    public void cancel(String id) {
        if (id == null || id.isEmpty()) {
            log.warn("Cannot cancel query execution: ID is null or empty");
            return;
        }

        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            log.warn("Cannot cancel query execution: {} (not found)", id);
            return;
        }

        QueryExecution queryExecution = executionOpt.get();
        if (!queryExecution.isRunning()) {
            log.warn("Cannot cancel query execution: {} (not running, current status: {})", id, queryExecution.getStatus());
            return;
        }

        try {
            // 实际取消查询执行
            sqlExecutionEngine.cancel(queryExecution.getId());

            // 更新执行记录状态
            queryExecution.markAsCancelled();
            queryExecutionRepository.save(queryExecution);

            log.info("Query execution cancelled: {}", id);
        } catch (Exception e) {
            log.error("Failed to cancel query execution: {}", id, e);
            queryExecution.markAsFailed("Failed to cancel query: " + e.getMessage());
            queryExecutionRepository.save(queryExecution);
        }
    }

    @Override
    public String exportResult(String id, String format) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Query execution ID cannot be null or empty");
        }

        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Export format cannot be null or empty");
        }

        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution queryExecution = executionOpt.get();

        // 检查查询是否已完成
        if (!queryExecution.isCompleted()) {
            throw new IllegalStateException("Cannot export results for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (queryExecution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results to export: " + id);
        }

        // 根据格式导出结果
        String exportPath = "/tmp/export/" + id + "." + format.toLowerCase();

        try {
            switch (format.toLowerCase()) {
                case "csv":
                    exportToCsv(queryExecution.getResult(), exportPath);
                    break;
                case "json":
                    exportToJson(queryExecution.getResult(), exportPath);
                    break;
                case "excel":
                    exportToExcel(queryExecution.getResult(), exportPath);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported export format: " + format);
            }

            log.info("Query results exported to {}", exportPath);
            return exportPath;
        } catch (Exception e) {
            log.error("Failed to export query results: {}", id, e);
            throw new RuntimeException("Failed to export query results: " + e.getMessage(), e);
        }
    }

    private void exportToCsv(QueryResult result, String filePath) {
        // 简单实现，实际项目中需要更完善的CSV导出逻辑
        log.info("CSV export to: {}", filePath);
    }

    private void exportToJson(QueryResult result, String filePath) {
        // 简单实现，实际项目中需要更完善的JSON导出逻辑
        log.info("JSON export to: {}", filePath);
    }

    private void exportToExcel(QueryResult result, String filePath) {
        // 简单实现，实际项目中需要更完善的Excel导出逻辑
        log.info("Excel export to: {}", filePath);
    }

    @Override
    @Transactional
    public QueryExecution executePagedSql(String dataSourceId, String sql, Map<String, Object> parameters, int pageNumber, int pageSize) {
        log.info("Executing paged SQL query on data source {}: {}", dataSourceId, sql);

        // 验证参数
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than or equal to 1");
        }

        // 创建查询执行记录
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            sql,
            parameters
        );

        // 添加分页信息到参数中
        Map<String, Object> pagedParameters = parameters != null ? parameters : Map.of();
        pagedParameters.put("_page_number", pageNumber);
        pagedParameters.put("_page_size", pageSize);
        pagedParameters.put("_offset", (pageNumber - 1) * pageSize);
        pagedParameters.put("_limit", pageSize);

        // 保存查询执行记录
        execution = queryExecutionRepository.save(execution);

        // 确保执行记录不为null
        if (execution == null) {
            log.error("Failed to save query execution record for paged SQL query");
            throw new IllegalStateException("Failed to save query execution record");
        }

        // 验证SQL
        boolean isValid = sqlExecutionEngine.validate(execution.getDataSourceId(), sql);
        if (!isValid) {
            execution.markAsFailed("Invalid SQL query");
            queryExecutionRepository.save(execution);
            return execution;
        }

        // 执行查询
        try {
            execution.markAsStarted();

            // 设置查询超时
            ScheduledFuture<?> timeoutFuture = null;
            if (QUERY_TIMEOUT_SECONDS > 0) {
                final String executionId = execution.getId(); // 捕获final变量用于lambda
                timeoutFuture = scheduler.schedule(() -> {
                    try {
                        cancel(executionId);
                        log.warn("Query execution timed out after {} seconds: {}", QUERY_TIMEOUT_SECONDS, executionId);
                    } catch (Exception e) {
                        log.error("Failed to cancel timed out query: {}", executionId, e);
                    }
                }, QUERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            }

            // 执行查询
            QueryResult result = sqlExecutionEngine.execute(execution.getDataSourceId(), sql, pagedParameters);

            // 取消超时任务
            if (timeoutFuture != null) {
                timeoutFuture.cancel(false);
            }

            // 更新查询执行记录
            execution.markAsCompleted(result.getTotalRows());
            execution.setResult(result);

            log.info("Paged SQL query executed successfully: {}", execution.getId());
        } catch (Exception e) {
            log.error("Failed to execute paged SQL query: {}", execution.getId(), e);
            execution.markAsFailed(e.getMessage());
        }

        // 保存更新后的查询执行记录
        return queryExecutionRepository.save(execution);
    }

    @Override
    public PagedQueryResult getPagedResult(String id, int pageNumber, int pageSize) {
        log.info("Getting paged result for query execution {}, page {}, size {}", id, pageNumber, pageSize);

        // 验证参数
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than or equal to 1");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot get paged result for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 创建分页结果
        PagedQueryResult pagedResult = PagedQueryResult.fromQueryResult(execution.getResult(), pageNumber, pageSize);

        log.info("Paged result created for query execution {}", id);
        return pagedResult;
    }

    @Override
    public QueryResult getSortedResult(String id, List<SortField> sortFields) {
        log.info("Getting sorted result for query execution {}", id);

        if (sortFields == null || sortFields.isEmpty()) {
            throw new IllegalArgumentException("Sort fields cannot be null or empty");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot get sorted result for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 对结果进行排序
        QueryResult sortedResult = queryResultSortService.sort(execution.getResult(), sortFields);

        log.info("Sorted result created for query execution {}", id);
        return sortedResult;
    }

    @Override
    public QueryResult getSortedResult(String id, String fieldName, SortDirection direction) {
        return getSortedResult(id, List.of(new SortField(fieldName, direction)));
    }

    @Override
    public PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, List<SortField> sortFields) {
        log.info("Getting sorted paged result for query execution {}, page {}, size {}", id, pageNumber, pageSize);

        // 验证参数
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than or equal to 1");
        }
        if (sortFields == null || sortFields.isEmpty()) {
            throw new IllegalArgumentException("Sort fields cannot be null or empty");
        }

        // 获取排序后的结果
        QueryResult sortedResult = getSortedResult(id, sortFields);

        // 创建分页结果
        PagedQueryResult pagedResult = PagedQueryResult.fromQueryResult(sortedResult, pageNumber, pageSize);

        log.info("Sorted paged result created for query execution {}", id);
        return pagedResult;
    }

    @Override
    public PagedQueryResult getSortedPagedResult(String id, int pageNumber, int pageSize, String fieldName, SortDirection direction) {
        return getSortedPagedResult(id, pageNumber, pageSize, List.of(new SortField(fieldName, direction)));
    }

    @Override
    public boolean cacheResult(String id, int ttlSeconds) {
        log.info("Caching result for query execution {}, TTL: {} seconds", id, ttlSeconds);

        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be greater than 0");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            log.warn("Cannot cache result: query execution not found: {}", id);
            return false;
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            log.warn("Cannot cache result: query execution is not completed: {}", id);
            return false;
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            log.warn("Cannot cache result: query execution has no results: {}", id);
            return false;
        }

        // 缓存结果
        queryResultCacheService.cacheResult(id, execution.getResult(), ttlSeconds);

        log.info("Result cached for query execution {}", id);
        return true;
    }

    @Override
    public Optional<QueryResult> getResultFromCache(String id) {
        return queryResultCacheService.getResult(id);
    }

    @Override
    public void removeCachedResult(String id) {
        queryResultCacheService.removeResult(id);
    }

    @Override
    public QueryResult getFilteredResult(String id, FilterGroup filterGroup) {
        log.info("Getting filtered result for query execution {}", id);

        if (filterGroup == null) {
            throw new IllegalArgumentException("Filter group cannot be null");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot get filtered result for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 对结果进行过滤
        QueryResult filteredResult = queryResultFilterService.filter(execution.getResult(), filterGroup);

        log.info("Filtered result created for query execution {}", id);
        return filteredResult;
    }

    @Override
    public QueryResult getFilteredResult(String id, FilterCondition condition) {
        FilterGroup group = new FilterGroup(List.of(condition), FilterLogic.AND);
        return getFilteredResult(id, group);
    }

    @Override
    public PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, FilterGroup filterGroup) {
        log.info("Getting filtered paged result for query execution {}, page {}, size {}", id, pageNumber, pageSize);

        // 验证参数
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than or equal to 1");
        }
        if (filterGroup == null) {
            throw new IllegalArgumentException("Filter group cannot be null");
        }

        // 获取过滤后的结果
        QueryResult filteredResult = getFilteredResult(id, filterGroup);

        // 创建分页结果
        PagedQueryResult pagedResult = PagedQueryResult.fromQueryResult(filteredResult, pageNumber, pageSize);

        log.info("Filtered paged result created for query execution {}", id);
        return pagedResult;
    }

    @Override
    public PagedQueryResult getFilteredPagedResult(String id, int pageNumber, int pageSize, FilterCondition condition) {
        FilterGroup group = new FilterGroup(List.of(condition), FilterLogic.AND);
        return getFilteredPagedResult(id, pageNumber, pageSize, group);
    }

    @Override
    public QueryResult getFilteredAndSortedResult(String id, FilterGroup filterGroup, List<SortField> sortFields) {
        log.info("Getting filtered and sorted result for query execution {}", id);

        if (filterGroup == null) {
            throw new IllegalArgumentException("Filter group cannot be null");
        }
        if (sortFields == null || sortFields.isEmpty()) {
            throw new IllegalArgumentException("Sort fields cannot be null or empty");
        }

        // 获取过滤后的结果
        QueryResult filteredResult = getFilteredResult(id, filterGroup);

        // 对结果进行排序
        QueryResult sortedResult = queryResultSortService.sort(filteredResult, sortFields);

        log.info("Filtered and sorted result created for query execution {}", id);
        return sortedResult;
    }

    @Override
    public PagedQueryResult getFilteredAndSortedPagedResult(String id, int pageNumber, int pageSize, FilterGroup filterGroup, List<SortField> sortFields) {
        log.info("Getting filtered and sorted paged result for query execution {}, page {}, size {}", id, pageNumber, pageSize);

        // 验证参数
        if (pageNumber < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("Page size must be greater than or equal to 1");
        }
        if (filterGroup == null) {
            throw new IllegalArgumentException("Filter group cannot be null");
        }
        if (sortFields == null || sortFields.isEmpty()) {
            throw new IllegalArgumentException("Sort fields cannot be null or empty");
        }

        // 获取过滤并排序后的结果
        QueryResult filteredAndSortedResult = getFilteredAndSortedResult(id, filterGroup, sortFields);

        // 创建分页结果
        PagedQueryResult pagedResult = PagedQueryResult.fromQueryResult(filteredAndSortedResult, pageNumber, pageSize);

        log.info("Filtered and sorted paged result created for query execution {}", id);
        return pagedResult;
    }

    @Override
    public StatisticsResult calculateStatistics(String id, String fieldName, StatisticsFunction function) {
        log.info("Calculating statistics for query execution {}, field {}, function {}", id, fieldName, function);

        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (function == null) {
            throw new IllegalArgumentException("Statistics function cannot be null");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot calculate statistics for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 计算统计信息
        StatisticsResult result = queryResultStatisticsService.calculate(execution.getResult(), fieldName, function);

        log.info("Statistics calculated for query execution {}, field {}, function {}", id, fieldName, function);
        return result;
    }

    @Override
    public List<StatisticsResult> calculateStatistics(String id, String fieldName, List<StatisticsFunction> functions) {
        log.info("Calculating multiple statistics for query execution {}, field {}", id, fieldName);

        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (functions == null || functions.isEmpty()) {
            throw new IllegalArgumentException("Statistics functions cannot be null or empty");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot calculate statistics for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 计算统计信息
        List<StatisticsResult> results = queryResultStatisticsService.calculate(execution.getResult(), fieldName, functions);

        log.info("Multiple statistics calculated for query execution {}, field {}", id, fieldName);
        return results;
    }

    @Override
    public List<StatisticsResult> calculateBasicStatistics(String id, String fieldName) {
        log.info("Calculating basic statistics for query execution {}, field {}", id, fieldName);

        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot calculate statistics for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 计算基本统计信息
        List<StatisticsResult> results = queryResultStatisticsService.calculateBasicStatistics(execution.getResult(), fieldName);

        log.info("Basic statistics calculated for query execution {}, field {}", id, fieldName);
        return results;
    }

    @Override
    public List<StatisticsResult> calculateFullStatistics(String id, String fieldName) {
        log.info("Calculating full statistics for query execution {}, field {}", id, fieldName);

        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }

        // 获取查询执行记录
        Optional<QueryExecution> executionOpt = queryExecutionRepository.findById(id);
        if (executionOpt.isEmpty()) {
            throw new IllegalArgumentException("Query execution not found: " + id);
        }

        QueryExecution execution = executionOpt.get();

        // 检查查询是否已完成
        if (!execution.isCompleted()) {
            throw new IllegalStateException("Cannot calculate statistics for query that is not completed: " + id);
        }

        // 检查是否有结果
        if (execution.getResult() == null) {
            throw new IllegalStateException("Query execution has no results: " + id);
        }

        // 计算完整统计信息
        List<StatisticsResult> results = queryResultStatisticsService.calculateFullStatistics(execution.getResult(), fieldName);

        log.info("Full statistics calculated for query execution {}, field {}", id, fieldName);
        return results;
    }

    @Override
    public StatisticsResult calculateFilteredStatistics(String id, FilterGroup filterGroup, String fieldName, StatisticsFunction function) {
        log.info("Calculating filtered statistics for query execution {}, field {}, function {}", id, fieldName, function);

        if (filterGroup == null) {
            throw new IllegalArgumentException("Filter group cannot be null");
        }
        if (fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or empty");
        }
        if (function == null) {
            throw new IllegalArgumentException("Statistics function cannot be null");
        }

        // 获取过滤后的结果
        QueryResult filteredResult = getFilteredResult(id, filterGroup);

        // 计算统计信息
        StatisticsResult result = queryResultStatisticsService.calculate(filteredResult, fieldName, function);

        log.info("Filtered statistics calculated for query execution {}, field {}, function {}", id, fieldName, function);
        return result;
    }
}
