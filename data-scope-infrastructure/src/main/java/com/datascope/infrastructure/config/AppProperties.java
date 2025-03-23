package com.datascope.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * Redis配置
     */
    private RedisProperties redis = new RedisProperties();

    /**
     * 限流配置
     */
    private RateLimitProperties rateLimit = new RateLimitProperties();

    /**
     * Redis配置属性
     */
    @Data
    public static class RedisProperties {
        /**
         * 主机地址
         */
        private String host = "localhost";

        /**
         * 端口号
         */
        private int port = 6379;

        /**
         * 数据库索引
         */
        private int database = 0;

        /**
         * 密码
         */
        private String password;

        /**
         * 连接超时时间（毫秒）
         */
        private int timeout = 5000;
    }

    /**
     * 限流配置属性
     */
    @Data
    public static class RateLimitProperties {
        /**
         * 默认每秒请求数
         */
        private int defaultRate = 10;

        /**
         * 默认突发请求数
         */
        private int defaultBurst = 20;

        /**
         * 是否启用限流
         */
        private boolean enabled = true;

        /**
         * 限流键前缀
         */
        private String keyPrefix = "rate_limit";

        /**
         * 限流时间窗口（秒）
         */
        private int timeWindow = 1;
    }
}
