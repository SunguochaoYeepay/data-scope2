package com.datascope.domain.datasource.entity;

import com.datascope.domain.common.entity.BaseEntity;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 数据源实体
 */
@Getter
@Setter
public class DataSource extends BaseEntity {

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源描述
     */
    private String description;

    /**
     * 数据源类型
     */
    private DataSourceType type;

    /**
     * 数据库主机
     */
    private String host;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String databaseName;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 加密后的密码
     */
    private String passwordEncrypted;

    /**
     * 密码加密盐值
     */
    private String passwordSalt;

    /**
     * 额外连接参数(JSON)
     */
    private String connectionParams;

    /**
     * 连接状态
     */
    private DataSourceStatus status;

    /**
     * 同步调度Cron表达式
     */
    private String syncFrequency;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncTime;
}