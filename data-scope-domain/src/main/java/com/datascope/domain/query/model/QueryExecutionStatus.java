package com.datascope.domain.query.model;

/**
 * 查询执行状态
 */
public enum QueryExecutionStatus {
    /**
     * 已创建
     */
    CREATED,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 已完成
     */
    COMPLETED,

    /**
     * 执行失败
     */
    FAILED,

    /**
     * 已取消
     */
    CANCELLED
}
