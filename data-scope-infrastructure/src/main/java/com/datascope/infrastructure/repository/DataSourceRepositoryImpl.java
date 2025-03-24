package com.datascope.infrastructure.repository;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.infrastructure.mybatis.mapper.DataSourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {

    private final DataSourceMapper mapper;

    @Override
    public DataSource save(DataSource entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity;
    }

    @Override
    public List<DataSource> saveAll(List<DataSource> entities) {
        for (DataSource entity : entities) {
            save(entity);
        }
        return entities;
    }

    @Override
    public Optional<DataSource> findById(String id) {
        return Optional.ofNullable(mapper.selectById(id));
    }

    @Override
    public List<DataSource> findAll() {
        return mapper.selectAll();
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
    public List<DataSource> findAllById(List<String> ids) {
        return mapper.findAllById(ids);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        mapper.deleteAllById(ids);
    }

    @Override
    public DataSource findByName(String name) {
        return mapper.selectByName(name);
    }

    @Override
    public List<DataSource> findByType(DataSourceType type) {
        return mapper.selectByType(type);
    }

    @Override
    public List<DataSource> findByStatus(DataSourceStatus status) {
        return mapper.selectByStatus(status);
    }

    @Override
    public List<DataSource> findByTypeAndStatus(DataSourceType type, DataSourceStatus status) {
        return mapper.selectByTypeAndStatus(type, status);
    }

    @Override
    public boolean existsByName(String name) {
        return mapper.existsByName(name);
    }

    @Override
    public List<DataSource> findByNameLike(String nameLike) {
        return mapper.selectByNameLike(nameLike);
    }

    @Override
    public List<DataSource> findByHost(String host) {
        return mapper.selectByHost(host);
    }

    @Override
    public List<DataSource> findByDatabase(String database) {
        return mapper.selectByDatabase(database);
    }

    @Override
    public List<DataSource> findByLastSyncStatus(SyncStatus syncStatus) {
        return mapper.selectByLastSyncStatus(syncStatus);
    }
}
