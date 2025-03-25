package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis Mapper for UserDisplayConfig
 */
@Mapper
public interface UserDisplayConfigMapper {

    /**
     * Insert a new configuration
     */
    int insert(UserDisplayConfig config);

    /**
     * Update an existing configuration
     */
    int update(UserDisplayConfig config);

    /**
     * Find configuration by ID
     */
    UserDisplayConfig selectById(@Param("id") String id);

    /**
     * Find configurations by user ID
     */
    List<UserDisplayConfig> selectByUserId(@Param("userId") String userId);

    /**
     * Find configurations by user ID and data source ID
     */
    List<UserDisplayConfig> selectByUserIdAndDataSourceId(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     */
    List<UserDisplayConfig> selectByUserIdAndDataSourceIdAndTableName(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);

    /**
     * Find configurations by user ID, data source ID, table name and column name
     */
    List<UserDisplayConfig> selectByUserIdAndDataSourceIdAndTableNameAndColumnName(
            @Param("userId") String userId,
            @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName,
            @Param("columnName") String columnName);

    /**
     * Delete configuration by ID
     */
    int deleteById(@Param("id") String id);

    /**
     * Delete configurations by user ID
     */
    int deleteByUserId(@Param("userId") String userId);

    /**
     * Delete configurations by user ID and data source ID
     */
    int deleteByUserIdAndDataSourceId(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId);

    /**
     * Delete configurations by user ID, data source ID and table name
     */
    int deleteByUserIdAndDataSourceIdAndTableName(
        @Param("userId") String userId,
        @Param("dataSourceId") String dataSourceId,
            @Param("tableName") String tableName);

    /**
     * Batch insert configurations
     */
    int batchInsert(@Param("configs") List<UserDisplayConfig> configs);
}
