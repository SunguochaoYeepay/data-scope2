package com.datascope.infrastructure.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.infrastructure.mapper.UserDisplayConfigMapper;
import com.datascope.infrastructure.mybatis.mapper.UserDisplayConfigMyBatisMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserDisplayConfigRepositoryImpl implements UserDisplayConfigRepository {

    private final UserDisplayConfigMapper mapper;
    private final UserDisplayConfigMyBatisMapper mybatisMapper;

    @Override
    public UserDisplayConfig save(UserDisplayConfig entity) {
        var entityToSave = mapper.toEntity(entity);
        if (entityToSave.getId() == null) {
            entityToSave.setId(UUID.randomUUID().toString());
            mybatisMapper.insert(entityToSave);
        } else {
            mybatisMapper.update(entityToSave);
        }
        return mapper.toDomain(entityToSave);
    }

    @Override
    public List<UserDisplayConfig> saveAll(List<UserDisplayConfig> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<UserDisplayConfig> findById(String id) {
        var entity = mybatisMapper.selectById(id);
        return Optional.ofNullable(mapper.toDomain(entity));
    }

    @Override
    public List<UserDisplayConfig> findAll() {
        return mapper.toDomainList(mybatisMapper.selectAll());
    }

    @Override
    public List<UserDisplayConfig> findAllById(List<String> ids) {
        return mapper.toDomainList(mybatisMapper.selectByIds(ids));
    }

    @Override
    public long count() {
        return mybatisMapper.count();
    }

    @Override
    public boolean existsById(String id) {
        return mybatisMapper.existsById(id);
    }

    @Override
    public void deleteById(String id) {
        mybatisMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        mybatisMapper.deleteAll();
    }

    @Override
    public void deleteAllById(List<String> ids) {
        mybatisMapper.deleteByIds(ids);
    }

    @Override
    public List<UserDisplayConfig> findByUserId(String userId) {
        return mapper.toDomainList(mybatisMapper.selectByUserId(userId));
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return mapper.toDomainList(mybatisMapper.selectByUserIdAndDataSourceId(userId, dataSourceId));
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        return mapper.toDomainList(
            mybatisMapper.selectByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName));
    }

    @Override
    public List<UserDisplayConfig> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName) {
        return mapper.toDomainList(
            mybatisMapper.selectByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName));
    }

    @Override
    public void deleteByUserId(String userId) {
        mybatisMapper.deleteByUserId(userId);
    }

    @Override
    public void deleteByDataSourceId(String dataSourceId) {
        mybatisMapper.deleteByDataSourceId(dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        mybatisMapper.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        mybatisMapper.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigs(String fromUserId, String toUserId) {
        mybatisMapper.copyConfigs(fromUserId, toUserId);
    }

    @Override
    public List<UserDisplayConfig> findByDataSourceId(String dataSourceId) {
        return mapper.toDomainList(mybatisMapper.selectByDataSourceId(dataSourceId));
    }
}
