package com.datascope.facade.query;

import com.datascope.facade.query.dto.UserDisplayConfigDTO;

import java.util.List;

public interface UserDisplayConfigFacade {
    UserDisplayConfigDTO save(UserDisplayConfigDTO dto, String operator);

    UserDisplayConfigDTO findById(String id);

    List<UserDisplayConfigDTO> findByUserId(String userId);

    List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId);

    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName);

    void deleteById(String id);

    void deleteByUserId(String userId);

    void deleteByUserIdAndDataSourceId(String userId, String dataSourceId);

    void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName);

    void copyConfigurations(String fromUserId, String toUserId);

    void incrementUsageCount(String id);

    UserDisplayConfigDTO create(UserDisplayConfigDTO dto, String operator);

    UserDisplayConfigDTO update(UserDisplayConfigDTO dto, String operator);

    void updateUsageStatistics(List<UserDisplayConfigDTO> dtos);
}
