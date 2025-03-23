package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用于测试的具体数据源实现类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ConcreteDataSource extends DataSource {

    public static ConcreteDataSource createMySqlDataSource() {
        ConcreteDataSource dataSource = new ConcreteDataSource();
        dataSource.setId("test-id");
        dataSource.setName("test-db");
        dataSource.setType(DataSourceType.MYSQL);
        dataSource.setHost("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabase("test_db");
        dataSource.setUsername("test_user");
        dataSource.setPassword("encrypted_password");
        dataSource.setSalt("test_salt");
        dataSource.setStatus(DataSourceStatus.ACTIVE);
        dataSource.setLastSyncStatus(SyncStatus.NOT_SYNCED);
        dataSource.init("test-operator");
        return dataSource;
    }

    public static ConcreteDataSource createDb2DataSource() {
        ConcreteDataSource dataSource = new ConcreteDataSource();
        dataSource.setId("test-id-2");
        dataSource.setName("test-db-2");
        dataSource.setType(DataSourceType.DB2);
        dataSource.setHost("localhost");
        dataSource.setPort(50000);
        dataSource.setDatabase("test_db_2");
        dataSource.setUsername("test_user_2");
        dataSource.setPassword("encrypted_password_2");
        dataSource.setSalt("test_salt_2");
        dataSource.setStatus(DataSourceStatus.ACTIVE);
        dataSource.setLastSyncStatus(SyncStatus.NOT_SYNCED);
        dataSource.init("test-operator");
        return dataSource;
    }
}
