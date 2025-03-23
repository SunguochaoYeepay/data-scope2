package com.datascope.infrastructure.repository;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.infrastructure.mybatis.mapper.DataSourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
    public DataSource findByName(String name) {
        return mapper.selectByName(name);
    }

    @Override
    public List<DataSource> findByType(DataSource.DataSourceType type) {
        return mapper.selectByType(type);
    }

    @Override
    public List<DataSource> findByStatus(DataSource.DataSourceStatus status) {
        return mapper.selectByStatus(status);
    }

    @Override
    public List<DataSource> findByTypeAndStatus(DataSource.DataSourceType type, DataSource.DataSourceStatus status) {
        return mapper.selectByTypeAndStatus(type, status);
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
