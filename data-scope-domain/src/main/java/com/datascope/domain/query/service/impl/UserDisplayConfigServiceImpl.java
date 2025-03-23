package com.datascope.domain.query.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.domain.query.service.UserDisplayConfigService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户显示配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDisplayConfigServiceImpl implements UserDisplayConfigService {

    private final UserDisplayConfigRepository repository;

    @Override
    @Transactional
    public UserDisplayConfig saveConfig(UserDisplayConfig config) {
        return repository.save(config);
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> saveConfigs(List<UserDisplayConfig> configs) {
        return repository.saveAll(configs);
    }

    @Override
    public List<UserDisplayConfig> getTableConfigs(String userId, String dataSourceId, String tableName) {
        return repository.findByTableName(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> getDataSourceConfigs(String userId, String dataSourceId) {
        return repository.findByDataSourceId(userId, dataSourceId);
    }

    @Override
    @Transactional
    public void deleteConfig(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteTableConfigs(String userId, String dataSourceId, String tableName) {
        repository.deleteByTableName(userId, dataSourceId, tableName);
    }

    @Override
    @Transactional
    public void recordConfigUsage(String id) {
        repository.updateUsage(id);
    }

    @Override
    public List<UserDisplayConfig> getMostUsedConfigs(String userId, String dataSourceId, int limit) {
        return repository.findMostUsed(userId, dataSourceId, limit);
    }

    @Override
    public List<UserDisplayConfig> recommendConfigs(String userId, String dataSourceId, String tableName) {
        return repository.findRecommended(userId, dataSourceId, tableName);
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> copyConfigsFromUser(String fromUserId, String toUserId,
                                                     String dataSourceId, String tableName) {
        return repository.copyFromUser(fromUserId, toUserId, dataSourceId, tableName);
    }
}