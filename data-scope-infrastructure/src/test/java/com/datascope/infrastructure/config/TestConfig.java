package com.datascope.infrastructure.config;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.infrastructure.external.datasource.TestDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 测试配置类
 */
@Configuration
@Profile("test")
public class TestConfig {

    @Bean(name = "testMySqlDataSource")
    public DataSource testMySqlDataSource() {
        return TestDataSourceFactory.createMySqlDataSource();
    }

    @Bean(name = "testDb2DataSource")
    public DataSource testDb2DataSource() {
        return TestDataSourceFactory.createDb2DataSource();
    }
}