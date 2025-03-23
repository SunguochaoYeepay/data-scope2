package com.datascope.domain.query.service;

import com.datascope.domain.query.entity.UserDisplayConfig;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for user display configuration
 */
public interface UserDisplayConfigService {

    /**
     * Save configuration
     */
    UserDisplayConfig save(UserDisplayConfig config, String operator);

    /**
     * Find configuration by ID
     */
    Optional<UserDisplayConfig> findById(String id);

    /**
     * Find configurations by user ID
     */
    List<UserDisplayConfig> findByUserId(String userId);

    /**
     * Find configurations by user ID and data source ID
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    /**
     * Find configurations by user ID, data source ID and table name
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    /**
     * Find configurations by user ID, data source ID, table name and column name
     */
    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
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
    UserDisplayConfig create(UserDisplayConfig config, String operator);

    /**
     * Update existing configuration
     */
    UserDisplayConfig update(UserDisplayConfig config, String operator);

    /**
     * Update usage statistics for configurations
     */
    void updateUsageStatistics(List<UserDisplayConfig> configs);

    /**
     * Increment usage count for configuration
     */
    void incrementUsageCount(String id);
}
