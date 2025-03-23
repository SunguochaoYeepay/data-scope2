# Performance Guidelines

## Overview
This document outlines performance requirements, optimization strategies, and best practices for the DataScope system.

## Performance Requirements

### Response Times
- API Response Time: < 500ms (95th percentile)
- Query Execution: < 30 seconds
- Metadata Sync: < 5 minutes per data source
- UI Rendering: < 2 seconds for initial load
- Page Navigation: < 1 second

### Throughput
- Concurrent Users: Up to 1000
- Queries per Second: Up to 100
- API Requests per Second: Up to 1000
- Metadata Sync: Up to 10 concurrent jobs

### Resource Utilization
- CPU Usage: < 70% under normal load
- Memory Usage: < 80% of available RAM
- Disk I/O: < 70% of capacity
- Network Bandwidth: < 60% of capacity

## Caching Strategy

### Redis Caching
```java
@Configuration
public class CacheConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .withCacheConfiguration("metadata", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofHours(24)))
            .withCacheConfiguration("queries", RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10)))
            .build();
    }
}
```

### Cache Keys
- Metadata: `metadata:{datasourceId}:{schema}:{table}`
- Query Results: `query:{hash}:{params}`
- User Preferences: `user:{userId}:preferences`
- Display Configs: `display:{userId}:{datasourceId}:{table}`

### Cache Invalidation
- Metadata: On sync or manual update
- Query Results: Time-based expiry
- User Preferences: On update
- Display Configs: On update

## Query Optimization

### SQL Optimization
- Use prepared statements
- Implement query timeout
- Add appropriate indexes
- Optimize JOIN operations
- Use pagination
- Avoid SELECT *

### Example Query Pattern
```java
@Repository
public class OptimizedQueryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> executeQuery(String sql, Map<String, Object> params, int timeout) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setQueryTimeout(timeout);
                setParameters(stmt, params);
                return extractResults(stmt.executeQuery());
            }
        });
    }
}
```

## Rate Limiting

### API Rate Limits
```java
@Configuration
public class RateLimitConfig {
    @Bean
    public Bucket4j.Builder bucketBuilder() {
        return Bucket4j.builder()
            .addLimit(Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1))))
            .addLimit(Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofHours(1))));
    }
}
```

### Limits by Resource
- Query Execution: 10 per minute per user
- Metadata Sync: 1 per hour per data source
- API Requests: 100 per minute per user
- Export Data: 1 per minute per user

## Connection Pooling

### HikariCP Configuration
```java
@Configuration
public class DataSourceConfig {
    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        config.setMaxLifetime(1200000);
        return config;
    }
}
```

### Pool Sizing Guidelines
- Maximum Pool Size = (Core Count * 2) + 1
- Minimum Idle = Maximum Pool Size / 4
- Connection Timeout = 20 seconds
- Idle Timeout = 5 minutes
- Max Lifetime = 20 minutes

## Memory Management

### JVM Configuration
```bash
JAVA_OPTS="\
    -Xms2g \
    -Xmx4g \
    -XX:+UseG1GC \
    -XX:MaxGCPauseMillis=200 \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/var/log/datascope/heap-dump.hprof"
```

### Memory Guidelines
- Heap Size: 25-50% of available RAM
- MetaSpace: 256MB initial
- Direct Memory: 20% of heap
- Stack Size: 1MB per thread

## Monitoring and Optimization

### Metrics to Monitor
- Response Times
- Error Rates
- Cache Hit Rates
- Connection Pool Usage
- Memory Usage
- GC Activity
- Thread States
- CPU Usage
- Disk I/O
- Network I/O

### Performance Testing
- Load Testing: Apache JMeter
- Stress Testing: Gatling
- Profiling: JProfiler/YourKit
- Monitoring: Prometheus/Grafana

### Test Scenarios
```java
@Test
public void loadTest() {
    // Simulate 100 concurrent users
    // Execute for 10 minutes
    // Monitor response times
    // Check error rates
    // Verify resource usage
}
```

## Performance Optimization Tips

### Database
- Use appropriate indexes
- Optimize queries
- Regular maintenance
- Monitor query plans
- Partition large tables

### Application
- Use async processing
- Implement caching
- Pool connections
- Optimize serialization
- Use compression

### Frontend
- Minimize HTTP requests
- Use CDN
- Compress assets
- Lazy loading
- Virtual scrolling

### Network
- Use HTTP/2
- Enable compression
- Minimize payload size
- Use connection pooling
- Implement timeouts

## Scalability Considerations

### Horizontal Scaling
- Stateless design
- Distributed caching
- Load balancing
- Session management
- Database sharding

### Vertical Scaling
- CPU optimization
- Memory utilization
- Disk I/O
- Network capacity
- Connection pooling

## Performance Checklist

### Development
- [ ] Use appropriate data structures
- [ ] Implement caching
- [ ] Optimize database queries
- [ ] Handle concurrent requests
- [ ] Implement timeouts

### Testing
- [ ] Run load tests
- [ ] Monitor resource usage
- [ ] Check response times
- [ ] Verify error handling
- [ ] Test concurrent users

### Deployment
- [ ] Configure JVM properly
- [ ] Set up monitoring
- [ ] Enable metrics collection
- [ ] Configure logging
- [ ] Set up alerts