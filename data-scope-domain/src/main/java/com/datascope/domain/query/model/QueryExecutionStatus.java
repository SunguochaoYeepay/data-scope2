package com.datascope.domain.query.model;

/**
 * 查询执行状态
 */
public enum QueryExecutionStatus {
    /**
     * 等待执行
     */
    PENDING,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 执行完成
     */
    COMPLETED,

    /**
     * 执行失败
     */
    FAILED
}
