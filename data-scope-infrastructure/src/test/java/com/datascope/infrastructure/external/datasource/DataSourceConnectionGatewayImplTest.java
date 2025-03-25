package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.gateway.PasswordEncryptorGateway;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class DataSourceConnectionGatewayImplTest {

    @Mock
    private PasswordEncryptorGateway passwordEncryptor;

    @InjectMocks
    private DataSourceConnectionGatewayImpl dataSourceConnectionGateway;

    private DataSource dataSource;

    @BeforeEach
    void setUp() {
        // 创建MySQL测试数据源
        dataSource = new DataSource();
        dataSource.init("test-operator");

        // 使用反射设置字段值
        try {
            java.lang.reflect.Field[] fields = DataSource.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                switch (field.getName()) {
                    case "id":
                        field.set(dataSource, "test-id");
                        break;
                    case "name":
                        field.set(dataSource, "test-db");
                        break;
                    case "type":
                        field.set(dataSource, DataSourceType.MYSQL);
                        break;
                    case "host":
                        field.set(dataSource, "localhost");
                        break;
                    case "port":
                        field.set(dataSource, 3306);
                        break;
                    case "database":
                        field.set(dataSource, "test");
                        break;
                    case "username":
                        field.set(dataSource, "test");
                        break;
                    case "password":
                        field.set(dataSource, "encrypted_password");
                        break;
                    case "salt":
                        field.set(dataSource, "test_salt");
                        break;
                    case "status":
                        field.set(dataSource, DataSourceStatus.ACTIVE);
                        break;
                    case "lastSyncStatus":
                        field.set(dataSource, SyncStatus.NOT_SYNCED);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Failed to set field value", e);
            throw new RuntimeException(e);
        }
    }

    @Test
    void testTestConnection() {
        when(passwordEncryptor.decrypt(anyString(), anyString()))
            .thenReturn("test");

        boolean result = dataSourceConnectionGateway.testConnection(dataSource);
        assertTrue(result);
        verify(passwordEncryptor).decrypt(anyString(), anyString());
    }

    @Test
    void testCloseDataSource() {
        dataSourceConnectionGateway.closeDataSource("test-id");
    }

    @Test
    void testCloseAllDataSources() {
        dataSourceConnectionGateway.closeAllDataSources();
    }

    @Test
    void testGetConnection() throws SQLException {
        when(passwordEncryptor.decrypt(anyString(), anyString()))
            .thenReturn("test");

        dataSourceConnectionGateway.getConnection(dataSource);
        verify(passwordEncryptor).decrypt(anyString(), anyString());
    }

    @Test
    void testMultipleDataSources() throws SQLException {
        // 创建DB2测试数据源
        DataSource dataSource2 = new DataSource();
        dataSource2.init("test-operator");

        // 使用反射设置字段值
        try {
            java.lang.reflect.Field[] fields = DataSource.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                switch (field.getName()) {
                    case "id":
                        field.set(dataSource2, "test-id-2");
                        break;
                    case "name":
                        field.set(dataSource2, "test-db-2");
                        break;
                    case "type":
                        field.set(dataSource2, DataSourceType.MYSQL);
//                        field.set(dataSource2, DataSourceType.DB2);
                        break;
                    case "host":
                        field.set(dataSource2, "localhost");
                        break;
                    case "port":
                        field.set(dataSource2, 3306);
//                        field.set(dataSource2, 50000);
                        break;
                    case "database":
                        field.set(dataSource2, "test");
                        break;
                    case "username":
                        field.set(dataSource2, "test");
                        break;
                    case "password":
                        field.set(dataSource2, "encrypted_password_2");
                        break;
                    case "salt":
                        field.set(dataSource2, "test_salt_2");
                        break;
                    case "status":
                        field.set(dataSource2, DataSourceStatus.ACTIVE);
                        break;
                    case "lastSyncStatus":
                        field.set(dataSource2, SyncStatus.NOT_SYNCED);
                        break;
                }
            }
        } catch (Exception e) {
            log.error("Failed to set field value", e);
            throw new RuntimeException(e);
        }

        when(passwordEncryptor.decrypt(anyString(), anyString()))
            .thenReturn("test");

        dataSourceConnectionGateway.getConnection(dataSource);
        dataSourceConnectionGateway.getConnection(dataSource2);

        verify(passwordEncryptor, times(2)).decrypt(anyString(), anyString());

        dataSourceConnectionGateway.closeAllDataSources();
    }
}
