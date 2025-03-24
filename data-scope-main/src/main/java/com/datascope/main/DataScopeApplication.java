package com.datascope.main;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
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
@SpringBootApplication(exclude = {
    HibernateJpaAutoConfiguration.class
})
@EnableTransactionManagement
@EnableConfigurationProperties
@ComponentScan(basePackages = "com.datascope")
@MapperScan(basePackages = "com.datascope.infrastructure.mybatis.mapper")
@EntityScan(basePackages = "com.datascope.domain.*.entity")
public class DataScopeApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DataScopeApplication.class);
        application.run(args);
    }

}
