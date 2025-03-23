package com.datascope.domain.datasource.enums;

/**
 * 数据源状态枚举
 * 
 * @author dreambt
 */
public enum DataSourceStatus {
    /**
     * 未激活
     */
    INACTIVE,

    /**
     * 活跃
     */
    ACTIVE,

    /**
     * 同步中
     */
    SYNCING,

    /**
     * 错误
     */
    ERROR,

    /**
     * 已禁用
     */
    DISABLED
}
