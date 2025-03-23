package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;

public class TestDataSourceAdapter implements ITestDataSource {

    private final TestDataSource testDataSource;

    public TestDataSourceAdapter(TestDataSource testDataSource) {
        this.testDataSource = testDataSource;
    }

    @Override
    public String getId() {
        return testDataSource.getId();
    }

    @Override
    public String getName() {
        return testDataSource.getName();
    }

    @Override
    public String getHost() {
        return testDataSource.getHost();
    }

    @Override
    public Integer getPort() {
        return testDataSource.getPort();
    }

    @Override
    public String getDatabase() {
        return testDataSource.getDatabase();
    }

    @Override
    public String getUsername() {
        return testDataSource.getUsername();
    }

    @Override
    public String getPassword() {
        return testDataSource.getPassword();
    }

    @Override
    public String getSalt() {
        return testDataSource.getSalt();
    }

    @Override
    public DataSourceType getType() {
        return testDataSource.getType();
    }

    private DataSource.DataSourceType convertType(DataSourceType type) {
        switch (type) {
            case MYSQL:
                return DataSource.DataSourceType.MYSQL;
            case DB2:
                return DataSource.DataSourceType.DB2;
            default:
                throw new IllegalArgumentException("Unsupported data source type: " + type);
        }
    }
}