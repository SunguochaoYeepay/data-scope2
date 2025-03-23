package com.datascope.facade.query.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.service.UserDisplayConfigService;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import com.datascope.facade.query.mapper.UserDisplayConfigFacadeMapper;

import lombok.RequiredArgsConstructor;

/**
 * User display configuration facade implementation
 * 
 * @author dreambt
 */
@Service
@RequiredArgsConstructor
public class UserDisplayConfigFacadeImpl implements UserDisplayConfigFacade {
    private final UserDisplayConfigService service;
    private final UserDisplayConfigFacadeMapper mapper;

    @Override
    public UserDisplayConfigDTO create(UserDisplayConfigDTO dto) {
        UserDisplayConfig entity = mapper.toEntity(dto);
        entity = service.create(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public UserDisplayConfigDTO update(UserDisplayConfigDTO dto) {
        UserDisplayConfig entity = service.getById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Entity not found"));
        mapper.updateEntity(dto, entity);
        entity = service.update(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public UserDisplayConfigDTO getById(String id) {
        return service.getById(id)
                .map(mapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<UserDisplayConfigDTO> getAll() {
        return mapper.toDTOList(service.getAll());
    }

    @Override
    public void deleteById(String id) {
        service.deleteById(id);
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserId(String userId) {
        return mapper.toDTOList(service.findByUserId(userId));
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return mapper.toDTOList(service.findByUserIdAndDataSourceId(userId, dataSourceId));
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        return mapper.toDTOList(
                service.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName));
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            String userId, String dataSourceId, String tableName, String columnName) {
        return mapper.toDTOList(
                service.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                        userId, dataSourceId, tableName, columnName));
    }

    @Override
    public void deleteByUserId(String userId) {
        service.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        service.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
            String userId, String dataSourceId, String tableName) {
        service.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigurations(String fromUserId, String toUserId) {
        service.copyConfigurations(fromUserId, toUserId);
    }

    @Override
    public void updateUsage(String id) {
        service.updateUsage(id);
    }
}
