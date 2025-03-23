package com.datascope.domain.datasource.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 数据源实体类
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DataSource extends BaseEntity {

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据源类型
     */
    private DataSourceType type;

    /**
     * 主机地址
     */
    private String host;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 数据库名称
     */
    private String database;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 数据源状态
     */
    private DataSourceStatus status;

    /**
     * 最后同步时间
     */
    private LocalDateTime lastSyncAt;

    /**
     * 最后同步状态
     */
    private SyncStatus lastSyncStatus;

    /**
     * 最后同步消息
     */
    private String lastSyncMessage;

    /**
     * 备注
     */
    private String remark;

    /**
     * 数据源类型枚举
     */
    public enum DataSourceType {
        /**
         * MySQL数据库
         */
        MYSQL,

        /**
         * DB2数据库
         */
        DB2
    }

    /**
     * 数据源状态枚举
     */
    public enum DataSourceStatus {
        /**
         * 活跃状态
         */
        ACTIVE,

        /**
         * 非活跃状态
         */
        INACTIVE
    }

    /**
     * 同步状态枚举
     */
    public enum SyncStatus {
        /**
         * 同步成功
         */
        SUCCESS,

        /**
         * 同步失败
         */
        FAILED,

        /**
         * 同步中
         */
        SYNCING,

        /**
         * 未同步
         */
        NOT_SYNCED
    }

    /**
     * 初始化数据源
     *
     * @param operator 操作人
     */
    public void init(String operator) {
        super.init(operator);
        this.status = DataSourceStatus.INACTIVE;
        this.lastSyncStatus = SyncStatus.NOT_SYNCED;
    }

    /**
     * 激活数据源
     *
     * @param operator 操作人
     */
    public void activate(String operator) {
        this.status = DataSourceStatus.ACTIVE;
        this.update(operator);
    }

    /**
     * 停用数据源
     *
     * @param operator 操作人
     */
    public void deactivate(String operator) {
        this.status = DataSourceStatus.INACTIVE;
        this.update(operator);
    }

    /**
     * 更新同步状态
     *
     * @param status  同步状态
     * @param message 同步消息
     */
    public void updateSyncStatus(SyncStatus status, String message) {
        this.lastSyncAt = LocalDateTime.now();
        this.lastSyncStatus = status;
        this.lastSyncMessage = message;
    }
}