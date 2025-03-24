package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.domain.query.model.QueryExecution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 查询执行记录Mapper接口
 */
@Mapper
public interface QueryExecutionMapper {

    /**
     * 插入查询执行记录
     *
     * @param queryExecution 查询执行记录
     * @return 影响行数
     */
    int insert(QueryExecution queryExecution);

    /**
     * 更新查询执行记录
     *
     * @param queryExecution 查询执行记录
     * @return 影响行数
     */
    int update(QueryExecution queryExecution);

    /**
     * 根据ID查询查询执行记录
     *
     * @param id 查询执行记录ID
     * @return 查询执行记录
     */
    QueryExecution findById(@Param("id") String id);

    /**
     * 判断ID是否存在
     *
     * @param id 查询执行记录ID
     * @return 是否存在
     */
    boolean existsById(@Param("id") String id);

    /**
     * 查询所有查询执行记录
     *
     * @return 查询执行记录列表
     */
    List<QueryExecution> findAll();

    /**
     * 根据ID列表查询查询执行记录
     *
     * @param ids ID列表
     * @return 查询执行记录列表
     */
    List<QueryExecution> findAllById(@Param("ids") List<String> ids);

    /**
     * 统计查询执行记录数量
     *
     * @return 记录数量
     */
    long count();

    /**
     * 根据ID删除查询执行记录
     *
     * @param id 查询执行记录ID
     * @return 影响行数
     */
    int deleteById(@Param("id") String id);

    /**
     * 删除所有查询执行记录
     *
     * @return 影响行数
     */
    int deleteAll();

    /**
     * 根据ID列表删除查询执行记录
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteAllById(@Param("ids") List<String> ids);

    /**
     * 查找用户最近的查询记录
     *
     * @param userId 用户ID
     * @param limit  返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> findRecentByUserId(@Param("userId") String userId, @Param("limit") int limit);

    /**
     * 查找数据源的查询记录
     *
     * @param dataSourceId 数据源ID
     * @param limit        返回记录数量限制
     * @return 查询执行记录列表
     */
    List<QueryExecution> findByDataSourceId(@Param("dataSourceId") String dataSourceId, @Param("limit") int limit);

    /**
     * 根据状态查询查询执行记录
     *
     * @param status 状态
     * @return 查询执行记录列表
     */
    List<QueryExecution> findByStatus(@Param("status") String status);

    /**
     * 统计用户查询次数
     *
     * @param userId 用户ID
     * @return 查询次数
     */
    long countByUserId(@Param("userId") String userId);
}
