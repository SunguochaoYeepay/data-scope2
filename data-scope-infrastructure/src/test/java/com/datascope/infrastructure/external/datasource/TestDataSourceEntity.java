package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 测试用数据源实体类
 */
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TestDataSourceEntity extends DataSource {

    public static TestDataSourceEntity createMySqlDataSource() {
        TestDataSourceEntity entity = new TestDataSourceEntity();
        entity.init("test-operator");
        entity.setId("test-id");
        entity.setName("test-db");
        entity.setType(DataSourceType.MYSQL);
        entity.setHost("localhost");
        entity.setPort(3306);
        entity.setDatabase("test_db");
        entity.setUsername("test_user");
        entity.setPassword("encrypted_password");
        entity.setSalt("test_salt");
        return entity;
    }

    public static TestDataSourceEntity createDb2DataSource() {
        TestDataSourceEntity entity = new TestDataSourceEntity();
        entity.init("test-operator");
        entity.setId("test-id-2");
        entity.setName("test-db-2");
        entity.setType(DataSourceType.DB2);
        entity.setHost("localhost");
        entity.setPort(50000);
        entity.setDatabase("test_db_2");
        entity.setUsername("test_user_2");
        entity.setPassword("encrypted_password_2");
        entity.setSalt("test_salt_2");
        return entity;
    }
}
