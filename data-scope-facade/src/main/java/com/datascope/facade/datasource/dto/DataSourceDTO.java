package com.datascope.facade.datasource.dto;

import com.datascope.facade.datasource.enums.DataSourceStatus;
import com.datascope.facade.datasource.enums.DataSourceType;
import com.datascope.facade.datasource.enums.SyncStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DataSourceDTO {
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
    private String createdBy;
    private LocalDateTime createdTime;
    private String modifiedBy;
    private LocalDateTime modifiedTime;

}
