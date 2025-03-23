package com.datascope.infrastructure.mybatis.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.datascope.domain.query.entity.UserDisplayConfig;

/**
 * User display configuration MyBatis mapper
 * 
 * @author dreambt
 */
@Mapper
public interface UserDisplayConfigMapper {
    /**
     * Insert configuration
     *
     * @param entity Configuration to insert
     */
    void insert(UserDisplayConfig entity);

    /**
     * Update configuration
     *
     * @param entity Configuration to update
     */
    void update(UserDisplayConfig entity);

    /**
     * Find configuration by ID
     *
     * @param id Configuration ID
     * @return Configuration if found
     */
    UserDisplayConfig findById(@Param("id") String id);

    /**
     * Find all configurations
     *
     * @return List of configurations
     */
    List<UserDisplayConfig> findAll();

    /**
     * Delete configuration by ID
     *
     * @param id Configuration ID
     */
    void deleteById(@Param("id") String id);

    /**
     * Check if configuration exists by ID
     *
     * @param id Configuration ID
     * @return true if exists
     */
    boolean existsById(@Param("id") String id);

    /**
     * Count all configurations
     *
     * @return Count of configurations
     */
    long count();

    /**
     * Find configurations by user ID
     *
     * @param userId User ID
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserId(@Param("userId") String userId);

    /**
     * Find configurations by user ID and data source ID
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceId(
            @Param("userId") String userId, @Param("dataSourceId") String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            @Param("userId") String userId, 
            @Param("dataSourceId") String dataSourceId, 
            @Param("tableName") String tableName);

    /**
     * Find configurations by user ID, data source ID, table name and column name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     * @param columnName Column name
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName,
            @Param("columnName") String columnName);

    /**
     * Delete configurations by user ID
     *
     * @param userId User ID
     */
    void deleteByUserId(@Param("userId") String userId);

    /**
     * Delete configurations by user ID and data source ID
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     */
    void deleteByUserIdAndDataSourceId(
            @Param("userId") String userId, @Param("dataSourceId") String dataSourceId);

    /**
     * Delete configurations by user ID, data source ID and table name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     */
    void deleteByUserIdAndDataSourceIdAndTableName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);
}