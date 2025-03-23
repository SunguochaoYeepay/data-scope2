package com.datascope.domain.query.repository;

import com.datascope.domain.common.repository.BaseRepository;
import com.datascope.domain.query.model.QueryExecution;

import java.util.List;
import java.util.UUID;

/**
 * 查询执行记录仓储接口
 */
public interface QueryExecutionRepository extends BaseRepository<QueryExecution, UUID> {

    /**
     * 查询用户最近的查询记录
     *
     * @param userId 用户ID
     * @param limit  返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> findRecentByUserId(String userId, int limit);
}
