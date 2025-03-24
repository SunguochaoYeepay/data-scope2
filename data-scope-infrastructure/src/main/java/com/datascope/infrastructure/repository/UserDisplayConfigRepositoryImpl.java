package com.datascope.infrastructure.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.infrastructure.mybatis.mapper.UserDisplayConfigMapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Getter
@Setter
public class UserDisplayConfigRepositoryImpl implements UserDisplayConfigRepository {

    @Setter(onMethod_ = @Autowired)
    private UserDisplayConfigMapper userDisplayConfigMapper;

    @Override
    public UserDisplayConfig save(UserDisplayConfig config) {
        if (config.getId() == null) {
            config.setId(UUID.randomUUID().toString());
            userDisplayConfigMapper.insert(config);
        } else {
            userDisplayConfigMapper.update(config);
        }
        return config;
    }

    @Override
    public Optional<UserDisplayConfig> findById(String id) {
        return Optional.ofNullable(userDisplayConfigMapper.selectById(id));
    }

    @Override
    public List<UserDisplayConfig> findByUserId(String userId) {
        return userDisplayConfigMapper.selectByUserId(userId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return userDisplayConfigMapper.selectByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        return userDisplayConfigMapper.selectByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName) {
        return userDisplayConfigMapper.selectByUserIdAndDataSourceIdAndTableNameAndColumnName(
            userId, dataSourceId, tableName, columnName);
    }

    @Override
    public void deleteById(String id) {
        userDisplayConfigMapper.deleteById(id);
    }

    @Override
    public void deleteByUserId(String userId) {
        userDisplayConfigMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        userDisplayConfigMapper.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        userDisplayConfigMapper.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigurations(String fromUserId, String toUserId) {
        List<UserDisplayConfig> sourceConfigs = findByUserId(fromUserId);
        for (UserDisplayConfig config : sourceConfigs) {
            UserDisplayConfig newConfig = config.copy();
            newConfig.setId(null);
            newConfig.setUserId(toUserId);
            newConfig.setUsageCount(0);
            newConfig.setLastUsedAt(null);
            save(newConfig);
        }
    }
}
