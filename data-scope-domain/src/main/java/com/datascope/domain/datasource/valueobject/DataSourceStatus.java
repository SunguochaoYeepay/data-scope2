package com.datascope.domain.datasource.valueobject;

/**
 * 数据源状态
 */
public enum DataSourceStatus {
    /**
     * 活动状态 - 可以正常连接和使用
     */
    ACTIVE,

    /**
     * 非活动状态 - 暂时无法使用
     */
    INACTIVE,

    /**
     * 错误状态 - 连接或配置存在问题
     */
    ERROR,

    /**
     * 同步中状态 - 正在进行元数据同步
     */
    SYNCING,

    /**
     * 维护状态 - 正在进行维护
     */
    MAINTENANCE
}