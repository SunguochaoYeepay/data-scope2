package com.datascope.domain.query.enums;

/**
 * 查询状态
 */
public enum QueryStatus {
    /**
     * 执行中
     */
    EXECUTING,

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
     * 执行取消
     */
    CANCELLED
}