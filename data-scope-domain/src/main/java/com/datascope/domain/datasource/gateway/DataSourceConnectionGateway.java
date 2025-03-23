package com.datascope.domain.datasource.gateway;

import com.datascope.domain.datasource.entity.DataSource;

import java.sql.Connection;
import java.sql.SQLException;

public interface DataSourceConnectionGateway {
    Connection getConnection(DataSource dataSource) throws SQLException;

    boolean testConnection(DataSource dataSource);

    void closeDataSource(String dataSourceId);

    void closeAllDataSources();
}
