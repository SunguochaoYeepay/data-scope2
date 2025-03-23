package com.datascope.app.facade.impl;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.service.UserDisplayConfigService;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import com.datascope.facade.query.mapper.UserDisplayConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserDisplayConfigFacadeImpl implements UserDisplayConfigFacade {
    private final UserDisplayConfigService service;
    private final UserDisplayConfigMapper mapper;

    public UserDisplayConfigFacadeImpl(UserDisplayConfigService service, UserDisplayConfigMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Override
    public UserDisplayConfigDTO save(UserDisplayConfigDTO dto, String operator) {
        UserDisplayConfig config = mapper.toEntity(dto);
        config = service.save(config, operator);
        return mapper.toDto(config);
    }

    @Override
    public UserDisplayConfigDTO findById(String id) {
        return service.findById(id)
            .map(mapper::toDto)
            .orElse(null);
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserId(String userId) {
        return service.findByUserId(userId).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return service.findByUserIdAndDataSourceId(userId, dataSourceId).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        return service.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName) {
        return service.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName).stream()
            .map(mapper::toDto)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        service.deleteById(id);
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
    public void incrementUsageCount(String id) {
        service.incrementUsageCount(id);
    }

    @Override
    public UserDisplayConfigDTO create(UserDisplayConfigDTO dto, String operator) {
        UserDisplayConfig config = mapper.toEntity(dto);
        config = service.create(config, operator);
        return mapper.toDto(config);
    }

    @Override
    public UserDisplayConfigDTO update(UserDisplayConfigDTO dto, String operator) {
        UserDisplayConfig config = mapper.toEntity(dto);
        config = service.update(config, operator);
        return mapper.toDto(config);
    }

    @Override
    public void updateUsageStatistics(List<UserDisplayConfigDTO> dtos) {
        List<UserDisplayConfig> configs = dtos.stream()
            .map(mapper::toEntity)
            .collect(Collectors.toList());
        service.updateUsageStatistics(configs);
    }
}
