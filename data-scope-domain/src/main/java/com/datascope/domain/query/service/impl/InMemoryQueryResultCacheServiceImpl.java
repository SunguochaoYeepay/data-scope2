package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 基于内存的查询结果缓存服务实现
 */
@Slf4j
@Service
public class InMemoryQueryResultCacheServiceImpl implements QueryResultCacheService {

    // 默认缓存过期时间（秒）
    private static final int DEFAULT_TTL_SECONDS = 3600; // 1小时
    // 缓存数据结构
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    // 调度器用于处理缓存过期
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public InMemoryQueryResultCacheServiceImpl() {
        // 启动定期清理过期缓存的任务
        scheduler.scheduleAtFixedRate(this::cleanExpiredEntries, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public void cacheResult(String queryId, QueryResult result, int ttlSeconds) {
        if (queryId == null || result == null) {
            return;
        }

        int ttl = ttlSeconds > 0 ? ttlSeconds : DEFAULT_TTL_SECONDS;
        cache.put(queryId, new CacheEntry(result, ttl));
        log.debug("Cached query result for query ID: {}, TTL: {} seconds", queryId, ttl);
    }

    @Override
    public Optional<QueryResult> getResult(String queryId) {
        if (queryId == null) {
            return Optional.empty();
        }

        CacheEntry entry = cache.get(queryId);
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                // 如果已过期，从缓存中移除
                cache.remove(queryId);
                log.debug("Removed expired cache entry for query ID: {}", queryId);
            }
            return Optional.empty();
        }

        log.debug("Cache hit for query ID: {}", queryId);
        return Optional.of(entry.result);
    }

    @Override
    public void removeResult(String queryId) {
        if (queryId != null) {
            cache.remove(queryId);
            log.debug("Removed cache entry for query ID: {}", queryId);
        }
    }

    @Override
    public boolean hasResult(String queryId) {
        if (queryId == null) {
            return false;
        }

        CacheEntry entry = cache.get(queryId);
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                // 如果已过期，从缓存中移除
                cache.remove(queryId);
            }
            return false;
        }

        return true;
    }

    @Override
    public long getTtl(String queryId) {
        if (queryId == null) {
            return -1;
        }

        CacheEntry entry = cache.get(queryId);
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                // 如果已过期，从缓存中移除
                cache.remove(queryId);
            }
            return -1;
        }

        return entry.getRemainingTtlSeconds();
    }

    @Override
    public boolean updateTtl(String queryId, int ttlSeconds) {
        if (queryId == null || ttlSeconds <= 0) {
            return false;
        }

        CacheEntry entry = cache.get(queryId);
        if (entry == null || entry.isExpired()) {
            if (entry != null && entry.isExpired()) {
                // 如果已过期，从缓存中移除
                cache.remove(queryId);
            }
            return false;
        }

        entry.updateTtl(ttlSeconds);
        log.debug("Updated TTL for query ID: {}, new TTL: {} seconds", queryId, ttlSeconds);
        return true;
    }

    /**
     * 清理过期的缓存条目
     */
    private void cleanExpiredEntries() {
        int removedCount = 0;
        for (Map.Entry<String, CacheEntry> entry : cache.entrySet()) {
            if (entry.getValue().isExpired()) {
                cache.remove(entry.getKey());
                removedCount++;
            }
        }

        if (removedCount > 0) {
            log.debug("Cleaned {} expired cache entries", removedCount);
        }
    }

    /**
     * 缓存条目，包含查询结果和过期时间
     */
    private static class CacheEntry {
        private final QueryResult result;
        private long expirationTime;

        public CacheEntry(QueryResult result, int ttlSeconds) {
            this.result = result;
            this.expirationTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        public long getRemainingTtlSeconds() {
            long remainingMs = expirationTime - System.currentTimeMillis();
            return remainingMs > 0 ? remainingMs / 1000 : 0;
        }

        public void updateTtl(int ttlSeconds) {
            this.expirationTime = System.currentTimeMillis() + (ttlSeconds * 1000L);
        }
    }
}
