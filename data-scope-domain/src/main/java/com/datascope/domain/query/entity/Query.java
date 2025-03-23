package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 查询实体类
 */
@Getter
@Setter
@ToString(callSuper = true)
public class Query extends BaseEntity {

    /**
     * 查询名称
     */
    private String name;

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * SQL语句
     */
    private String sql;

    /**
     * 查询参数
     */
    private Map<String, Object> parameters;

    /**
     * 查询描述
     */
    private String description;

    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecutedAt;

    /**
     * 最后执行状态
     */
    private QueryExecutionStatus lastExecutionStatus;

    /**
     * 最后执行消息
     */
    private String lastExecutionMessage;

    /**
     * 初始化查询
     *
     * @param operator 操作人
     */
    public void init(String operator) {
        super.init(operator);
        this.lastExecutionStatus = QueryExecutionStatus.NOT_EXECUTED;
    }

    /**
     * 更新执行状态
     *
     * @param status  执行状态
     * @param message 执行消息
     */
    public void updateExecutionStatus(QueryExecutionStatus status, String message) {
        this.lastExecutedAt = LocalDateTime.now();
        this.lastExecutionStatus = status;
        this.lastExecutionMessage = message;
    }

    /**
     * 查询执行状态枚举
     */
    public enum QueryExecutionStatus {
        /**
         * 执行成功
         */
        SUCCESS,

        /**
         * 执行失败
         */
        FAILED,

        /**
         * 执行中
         */
        EXECUTING,

        /**
         * 未执行
         */
        NOT_EXECUTED
    }
}
