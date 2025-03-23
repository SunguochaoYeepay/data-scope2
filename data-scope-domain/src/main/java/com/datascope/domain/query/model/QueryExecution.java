package com.datascope.domain.query.model;

import com.datascope.domain.datasource.model.DataSourceId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 查询执行记录
 */
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class QueryExecution {
    private UUID id;
    private DataSourceId dataSourceId;
    private String sql;
    private Map<String, Object> parameters;
    private QueryExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long resultCount;
    private String errorMessage;
    private String userId;

    public QueryExecution(DataSourceId dataSourceId, String sql, Map<String, Object> parameters) {
        this.id = UUID.randomUUID();
        this.dataSourceId = dataSourceId;
        this.sql = sql;
        this.parameters = parameters;
        this.status = QueryExecutionStatus.CREATED;
        this.startTime = LocalDateTime.now();
    }

    public void markAsStarted() {
        this.status = QueryExecutionStatus.RUNNING;
        this.startTime = LocalDateTime.now();
    }

    public void markAsCompleted(Long resultCount) {
        this.status = QueryExecutionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.resultCount = resultCount;
    }

    public void markAsFailed(String errorMessage) {
        this.status = QueryExecutionStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    public void markAsCancelled() {
        this.status = QueryExecutionStatus.CANCELLED;
        this.endTime = LocalDateTime.now();
    }

    public boolean isRunning() {
        return this.status == QueryExecutionStatus.RUNNING;
    }

    public boolean isCompleted() {
        return this.status == QueryExecutionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.status == QueryExecutionStatus.FAILED;
    }

    public boolean isCancelled() {
        return this.status == QueryExecutionStatus.CANCELLED;
    }
}
