package com.datascope.domain.query.repository;

import java.util.List;

import com.datascope.domain.common.repository.BaseRepository;
import com.datascope.domain.query.entity.UserDisplayConfig;

/**
 * User display configuration repository interface
 * 
 * @author dreambt
 */
public interface UserDisplayConfigRepository extends BaseRepository<UserDisplayConfig, String> {
    /**
     * Find configurations by user ID
     *
     * @param userId User ID
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserId(String userId);

    /**
     * Find configurations by user ID and data source ID
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     * @return List of configurations
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

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
            String userId, String dataSourceId, String tableName, String columnName);

    /**
     * Delete configurations by user ID
     *
     * @param userId User ID
     */
    void deleteByUserId(String userId);

    /**
     * Delete configurations by user ID and data source ID
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     */
    void deleteByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Delete configurations by user ID, data source ID and table name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     */
    void deleteByUserIdAndDataSourceIdAndTableName(String userId, String dataSourceId, String tableName);

    /**
     * Copy configurations from one user to another
     *
     * @param fromUserId Source user ID
     * @param toUserId Target user ID
     */
    void copyConfigurations(String fromUserId, String toUserId);
}