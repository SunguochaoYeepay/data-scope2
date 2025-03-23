package com.datascope.domain.query.service;

import com.datascope.domain.query.entity.UserDisplayConfig;

import java.util.List;
import java.util.Optional;

public interface IUserDisplayConfigService {

    UserDisplayConfig save(UserDisplayConfig config, String operator);

    UserDisplayConfig update(UserDisplayConfig config, String operator);

    Optional<UserDisplayConfig> findById(String id);

    List<UserDisplayConfig> findAll();

    void deleteById(String id);

    void deleteAll();

    List<UserDisplayConfig> findByUserId(String userId);

    List<UserDisplayConfig> findByDataSourceId(String dataSourceId);

    List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName);

    List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName);

    void deleteByUserId(String userId);

    void deleteByDataSourceId(String dataSourceId);

    void deleteByUserIdAndDataSourceId(String userId, String dataSourceId);

    void deleteByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName);

    void copyConfigs(String fromUserId, String toUserId);

    UserDisplayConfig incrementUsageCount(String id);
}
