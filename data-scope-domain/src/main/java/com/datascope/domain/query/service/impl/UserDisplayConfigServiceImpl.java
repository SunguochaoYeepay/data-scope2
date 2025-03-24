package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.domain.query.service.UserDisplayConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDisplayConfigServiceImpl implements UserDisplayConfigService {

    private final UserDisplayConfigRepository repository;

    @Override
    @Transactional
    public UserDisplayConfig save(UserDisplayConfig config, String operator) {
        LocalDateTime now = LocalDateTime.now();
        if (config.getId() == null) {
            config = config.copy();
            config.setId(UUID.randomUUID().toString());
            config.setCreatedBy(operator);
            config.setCreatedTime(now);
            config.setUpdatedBy(operator);
            config.setUpdatedTime(now);
        } else {
            config = config.copy();
            config.setUpdatedBy(operator);
            config.setUpdatedTime(now);
        }
        return repository.save(config);
    }

    @Override
    public Optional<UserDisplayConfig> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<UserDisplayConfig> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return repository.findByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        return repository.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName) {
        return repository.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            userId, dataSourceId, tableName, columnName);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        repository.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        repository.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    @Transactional
    public void copyConfigurations(String fromUserId, String toUserId) {
        List<UserDisplayConfig> sourceConfigs = repository.findByUserId(fromUserId);
        for (UserDisplayConfig config : sourceConfigs) {
            UserDisplayConfig newConfig = config.copy();
            config.setId(null);
            config.setUserId(toUserId);
            config.setUsageCount(0);
            config.setLastUsedTime(null);
            repository.save(newConfig);
        }
    }

    @Override
    @Transactional
    public UserDisplayConfig create(UserDisplayConfig config, String operator) {
        LocalDateTime now = LocalDateTime.now();
        config.setId(UUID.randomUUID().toString());
        config.setCreatedBy(operator);
        config.setCreatedTime(now);
        config.setUpdatedBy(operator);
        config.setUpdatedTime(now);
        return repository.save(config);
    }

    @Override
    @Transactional
    public UserDisplayConfig update(UserDisplayConfig config, String operator) {
        LocalDateTime now = LocalDateTime.now();
        config = config.copy();
        config.setUpdatedBy(operator);
        config.setUpdatedTime(now);
        return repository.save(config);
    }

    @Override
    @Transactional
    public void updateUsageStatistics(List<UserDisplayConfig> configs) {
        configs.forEach(config -> {
            config.setUsageCount(config.getUsageCount() + 1);
            config.setLastUsedTime(LocalDateTime.now());
            repository.save(config);
        });
    }

    @Override
    @Transactional
    public void incrementUsageCount(String id) {
        repository.findById(id).ifPresent(config -> {
            config = config.copy();
            config.setUsageCount(config.getUsageCount() + 1);
            config.setLastUsedTime(LocalDateTime.now());
            repository.save(config);
        });
    }
}
