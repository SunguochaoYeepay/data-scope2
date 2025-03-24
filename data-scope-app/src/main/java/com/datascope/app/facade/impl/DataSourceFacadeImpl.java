package com.datascope.app.facade.impl;

import com.datascope.app.mapper.DataSourceMapper;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.service.DataSourceService;
import com.datascope.facade.datasource.DataSourceFacade;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import com.datascope.facade.datasource.dto.TestConnectionRequest;
import com.datascope.facade.datasource.enums.DataSourceStatus;
import com.datascope.facade.datasource.enums.DataSourceType;
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
    private final DataSourceMapper mapper;

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
    public boolean testConnection(TestConnectionRequest request) {
        DataSource connectionInfo = new DataSource();
        connectionInfo.setName(request.getName());
        connectionInfo.setType(convertType(request.getType()));
        connectionInfo.setHost(request.getHost());
        connectionInfo.setPort(request.getPort());
        connectionInfo.setDatabase(request.getDatabase());
        connectionInfo.setSchema(request.getSchema());
        connectionInfo.setUsername(request.getUsername());
        connectionInfo.setPassword(request.getPassword());

        return service.testConnection(connectionInfo);
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
    public List<DataSourceDTO> getByType(DataSourceType type) {
        return mapper.toDTOList(service.getByType(convertType(type)));
    }

    @Override
    public List<DataSourceDTO> getByStatus(DataSourceStatus status) {
        return mapper.toDTOList(service.getByStatus(convertStatus(status)));
    }

    @Override
    public List<DataSourceDTO> getByTypeAndStatus(DataSourceType type, DataSourceStatus status) {
        return mapper.toDTOList(service.getByTypeAndStatus(convertType(type), convertStatus(status)));
    }

    /**
     * 转换数据源类型
     *
     * @param type 门面层数据源类型
     * @return 领域层数据源类型
     */
    private com.datascope.domain.datasource.enums.DataSourceType convertType(DataSourceType type) {
        return com.datascope.domain.datasource.enums.DataSourceType.valueOf(type.name());
    }

    /**
     * 转换数据源状态
     *
     * @param status 门面层数据源状态
     * @return 领域层数据源状态
     */
    private com.datascope.domain.datasource.enums.DataSourceStatus convertStatus(DataSourceStatus status) {
        return com.datascope.domain.datasource.enums.DataSourceStatus.valueOf(status.name());
    }
}
