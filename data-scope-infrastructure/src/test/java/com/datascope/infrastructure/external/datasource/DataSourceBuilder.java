package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 测试数据源构建器
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class DataSourceBuilder {
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
    private String lastSyncMessage;
    private String remark;

    public static DataSourceBuilder mysql() {
        return new DataSourceBuilder()
            .setId("test-id")
            .setName("test-db")
            .setType(DataSourceType.MYSQL)
            .setHost("localhost")
            .setPort(3306)
            .setDatabase("test_db")
            .setUsername("test_user")
            .setPassword("encrypted_password")
            .setSalt("test_salt")
            .setStatus(DataSourceStatus.INACTIVE)
            .setLastSyncStatus(SyncStatus.NOT_SYNCED);
    }

    public static DataSourceBuilder db2() {
        return new DataSourceBuilder()
            .setId("test-id-2")
            .setName("test-db-2")
            .setType(DataSourceType.DB2)
            .setHost("localhost")
            .setPort(50000)
            .setDatabase("test_db_2")
            .setUsername("test_user_2")
            .setPassword("encrypted_password_2")
            .setSalt("test_salt_2")
            .setStatus(DataSourceStatus.INACTIVE)
            .setLastSyncStatus(SyncStatus.NOT_SYNCED);
    }

    public DataSource build() {
        DataSource dataSource = new DataSource();
        dataSource.init("test-operator");

        // 使用反射设置字段值
        try {
            java.lang.reflect.Field[] fields = DataSource.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                field.setAccessible(true);
                java.lang.reflect.Field builderField = this.getClass().getDeclaredField(field.getName());
                builderField.setAccessible(true);
                Object value = builderField.get(this);
                if (value != null) {
                    field.set(dataSource, value);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to build DataSource", e);
        }

        return dataSource;
    }
}
