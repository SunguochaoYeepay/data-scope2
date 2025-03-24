package com.datascope.domain.query.service.impl;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.PagedQueryResult;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.domain.query.service.QueryExecutionService;
import com.datascope.domain.query.service.SqlExecutionEngine;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
@Service
public class QueryExecutionServiceImpl implements QueryExecutionService {

    @Setter(onMethod_ = @Autowired)
    private QueryExecutionRepository queryExecutionRepository;

    @Setter(onMethod_ = @Autowired)
    private SqlExecutionEngine sqlExecutionEngine;

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
        queryExecutionRepository.save(execution);

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

        try {
            // 标记为执行中
            execution.markAsStarted();

            // TODO: 调用LLM服务将自然语言转换为SQL
            // 这里是临时实现，实际项目中需要集成LLM服务
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
        Optional<QueryExecution> execution = queryExecutionRepository.findById(id);
        if (execution.isPresent() && execution.get().isRunning()) {
            QueryExecution queryExecution = execution.get();

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
        } else {
            log.warn("Cannot cancel query execution: {} (not found or not running)", id);
        }
    }

    @Override
    public String exportResult(String id, String format) {
        Optional<QueryExecution> execution = queryExecutionRepository.findById(id);
        if (execution.isPresent()) {
            QueryExecution queryExecution = execution.get();

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
        throw new IllegalArgumentException("Query execution not found: " + id);
    }

    private void exportToCsv(QueryResult result, String filePath) {
        // TODO: 实现CSV导出逻辑
        log.info("CSV export not yet implemented");
    }

    private void exportToJson(QueryResult result, String filePath) {
        // TODO: 实现JSON导出逻辑
        log.info("JSON export not yet implemented");
    }

    private void exportToExcel(QueryResult result, String filePath) {
        // TODO: 实现Excel导出逻辑
        log.info("Excel export not yet implemented");
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
}
