package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.domain.query.service.IUserDisplayConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserDisplayConfigServiceImpl implements IUserDisplayConfigService {

    private final UserDisplayConfigRepository repository;

    @Override
    public UserDisplayConfig save(UserDisplayConfig config, String operator) {
        config.init(operator);
        return repository.save(config);
    }

    @Override
    public UserDisplayConfig update(UserDisplayConfig config, String operator) {
        config.update(operator);
        return repository.save(config);
    }

    @Override
    public Optional<UserDisplayConfig> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<UserDisplayConfig> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<UserDisplayConfig> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<UserDisplayConfig> findByDataSourceId(String dataSourceId) {
        return repository.findByDataSourceId(dataSourceId);
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
    public void deleteByUserId(String userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public void deleteByDataSourceId(String dataSourceId) {
        repository.deleteByDataSourceId(dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        repository.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        repository.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigs(String fromUserId, String toUserId) {
        repository.copyConfigs(fromUserId, toUserId);
    }

    @Override
    public UserDisplayConfig incrementUsageCount(String id) {
        var config = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Config not found: " + id));
        config.incrementUsageCount();
        return repository.save(config);
    }
}
