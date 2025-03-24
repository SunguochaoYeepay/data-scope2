package com.datascope.domain.query.service;

import com.datascope.domain.query.model.QueryResult;

import java.util.Optional;

/**
 * 查询结果缓存服务接口
 */
public interface QueryResultCacheService {

    /**
     * 将查询结果存入缓存
     *
     * @param queryId    查询ID
     * @param result     查询结果
     * @param ttlSeconds 缓存过期时间（秒）
     */
    void cacheResult(String queryId, QueryResult result, int ttlSeconds);

    /**
     * 从缓存中获取查询结果
     *
     * @param queryId 查询ID
     * @return 查询结果（如果存在）
     */
    Optional<QueryResult> getResult(String queryId);

    /**
     * 从缓存中删除查询结果
     *
     * @param queryId 查询ID
     */
    void removeResult(String queryId);

    /**
     * 检查缓存中是否存在查询结果
     *
     * @param queryId 查询ID
     * @return 是否存在
     */
    boolean hasResult(String queryId);

    /**
     * 获取缓存中查询结果的剩余生存时间（秒）
     *
     * @param queryId 查询ID
     * @return 剩余生存时间（秒），如果不存在则返回-1
     */
    long getTtl(String queryId);

    /**
     * 更新缓存中查询结果的生存时间
     *
     * @param queryId    查询ID
     * @param ttlSeconds 新的生存时间（秒）
     * @return 是否更新成功
     */
    boolean updateTtl(String queryId, int ttlSeconds);
}
