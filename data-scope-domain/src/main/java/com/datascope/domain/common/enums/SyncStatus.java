package com.datascope.domain.common.enums;

/**
 * 同步状态枚举
 */
public enum SyncStatus {
    /**
     * 未同步
     */
    NOT_SYNCED,

    /**
     * 同步中
     */
    SYNCING,

    /**
     * 已同步
     */
    SYNCED,

    /**
     * 同步失败
     */
    FAILED,

    /**
     * 同步成功
     */
    SUCCESS,

    /**
     * 同步取消
     */
    CANCELLED
}