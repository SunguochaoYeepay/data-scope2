package com.datascope.domain.query.model;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.enums.QueryExecutionStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 查询执行记录
 */
@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class QueryExecution {
    private String id;
    private DataSourceId dataSourceId;
    private String sql;
    private Map<String, Object> parameters;
    private QueryExecutionStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long resultCount;
    private String errorMessage;
    private String userId;
    private QueryResult result;

    /**
     * 获取ID
     *
     * @return ID
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取数据源ID
     *
     * @return 数据源ID
     */
    public DataSourceId getDataSourceId() {
        return dataSourceId;
    }

    /**
     * 设置数据源ID
     *
     * @param dataSourceId 数据源ID
     */
    public void setDataSourceId(DataSourceId dataSourceId) {
        this.dataSourceId = dataSourceId;
    }

    /**
     * 获取SQL语句
     *
     * @return SQL语句
     */
    public String getSql() {
        return sql;
    }

    /**
     * 设置SQL语句
     *
     * @param sql SQL语句
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * 获取查询参数
     *
     * @return 查询参数
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * 设置查询参数
     *
     * @param parameters 查询参数
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * 获取查询状态
     *
     * @return 查询状态
     */
    public QueryExecutionStatus getStatus() {
        return status;
    }

    /**
     * 设置查询状态
     *
     * @param status 查询状态
     */
    public void setStatus(QueryExecutionStatus status) {
        this.status = status;
    }

    /**
     * 获取查询结果
     *
     * @return 查询结果
     */
    public QueryResult getResult() {
        return result;
    }

    /**
     * 设置查询结果
     *
     * @param result 查询结果
     */
    public void setResult(QueryResult result) {
        this.result = result;
    }

    public QueryExecution(DataSourceId dataSourceId, String sql, Map<String, Object> parameters) {
        this.id = UUID.randomUUID().toString();
        this.dataSourceId = dataSourceId;
        this.sql = sql;
        this.parameters = parameters;
        this.status = QueryExecutionStatus.NOT_STARTED;
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
