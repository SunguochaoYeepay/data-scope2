package com.datascope.domain.query.enums;

/**
 * 查询执行状态枚举
 */
public enum QueryExecutionStatus {
    /**
     * 已创建
     */
    CREATED,

    /**
     * 未执行
     */
    NOT_EXECUTED,

    /**
     * 执行中
     */
    RUNNING,

    /**
     * 已完成
     */
    COMPLETED,

    /**
     * 执行成功
     */
    SUCCESS,

    /**
     * 执行失败
     */
    FAILED,

    /**
     * 执行超时
     */
    TIMEOUT,

    /**
     * 已取消
     */
    CANCELLED
}