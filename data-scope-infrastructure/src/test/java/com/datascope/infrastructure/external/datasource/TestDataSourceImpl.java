package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于测试的具体数据源实现类
 */
@Getter
@Setter
public class TestDataSourceImpl {
    private String id;
    private String name;
    private DataSource.DataSourceType type;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String salt;
    private DataSource.DataSourceStatus status;
    private DataSource.SyncStatus lastSyncStatus;
    private String lastSyncMessage;
    private String remark;

    public static TestDataSourceImpl createMySqlDataSource() {
        TestDataSourceImpl impl = new TestDataSourceImpl();
        impl.setId("test-id");
        impl.setName("test-db");
        impl.setType(DataSource.DataSourceType.MYSQL);
        impl.setHost("localhost");
        impl.setPort(3306);
        impl.setDatabase("test_db");
        impl.setUsername("test_user");
        impl.setPassword("encrypted_password");
        impl.setSalt("test_salt");
        impl.setStatus(DataSource.DataSourceStatus.ACTIVE);
        impl.setLastSyncStatus(DataSource.SyncStatus.NOT_SYNCED);
        return impl;
    }

    public static TestDataSourceImpl createDb2DataSource() {
        TestDataSourceImpl impl = new TestDataSourceImpl();
        impl.setId("test-id-2");
        impl.setName("test-db-2");
        impl.setType(DataSource.DataSourceType.DB2);
        impl.setHost("localhost");
        impl.setPort(50000);
        impl.setDatabase("test_db_2");
        impl.setUsername("test_user_2");
        impl.setPassword("encrypted_password_2");
        impl.setSalt("test_salt_2");
        impl.setStatus(DataSource.DataSourceStatus.ACTIVE);
        impl.setLastSyncStatus(DataSource.SyncStatus.NOT_SYNCED);
        return impl;
    }

    public DataSource toDataSource() {
        DataSource dataSource = new DataSource() {
        };
        dataSource.setId(id);
        dataSource.setName(name);
        dataSource.setType(type);
        dataSource.setHost(host);
        dataSource.setPort(port);
        dataSource.setDatabase(database);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setSalt(salt);
        dataSource.setStatus(status);
        dataSource.setLastSyncStatus(lastSyncStatus);
        dataSource.setLastSyncMessage(lastSyncMessage);
        dataSource.setRemark(remark);
        return dataSource;
    }
}
