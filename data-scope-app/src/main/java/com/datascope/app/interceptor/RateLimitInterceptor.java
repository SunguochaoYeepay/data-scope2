package com.datascope.app.interceptor;

import com.datascope.app.config.AppProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.redis.RedisProxyManager;
import io.github.bucket4j.redis.lettuce.LettuceProxyManager;
import io.lettuce.core.api.StatefulRedisConnection;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 限流拦截器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitInterceptor implements HandlerInterceptor {

    private final StatefulRedisConnection<String, String> redisConnection;
    private final AppProperties appProperties;
    private final RedisProxyManager<String> proxyManager;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!appProperties.getRateLimit().isEnabled()) {
            return true;
        }

        String key = getKey(request);
        Bucket bucket = resolveBucket(key);

        if (bucket.tryConsume(1)) {
            return true;
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests");
            return false;
        }
    }

    private String getKey(HttpServletRequest request) {
        String userId = request.getHeader("X-User-Id");
        String uri = request.getRequestURI();
        return String.format("%s:%s:%s", 
            appProperties.getRateLimit().getKeyPrefix(), userId, uri);
    }

    private Bucket resolveBucket(String key) {
        Supplier<BucketConfiguration> configSupplier = () -> {
            AppProperties.RateLimitProperties properties = appProperties.getRateLimit();
            return BucketConfiguration.builder()
                .addLimit(Bandwidth.simple(properties.getDefaultRate(), 
                    Duration.ofSeconds(properties.getTimeWindow())))
                .addLimit(Bandwidth.simple(properties.getDefaultBurst(), 
                    Duration.ofSeconds(properties.getTimeWindow() * 60)))
                .build();
        };

        return proxyManager.builder().build(key, configSupplier);
    }

    @Component
    @RequiredArgsConstructor
    public static class RedisProxyManagerConfig {
        private final StatefulRedisConnection<String, String> redisConnection;

        public RedisProxyManager<String> redisProxyManager() {
            return LettuceProxyManager.builderFor(redisConnection)
                .withKeyPrefix("bucket4j")
                .build();
        }
    }
}