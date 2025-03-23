package com.datascope.infrastructure.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.infrastructure.mybatis.mapper.UserDisplayConfigMapper;

import lombok.RequiredArgsConstructor;

/**
 * 用户显示配置仓储实现
 */
@Repository
@RequiredArgsConstructor
public class UserDisplayConfigRepositoryImpl implements UserDisplayConfigRepository {

    private final UserDisplayConfigMapper mapper;

    @Override
    public UserDisplayConfig save(UserDisplayConfig config) {
        if (config.getId() == null) {
            mapper.insert(config);
        } else {
            mapper.update(config);
        }
        return config;
    }

    @Override
    public List<UserDisplayConfig> saveAll(List<UserDisplayConfig> configs) {
        for (UserDisplayConfig config : configs) {
            save(config);
        }
        return configs;
    }

    @Override
    public List<UserDisplayConfig> findByTableName(String userId, String dataSourceId, String tableName) {
        return mapper.findByTableName(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> findByDataSourceId(String userId, String dataSourceId) {
        return mapper.findByDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByTableName(String userId, String dataSourceId, String tableName) {
        mapper.deleteByTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void updateUsage(String id) {
        mapper.updateUsage(id);
    }

    @Override
    public List<UserDisplayConfig> findMostUsed(String userId, String dataSourceId, int limit) {
        return mapper.findMostUsed(userId, dataSourceId, limit);
    }

    @Override
    public List<UserDisplayConfig> findRecommended(String userId, String dataSourceId, String tableName) {
        return mapper.findRecommended(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> copyFromUser(String fromUserId, String toUserId, String dataSourceId, String tableName) {
        List<UserDisplayConfig> configs = mapper.findByTableName(fromUserId, dataSourceId, tableName);
        for (UserDisplayConfig config : configs) {
            config.setId(null);
            config.setUserId(toUserId);
            config.setUsageCount(0L);
            config.setLastUsedAt(null);
            save(config);
        }
        return configs;
    }
}