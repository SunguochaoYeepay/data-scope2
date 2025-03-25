package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用于测试的具体数据源实现类
 */
@Data
@NoArgsConstructor
public class TestDataSourceImpl {
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

    public static TestDataSourceImpl createMySqlDataSource() {
        TestDataSourceImpl impl = new TestDataSourceImpl();
        impl.id = "test-id";
        impl.name = "test-db";
        impl.type = DataSourceType.MYSQL;
        impl.host = "localhost";
        impl.port = 3306;
        impl.database = "test_db";
        impl.username = "test_user";
        impl.password = "encrypted_password";
        impl.salt = "test_salt";
        impl.status = DataSourceStatus.ACTIVE;
        impl.lastSyncStatus = SyncStatus.NOT_SYNCED;
        return impl;
    }

    public static TestDataSourceImpl createDb2DataSource() {
        TestDataSourceImpl impl = new TestDataSourceImpl();
        impl.id = "test-id-2";
        impl.name = "test-db-2";
        impl.type = DataSourceType.DB2;
        impl.host = "localhost";
        impl.port = 50000;
        impl.database = "test_db_2";
        impl.username = "test_user_2";
        impl.password = "encrypted_password_2";
        impl.salt = "test_salt_2";
        impl.status = DataSourceStatus.ACTIVE;
        impl.lastSyncStatus = SyncStatus.NOT_SYNCED;
        return impl;
    }

    public DataSource toDataSource() {
        DataSource dataSource = new DataSource();
        dataSource.init("test-operator");

        // Copy fields using direct assignment
        try {
            java.lang.reflect.Field[] fields = DataSource.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                java.lang.reflect.Field sourceField = this.getClass().getDeclaredField(field.getName());
                sourceField.setAccessible(true);
                Object value = sourceField.get(this);
                if (value != null) {
                    field.set(dataSource, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy fields", e);
        }

        return dataSource;
    }
}
