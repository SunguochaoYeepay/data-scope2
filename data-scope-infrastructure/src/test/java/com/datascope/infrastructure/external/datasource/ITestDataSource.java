package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.enums.DataSourceType;

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
