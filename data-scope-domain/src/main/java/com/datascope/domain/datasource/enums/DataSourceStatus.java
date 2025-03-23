package com.datascope.domain.datasource.enums;

/**
 * 数据源状态
 */
public enum DataSourceStatus {
    /**
     * 活跃状态
     */
    ACTIVE,

    /**
     * 非活跃状态
     */
    INACTIVE,

    /**
     * 同步中
     */
    SYNCING,

    /**
     * 错误状态
     */
    ERROR
}