package com.datascope.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableAutoConfiguration
@EnableTransactionManagement
@ComponentScan(basePackages = {
    "com.datascope.infrastructure.repository",
    "com.datascope.infrastructure.mybatis.mapper"
})
@MapperScan("com.datascope.infrastructure.mybatis.mapper")
public class TestConfig {
}