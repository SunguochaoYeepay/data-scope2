package com.datascope.domain.query.model;

import com.datascope.domain.common.model.AuditInfo;
import com.datascope.domain.datasource.model.DataSourceId;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 查询执行记录
 */
@Getter
public class QueryExecution {
    private final UUID id;
    private final DataSourceId dataSourceId;
    private final String sql;
    private final Map<String, Object> parameters;
    private final LocalDateTime startTime;
    private QueryExecutionStatus status;
    private LocalDateTime endTime;
    private String errorMessage;
    private Long rowCount;
    private AuditInfo auditInfo;

    public QueryExecution(DataSourceId dataSourceId, String sql, Map<String, Object> parameters) {
        this.id = UUID.randomUUID();
        this.dataSourceId = dataSourceId;
        this.sql = sql;
        this.parameters = parameters;
        this.status = QueryExecutionStatus.PENDING;
        this.startTime = LocalDateTime.now();
    }

    public void markAsStarted() {
        this.status = QueryExecutionStatus.RUNNING;
    }

    public void markAsCompleted(Long rowCount) {
        this.status = QueryExecutionStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
        this.rowCount = rowCount;
    }

    public void markAsFailed(String errorMessage) {
        this.status = QueryExecutionStatus.FAILED;
        this.endTime = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }

    public Duration getDuration() {
        if (startTime == null || endTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(startTime, endTime);
    }

    public void setAuditInfo(AuditInfo auditInfo) {
        this.auditInfo = auditInfo;
    }
}
