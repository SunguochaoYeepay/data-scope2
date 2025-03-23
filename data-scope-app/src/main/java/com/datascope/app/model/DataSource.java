package com.datascope.app.model;

import com.datascope.domain.datasource.entity.DataSource.DataSourceStatus;
import com.datascope.domain.datasource.entity.DataSource.DataSourceType;
import com.datascope.domain.datasource.entity.DataSource.SyncStatus;
import lombok.Data;

@Data
public class DataSource {
    private String id;
    private String name;
    private DataSourceType type;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String salt;
    private DataSourceStatus status;
    private SyncStatus lastSyncStatus;
}
