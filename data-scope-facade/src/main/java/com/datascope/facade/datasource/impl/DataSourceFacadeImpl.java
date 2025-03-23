package com.datascope.facade.datasource.impl;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.service.DataSourceService;
import com.datascope.facade.datasource.DataSourceFacade;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import com.datascope.facade.datasource.mapper.DataSourceFacadeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据源门面实现类
 */
@Service
@RequiredArgsConstructor
public class DataSourceFacadeImpl implements DataSourceFacade {

    private final DataSourceService service;
    private final DataSourceFacadeMapper mapper;

    @Override
    public DataSourceDTO create(DataSourceDTO dto, String operator) {
        DataSource entity = mapper.toEntity(dto);
        entity = service.create(entity, operator);
        return mapper.toDTO(entity);
    }

    @Override
    public DataSourceDTO update(DataSourceDTO dto, String operator) {
        DataSource entity = service.getById(dto.getId());
        mapper.updateEntity(dto, entity);
        entity = service.update(entity, operator);
        return mapper.toDTO(entity);
    }

    @Override
    public DataSourceDTO getById(String id) {
        return mapper.toDTO(service.getById(id));
    }

    @Override
    public DataSourceDTO getByName(String name) {
        return mapper.toDTO(service.getByName(name));
    }

    @Override
    public List<DataSourceDTO> getAll() {
        return mapper.toDTOList(service.getAll());
    }

    @Override
    public void delete(String id, String operator) {
        service.delete(id, operator);
    }

    @Override
    public DataSourceDTO activate(String id, String operator) {
        return mapper.toDTO(service.activate(id, operator));
    }

    @Override
    public DataSourceDTO deactivate(String id, String operator) {
        return mapper.toDTO(service.deactivate(id, operator));
    }

    @Override
    public boolean testConnection(String id) {
        return service.testConnection(id);
    }

    @Override
    public DataSourceDTO syncMetadata(String id, String operator) {
        return mapper.toDTO(service.syncMetadata(id, operator));
    }

    @Override
    public boolean checkNameExists(String name) {
        return service.checkNameExists(name);
    }

    @Override
    public List<DataSourceDTO> searchByName(String nameLike) {
        return mapper.toDTOList(service.searchByName(nameLike));
    }

    @Override
    public List<DataSourceDTO> getByType(DataSource.DataSourceType type) {
        return mapper.toDTOList(service.getByType(type));
    }

    @Override
    public List<DataSourceDTO> getByStatus(DataSource.DataSourceStatus status) {
        return mapper.toDTOList(service.getByStatus(status));
    }

    @Override
    public List<DataSourceDTO> getByTypeAndStatus(DataSource.DataSourceType type, DataSource.DataSourceStatus status) {
        return mapper.toDTOList(service.getByTypeAndStatus(type, status));
    }
}