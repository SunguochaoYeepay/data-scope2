package com.datascope.main.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 组件扫描配置类
 */
@Configuration
@ComponentScan(basePackages = {
    "com.datascope.domain.*",
    "com.datascope.app.*",
    "com.datascope.infrastructure.*"
})
public class ComponentScanConfig {
    // 配置类，无需额外方法
}
