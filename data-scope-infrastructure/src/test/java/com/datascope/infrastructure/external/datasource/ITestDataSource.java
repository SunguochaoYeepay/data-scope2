package com.datascope.infrastructure.external.datasource;

public interface ITestDataSource {
    String getId();

    String getName();

    String getHost();

    Integer getPort();

    String getDatabase();

    String getUsername();

    String getPassword();

    String getSalt();

    DataSourceType getType();
}
