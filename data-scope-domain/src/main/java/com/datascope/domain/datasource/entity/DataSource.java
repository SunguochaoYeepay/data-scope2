package com.datascope.domain.datasource.entity;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 数据源实体
 */
@Data
@NoArgsConstructor
public class DataSource {
    private String id;
    private String name;
    private DataSourceType type;
    private String host;
    private int port;
    private String database;
    private String schema;
    private String username;
    private String password;
    private String salt;
    private DataSourceStatus status;
    private SyncStatus lastSyncStatus;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

    public void init(String operator) {
        this.createdBy = operator;
        this.updatedBy = operator;
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.status = DataSourceStatus.INACTIVE;
        this.lastSyncStatus = SyncStatus.NOT_SYNCED;
    }

    public void update(String operator) {
        this.updatedBy = operator;
        this.updatedTime = LocalDateTime.now();
    }

    public void activate(String operator) {
        this.status = DataSourceStatus.ACTIVE;
        update(operator);
    }

    public void deactivate(String operator) {
        this.status = DataSourceStatus.INACTIVE;
        update(operator);
    }

    public void updateSyncStatus(SyncStatus status, String operator) {
        this.lastSyncStatus = status;
        update(operator);
    }
}
