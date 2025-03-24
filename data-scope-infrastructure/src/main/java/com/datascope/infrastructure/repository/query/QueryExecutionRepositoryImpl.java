package com.datascope.infrastructure.repository.query;

import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.infrastructure.mybatis.mapper.QueryExecutionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 查询执行记录仓储实现类
 */
@Repository
@RequiredArgsConstructor
public class QueryExecutionRepositoryImpl implements QueryExecutionRepository {

    private final QueryExecutionMapper queryExecutionMapper;

    @Override
    public QueryExecution save(QueryExecution entity) {
        if (existsById(entity.getId())) {
            queryExecutionMapper.update(entity);
        } else {
            queryExecutionMapper.insert(entity);
        }
        return entity;
    }

    @Override
    public List<QueryExecution> saveAll(List<QueryExecution> entities) {
        entities.forEach(this::save);
        return entities;
    }

    @Override
    public Optional<QueryExecution> findById(String id) {
        return Optional.ofNullable(queryExecutionMapper.findById(id));
    }

    @Override
    public boolean existsById(String id) {
        return queryExecutionMapper.existsById(id);
    }

    @Override
    public List<QueryExecution> findAll() {
        return queryExecutionMapper.findAll();
    }

    @Override
    public List<QueryExecution> findAllById(List<String> ids) {
        return queryExecutionMapper.findAllById(ids);
    }

    @Override
    public long count() {
        return queryExecutionMapper.count();
    }

    @Override
    public void deleteById(String id) {
        queryExecutionMapper.deleteById(id);
    }

    @Override
    public void deleteAll() {
        queryExecutionMapper.deleteAll();
    }

    @Override
    public void deleteAllById(List<String> ids) {
        queryExecutionMapper.deleteAllById(ids);
    }

    @Override
    public List<QueryExecution> findRecentByUserId(String userId, int limit) {
        return queryExecutionMapper.findRecentByUserId(userId, limit);
    }

    @Override
    public List<QueryExecution> findByDataSourceId(String dataSourceId, int limit) {
        return queryExecutionMapper.findByDataSourceId(dataSourceId, limit);
    }

    @Override
    public List<QueryExecution> findRunning() {
        return queryExecutionMapper.findByStatus("RUNNING");
    }

    @Override
    public long countByUserId(String userId) {
        return queryExecutionMapper.countByUserId(userId);
    }
}