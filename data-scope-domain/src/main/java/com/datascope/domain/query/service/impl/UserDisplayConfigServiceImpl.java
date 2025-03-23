package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.domain.query.service.UserDisplayConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * User display configuration service implementation
 *
 * @author dreambt
 */
@Service
@RequiredArgsConstructor
public class UserDisplayConfigServiceImpl implements UserDisplayConfigService {
    private final UserDisplayConfigRepository repository;

    @Override
    @Transactional
    public UserDisplayConfig create(UserDisplayConfig entity) {
        validate(entity);
        entity.setId(UUID.randomUUID().toString());
//        entity.prePersist();
        return repository.save(entity);
    }

    @Override
    @Transactional
    public UserDisplayConfig update(UserDisplayConfig entity) {
        validate(entity);
        Assert.notNull(entity.getId(), "ID must not be null");

        Optional<UserDisplayConfig> existing = repository.findById(entity.getId());
        Assert.isTrue(existing.isPresent(), "Entity not found");

//        entity.preUpdate();
        return repository.save(entity);
    }

    @Override
    public Optional<UserDisplayConfig> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<UserDisplayConfig> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean exists(String id) {
        return repository.existsById(id);
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteAll() {
        // TODO
    }

    @Override
    public void validate(UserDisplayConfig entity) {
        Assert.notNull(entity, "Entity must not be null");
        Assert.hasText(entity.getUserId(), "User ID must not be empty");
        Assert.hasText(entity.getDataSourceId(), "Data source ID must not be empty");
        Assert.hasText(entity.getTableName(), "Table name must not be empty");
        Assert.hasText(entity.getColumnName(), "Column name must not be empty");
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> findByUserId(String userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return repository.findByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        return repository.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    @Transactional
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName) {
        return repository.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName);
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
        repository.copyConfigurations(fromUserId, toUserId);
    }

    @Override
    @Transactional
    public void updateUsage(String id) {
        Optional<UserDisplayConfig> config = repository.findById(id);
        if (config.isPresent()) {
            UserDisplayConfig entity = config.get();
            entity.updateUsage();
            repository.save(entity);
        }
    }
}
