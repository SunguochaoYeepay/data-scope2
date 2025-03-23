package com.datascope.domain.query.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for user display configuration
 */
public interface UserDisplayConfigRepository {

    /**
     * Save configuration
     */
    UserDisplayConfig save(UserDisplayConfig config);

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
}
