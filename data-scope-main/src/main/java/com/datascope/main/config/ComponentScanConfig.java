package com.datascope.main.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Component Scan Configuration
 * Scans all modules for Spring components
 * 
 * @author dreambt
 */
@Configuration
@ComponentScan(basePackages = {
    "com.datascope.app",
    "com.datascope.domain",
    "com.datascope.facade",
    "com.datascope.infrastructure"
})
public class ComponentScanConfig {
    // Configuration is handled through annotations
}