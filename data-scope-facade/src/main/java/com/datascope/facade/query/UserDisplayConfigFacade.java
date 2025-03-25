package com.datascope.facade.query;

import com.datascope.facade.query.dto.UserDisplayConfigDTO;

import java.util.List;

/**
 * Facade interface for user display configuration
 */
public interface UserDisplayConfigFacade {

    /**
     * Save configuration
     */
    UserDisplayConfigDTO save(UserDisplayConfigDTO config, String operator);

    /**
     * Find configuration by ID
     */
    UserDisplayConfigDTO findById(String id);

    /**
     * Find configurations by user ID
     */
    List<UserDisplayConfigDTO> findByUserId(String userId);

    /**
     * Find configurations by user ID and data source ID
     */
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     */
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    /**
     * Find configurations by user ID, data source ID, table name and column name
     */
    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName);

    /**
     * Delete configuration by ID
     */
    void deleteById(String id);

    /**
     * Delete configurations by user ID
     */
    void deleteByUserId(String userId);

    /**
     * Delete configurations by user ID and data source ID
     */
    void deleteByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Delete configurations by user ID, data source ID and table name
     */
    void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    /**
     * Copy configurations from one user to another
     */
    void copyConfigurations(String fromUserId, String toUserId);

    /**
     * Create new configuration
     */
    UserDisplayConfigDTO create(UserDisplayConfigDTO config, String operator);

    /**
     * Update existing configuration
     */
    UserDisplayConfigDTO update(UserDisplayConfigDTO config, String operator);

    /**
     * Update usage statistics for configurations
     */
    void updateUsageStatistics(List<UserDisplayConfigDTO> configs);

    /**
     * Increment usage count for configuration
     */
    void incrementUsageCount(String id);
}
