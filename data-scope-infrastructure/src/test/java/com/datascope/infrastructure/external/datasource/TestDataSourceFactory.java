package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;

import java.lang.reflect.Field;

/**
 * 测试数据源工厂类
 */
public final class TestDataSourceFactory {

    private TestDataSourceFactory() {
        // 工具类私有构造函数
    }

    private static void setFieldValue(Object obj, String fieldName, Object value) {
        try {
            Field field;
            try {
                field = obj.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
            }
            if (field != null) {
                field.setAccessible(true);
                field.set(obj, value);
            }
        } catch (Exception e) {
            System.err.println("Failed to set field value: " + e.getMessage());
        }
    }

    public static DataSource createMySqlDataSource() {
        DataSource dataSource = new DataSource();
        dataSource.init("test-operator");

        setFieldValue(dataSource, "id", "test-id");
        setFieldValue(dataSource, "name", "test-db");
        setFieldValue(dataSource, "type", DataSourceType.MYSQL);
        setFieldValue(dataSource, "host", "localhost");
        setFieldValue(dataSource, "port", 3306);
        setFieldValue(dataSource, "database", "test_db");
        setFieldValue(dataSource, "username", "test_user");
        setFieldValue(dataSource, "password", "encrypted_password");
        setFieldValue(dataSource, "salt", "test_salt");
        setFieldValue(dataSource, "status", DataSourceStatus.ACTIVE);
        setFieldValue(dataSource, "lastSyncStatus", SyncStatus.NOT_SYNCED);

        return dataSource;
    }

    public static DataSource createDb2DataSource() {
        DataSource dataSource = new DataSource();
        dataSource.init("test-operator");

        setFieldValue(dataSource, "id", "test-id-2");
        setFieldValue(dataSource, "name", "test-db-2");
        setFieldValue(dataSource, "type", DataSourceType.DB2);
        setFieldValue(dataSource, "host", "localhost");
        setFieldValue(dataSource, "port", 50000);
        setFieldValue(dataSource, "database", "test_db_2");
        setFieldValue(dataSource, "username", "test_user_2");
        setFieldValue(dataSource, "password", "encrypted_password_2");
        setFieldValue(dataSource, "salt", "test_salt_2");
        setFieldValue(dataSource, "status", DataSourceStatus.ACTIVE);
        setFieldValue(dataSource, "lastSyncStatus", SyncStatus.NOT_SYNCED);

        return dataSource;
    }
}
