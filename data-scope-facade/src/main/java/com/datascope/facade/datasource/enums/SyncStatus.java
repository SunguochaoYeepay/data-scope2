package com.datascope.facade.datasource.enums;

public enum SyncStatus {
    NOT_SYNCED,
    SYNCING,
    SYNCED,
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
