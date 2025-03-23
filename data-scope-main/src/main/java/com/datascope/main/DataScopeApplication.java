package com.datascope.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 数据范围应用程序入口
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan(basePackages = "com.datascope")
@MapperScan(basePackages = "com.datascope.infrastructure.mybatis.mapper")
public class DataScopeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DataScopeApplication.class, args);
    }
}