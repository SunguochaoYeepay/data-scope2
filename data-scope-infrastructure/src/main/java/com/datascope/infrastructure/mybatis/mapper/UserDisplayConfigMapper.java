package com.datascope.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.datascope.domain.query.entity.UserDisplayConfig;

/**
 * 用户显示配置Mapper
 */
@Mapper
public interface UserDisplayConfigMapper {

    /**
     * 插入配置
     *
     * @param config 配置
     */
    void insert(UserDisplayConfig config);

    /**
     * 更新配置
     *
     * @param config 配置
     */
    void update(UserDisplayConfig config);

    /**
     * 根据ID删除配置
     *
     * @param id 配置ID
     */
    void deleteById(@Param("id") String id);

    /**
     * 删除表的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     */
    void deleteByTableName(@Param("userId") String userId,
                          @Param("dataSourceId") String dataSourceId,
                          @Param("tableName") String tableName);

    /**
     * 更新使用信息
     *
     * @param id 配置ID
     */
    void updateUsage(@Param("id") String id);

    /**
     * 获取表的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return 配置列表
     */
    List<UserDisplayConfig> findByTableName(@Param("userId") String userId,
                                          @Param("dataSourceId") String dataSourceId,
                                          @Param("tableName") String tableName);

    /**
     * 获取数据源的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @return 配置列表
     */
    List<UserDisplayConfig> findByDataSourceId(@Param("userId") String userId,
                                             @Param("dataSourceId") String dataSourceId);

    /**
     * 获取最常用的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param limit        限制数量
     * @return 配置列表
     */
    List<UserDisplayConfig> findMostUsed(@Param("userId") String userId,
                                        @Param("dataSourceId") String dataSourceId,
                                        @Param("limit") int limit);

    /**
     * 获取推荐配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return 配置列表
     */
    List<UserDisplayConfig> findRecommended(@Param("userId") String userId,
                                          @Param("dataSourceId") String dataSourceId,
                                          @Param("tableName") String tableName);
}