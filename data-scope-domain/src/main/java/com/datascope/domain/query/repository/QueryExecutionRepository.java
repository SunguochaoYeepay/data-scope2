package com.datascope.domain.query.repository;

import com.datascope.domain.common.repository.BaseRepository;
import com.datascope.domain.query.model.QueryExecution;

import java.util.List;

/**
 * 查询执行记录仓储接口
 */
public interface QueryExecutionRepository extends BaseRepository<QueryExecution, String> {

    /**
     * 查找用户最近的查询记录
     *
     * @param userId 用户ID
     * @param limit 返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> findRecentByUserId(String userId, int limit);

    /**
     * 查找数据源的查询记录
     *
     * @param dataSourceId 数据源ID
     * @param limit        返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> findByDataSourceId(String dataSourceId, int limit);

    /**
     * 查找正在执行的查询
     *
     * @return 查询执行记录列表
     */
    List<QueryExecution> findRunning();

    /**
     * 统计用户查询次数
     *
     * @param userId 用户ID
     * @return 查询次数
     */
    long countByUserId(String userId);
}
