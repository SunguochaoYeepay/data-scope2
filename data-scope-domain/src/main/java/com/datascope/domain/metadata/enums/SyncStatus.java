package com.datascope.domain.metadata.enums;

/**
 * 同步状态
 */
public enum SyncStatus {
    /**
     * 同步中
     */
    SYNCING,

    /**
     * 同步成功
     */
    SUCCESS,

    /**
     * 同步失败
     */
    FAILED,

    /**
     * 同步取消
     */
    CANCELLED
}