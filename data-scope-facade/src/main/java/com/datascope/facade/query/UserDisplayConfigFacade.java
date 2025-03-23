package com.datascope.facade.query;

import java.util.List;

import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * User display configuration facade interface
 * 
 * @author dreambt
 */
public interface UserDisplayConfigFacade {
    /**
     * Create configuration
     *
     * @param dto Configuration DTO
     * @return Created configuration
     */
    UserDisplayConfigDTO create(UserDisplayConfigDTO dto);

    /**
     * Update configuration
     *
     * @param dto Configuration DTO
     * @return Updated configuration
     */
    UserDisplayConfigDTO update(UserDisplayConfigDTO dto);

    /**
     * Get configuration by ID
     *
     * @param id Configuration ID
     * @return Configuration if found
     */
    UserDisplayConfigDTO getById(String id);

    /**
     * Get all configurations
     *
     * @return List of configurations
     */
    List<UserDisplayConfigDTO> getAll();

    /**
     * Delete configuration by ID
     *
     * @param id Configuration ID
     */
    void deleteById(String id);

    /**
     * Find configurations by user ID
     *
     * @param userId User ID
     * @return List of configurations
     */
    List<UserDisplayConfigDTO> findByUserId(String userId);

    /**
     * Find configurations by user ID and data source ID
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @return List of configurations
     */
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     *
     * @param userId User ID
     * @param dataSourceId Data source ID
     * @param tableName Table name
     * @return List of configurations
     */
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
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
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
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
    void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    /**
     * Copy configurations from one user to another
     *
     * @param fromUserId Source user ID
     * @param toUserId Target user ID
     */
    void copyConfigurations(String fromUserId, String toUserId);

    /**
     * Update usage statistics
     *
     * @param id Configuration ID
     */
    void updateUsage(String id);
}
