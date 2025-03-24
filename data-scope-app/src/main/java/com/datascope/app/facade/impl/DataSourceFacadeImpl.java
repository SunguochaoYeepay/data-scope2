package com.datascope.app.facade.impl;

import com.datascope.app.converter.DataSourceConverter;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.service.DataSourceService;
import com.datascope.facade.datasource.DataSourceFacade;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import com.datascope.facade.datasource.dto.TestConnectionRequest;
import com.datascope.facade.datasource.enums.DataSourceStatus;
import com.datascope.facade.datasource.enums.DataSourceType;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据源门面实现类
 */
@Service
public class DataSourceFacadeImpl implements DataSourceFacade {

    @Setter(onMethod_ = @Autowired)
    private DataSourceService dataSourceService;

    @Setter(onMethod_ = @Autowired)
    private DataSourceConverter dataSourceConverter;

    @Override
    public DataSourceDTO create(DataSourceDTO dto, String operator) {
        DataSource entity = dataSourceConverter.toEntity(dto);
        entity = dataSourceService.create(entity, operator);
        return dataSourceConverter.toDTO(entity);
    }

    @Override
    public DataSourceDTO update(DataSourceDTO dto, String operator) {
        DataSource entity = dataSourceService.getById(dto.getId());
        dataSourceConverter.updateEntity(dto, entity);
        entity = dataSourceService.update(entity, operator);
        return dataSourceConverter.toDTO(entity);
    }

    @Override
    public DataSourceDTO getById(String id) {
        return dataSourceConverter.toDTO(dataSourceService.getById(id));
    }

    @Override
    public DataSourceDTO getByName(String name) {
        return dataSourceConverter.toDTO(dataSourceService.getByName(name));
    }

    @Override
    public List<DataSourceDTO> getAll() {
        return dataSourceConverter.toDTOList(dataSourceService.getAll());
    }

    @Override
    public void delete(String id, String operator) {
        dataSourceService.delete(id, operator);
    }

    @Override
    public DataSourceDTO activate(String id, String operator) {
        return dataSourceConverter.toDTO(dataSourceService.activate(id, operator));
    }

    @Override
    public DataSourceDTO deactivate(String id, String operator) {
        return dataSourceConverter.toDTO(dataSourceService.deactivate(id, operator));
    }

    @Override
    public boolean testConnection(String id) {
        return dataSourceService.testConnection(id);
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

        return dataSourceService.testConnection(connectionInfo);
    }

    @Override
    public DataSourceDTO syncMetadata(String id, String operator) {
        return dataSourceConverter.toDTO(dataSourceService.syncMetadata(id, operator));
    }

    @Override
    public boolean checkNameExists(String name) {
        return dataSourceService.checkNameExists(name);
    }

    @Override
    public List<DataSourceDTO> searchByName(String nameLike) {
        return dataSourceConverter.toDTOList(dataSourceService.searchByName(nameLike));
    }

    @Override
    public List<DataSourceDTO> getByType(DataSourceType type) {
        return dataSourceConverter.toDTOList(dataSourceService.getByType(convertType(type)));
    }

    @Override
    public List<DataSourceDTO> getByStatus(DataSourceStatus status) {
        return dataSourceConverter.toDTOList(dataSourceService.getByStatus(convertStatus(status)));
    }

    @Override
    public List<DataSourceDTO> getByTypeAndStatus(DataSourceType type, DataSourceStatus status) {
        return dataSourceConverter.toDTOList(dataSourceService.getByTypeAndStatus(convertType(type), convertStatus(status)));
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
