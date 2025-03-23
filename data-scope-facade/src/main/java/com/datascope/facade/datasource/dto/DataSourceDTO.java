package com.datascope.facade.datasource.dto;

import com.datascope.facade.datasource.enums.DataSourceStatus;
import com.datascope.facade.datasource.enums.DataSourceType;
import com.datascope.facade.datasource.enums.SyncStatus;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataSourceType getType() {
        return type;
    }

    public void setType(DataSourceType type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public DataSourceStatus getStatus() {
        return status;
    }

    public void setStatus(DataSourceStatus status) {
        this.status = status;
    }

    public SyncStatus getLastSyncStatus() {
        return lastSyncStatus;
    }

    public void setLastSyncStatus(SyncStatus lastSyncStatus) {
        this.lastSyncStatus = lastSyncStatus;
    }
}
