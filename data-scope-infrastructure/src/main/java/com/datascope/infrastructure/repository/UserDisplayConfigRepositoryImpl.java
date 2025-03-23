package com.datascope.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.infrastructure.mybatis.mapper.UserDisplayConfigMapper;

import lombok.RequiredArgsConstructor;

/**
 * User display configuration repository implementation
 * 
 * @author dreambt
 */
@Repository
@RequiredArgsConstructor
public class UserDisplayConfigRepositoryImpl implements UserDisplayConfigRepository {
    private final UserDisplayConfigMapper mapper;

    @Override
    public UserDisplayConfig save(UserDisplayConfig entity) {
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity;
    }

    @Override
    public Optional<UserDisplayConfig> findById(String id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<UserDisplayConfig> findAll() {
        return mapper.findAll();
    }

    @Override
    public void deleteById(String id) {
        mapper.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return mapper.existsById(id);
    }

    @Override
    public long count() {
        return mapper.count();
    }

    @Override
    public void deleteAll() {
        mapper.deleteAll();
    }

    @Override
    public List<UserDisplayConfig> findByUserId(String userId) {
        return mapper.findByUserId(userId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return mapper.findByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        return mapper.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName) {
        return mapper.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName);
    }

    @Override
    public void deleteByUserId(String userId) {
        mapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        mapper.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        mapper.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigurations(String fromUserId, String toUserId) {
        List<UserDisplayConfig> configs = findByUserId(fromUserId);
        for (UserDisplayConfig config : configs) {
            UserDisplayConfig newConfig = new UserDisplayConfig();
            newConfig.setUserId(toUserId);
            newConfig.setDataSourceId(config.getDataSourceId());
            newConfig.setTableName(config.getTableName());
            newConfig.setColumnName(config.getColumnName());
            newConfig.setDisplayName(config.getDisplayName());
            newConfig.setWidth(config.getWidth());
            newConfig.setAlign(config.getAlign());
            newConfig.setFixed(config.getFixed());
            newConfig.setVisible(config.getVisible());
            newConfig.setOrderNum(config.getOrderNum());
            newConfig.setSortable(config.getSortable());
            newConfig.setSearchable(config.getSearchable());
            newConfig.setRequired(config.getRequired());
            newConfig.setMaskType(config.getMaskType());
            newConfig.setMaskConfig(config.getMaskConfig());
            save(newConfig);
        }
    }
}