package com.datascope.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.infrastructure.mybatis.mapper.DataSourceMapper;

import lombok.RequiredArgsConstructor;

/**
 * 数据源仓储实现
 * 
 * @author dreambt
 */
@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {
    private final DataSourceMapper mapper;

    @Override
    public DataSource save(DataSource entity) {
        if (entity.getId() == null) {
            mapper.insert(entity);
        } else {
            mapper.update(entity);
        }
        return entity;
    }

    @Override
    public Optional<DataSource> findById(String id) {
        return Optional.ofNullable(mapper.findById(id));
    }

    @Override
    public List<DataSource> findAll() {
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
    public List<DataSource> findByName(String name) {
        return mapper.findByName(name);
    }

    @Override
    public List<DataSource> findByType(DataSourceType type) {
        return mapper.findByType(type);
    }

    @Override
    public List<DataSource> findByStatus(DataSourceStatus status) {
        return mapper.findByStatus(status);
    }

    @Override
    public List<DataSource> findByNameAndType(String name, DataSourceType type) {
        return mapper.findByNameAndType(name, type);
    }

    @Override
    public List<DataSource> findByNameAndStatus(String name, DataSourceStatus status) {
        return mapper.findByNameAndStatus(name, status);
    }

    @Override
    public List<DataSource> findByTypeAndStatus(DataSourceType type, DataSourceStatus status) {
        return mapper.findByTypeAndStatus(type, status);
    }

    @Override
    public boolean existsByName(String name) {
        return mapper.existsByName(name);
    }

    @Override
    public long countByType(DataSourceType type) {
        return mapper.countByType(type);
    }

    @Override
    public long countByStatus(DataSourceStatus status) {
        return mapper.countByStatus(status);
    }
}