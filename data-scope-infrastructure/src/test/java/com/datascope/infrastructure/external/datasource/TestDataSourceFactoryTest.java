package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestDataSourceFactoryTest {

    private Object getFieldValue(Object obj, String fieldName) throws Exception {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        }
        field.setAccessible(true);
        return field.get(obj);
    }

    @Test
    void createMySqlDataSource() throws Exception {
        DataSource dataSource = TestDataSourceFactory.createMySqlDataSource();

        assertNotNull(dataSource);
        assertEquals("test-id", getFieldValue(dataSource, "id"));
        assertEquals("test-db", getFieldValue(dataSource, "name"));
        assertEquals(DataSource.DataSourceType.MYSQL, getFieldValue(dataSource, "type"));
        assertEquals("localhost", getFieldValue(dataSource, "host"));
        assertEquals(3306, getFieldValue(dataSource, "port"));
        assertEquals("test_db", getFieldValue(dataSource, "database"));
        assertEquals("test_user", getFieldValue(dataSource, "username"));
        assertEquals("encrypted_password", getFieldValue(dataSource, "password"));
        assertEquals("test_salt", getFieldValue(dataSource, "salt"));
        assertEquals(DataSource.DataSourceStatus.ACTIVE, getFieldValue(dataSource, "status"));
        assertEquals(DataSource.SyncStatus.NOT_SYNCED, getFieldValue(dataSource, "lastSyncStatus"));
    }

    @Test
    void createDb2DataSource() throws Exception {
        DataSource dataSource = TestDataSourceFactory.createDb2DataSource();

        assertNotNull(dataSource);
        assertEquals("test-id-2", getFieldValue(dataSource, "id"));
        assertEquals("test-db-2", getFieldValue(dataSource, "name"));
        assertEquals(DataSource.DataSourceType.DB2, getFieldValue(dataSource, "type"));
        assertEquals("localhost", getFieldValue(dataSource, "host"));
        assertEquals(50000, getFieldValue(dataSource, "port"));
        assertEquals("test_db_2", getFieldValue(dataSource, "database"));
        assertEquals("test_user_2", getFieldValue(dataSource, "username"));
        assertEquals("encrypted_password_2", getFieldValue(dataSource, "password"));
        assertEquals("test_salt_2", getFieldValue(dataSource, "salt"));
        assertEquals(DataSource.DataSourceStatus.ACTIVE, getFieldValue(dataSource, "status"));
        assertEquals(DataSource.SyncStatus.NOT_SYNCED, getFieldValue(dataSource, "lastSyncStatus"));
    }
}
