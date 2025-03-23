package com.datascope.infrastructure.config;

import com.datascope.domain.datasource.entity.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TestConfigTest {

    @Autowired
    @Qualifier("testMySqlDataSource")
    private DataSource dataSource;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(dataSource);
    }
}
