package com.datascope.domain.query.service.impl;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.domain.query.service.QueryExecutionService;
import com.datascope.domain.query.service.SqlExecutionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 查询执行服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QueryExecutionServiceImpl implements QueryExecutionService {

    private final QueryExecutionRepository queryExecutionRepository;
    private final SqlExecutionEngine sqlExecutionEngine;

    @Override
    @Transactional
    public QueryExecution executeSql(String dataSourceId, String sql, Map<String, Object> parameters) {
        // 创建查询执行记录
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            sql,
            parameters
        );

        // 保存执行记录
        execution = queryExecutionRepository.save(execution);

        try {
            // 标记为执行中
            execution.markAsStarted();

            // 实际执行SQL查询
            QueryResult queryResult = sqlExecutionEngine.execute(
                DataSourceId.of(dataSourceId),
                sql,
                parameters
            );

            // 更新执行记录
            execution.markAsCompleted(queryResult.getTotalRows());
            execution.setResult(queryResult);

        } catch (Exception e) {
            log.error("SQL execution failed", e);
            execution.markAsFailed(e.getMessage());
        }

        return queryExecutionRepository.save(execution);
    }

    @Override
    @Transactional
    public QueryExecution executeNaturalLanguage(String dataSourceId, String text) {
        // TODO: 调用LLM服务将自然语言转换为SQL
        String sql = "SELECT 1"; // 临时占位

        return executeSql(dataSourceId, sql, Map.of());
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
        if (execution.isPresent()) {
            // TODO: 实际取消查询执行
            execution.get().markAsFailed("Query cancelled by user");
            queryExecutionRepository.save(execution.get());
        }
    }

    @Override
    public String exportResult(String id, String format) {
        Optional<QueryExecution> execution = queryExecutionRepository.findById(id);
        if (execution.isPresent()) {
            // TODO: 实现导出逻辑
            return "/tmp/export/" + id + "." + format.toLowerCase();
        }
        throw new IllegalArgumentException("Query execution not found: " + id);
    }
}
