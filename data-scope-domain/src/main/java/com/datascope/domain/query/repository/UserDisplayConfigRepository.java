package com.datascope.domain.query.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;

import java.util.List;
import java.util.Optional;

public interface UserDisplayConfigRepository {
    UserDisplayConfig save(UserDisplayConfig entity);

    List<UserDisplayConfig> saveAll(List<UserDisplayConfig> entities);

    Optional<UserDisplayConfig> findById(String id);

    List<UserDisplayConfig> findAll();

    List<UserDisplayConfig> findAllById(List<String> ids);

    long count();

    boolean existsById(String id);

    void deleteById(String id);

    void deleteAll();

    void deleteAllById(List<String> ids);

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
}
