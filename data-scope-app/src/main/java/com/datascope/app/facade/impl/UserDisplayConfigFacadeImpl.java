package com.datascope.app.facade.impl;

import com.datascope.app.mapper.UserDisplayConfigConverter;
import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.service.UserDisplayConfigService;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDisplayConfigFacadeImpl implements UserDisplayConfigFacade {

    private final UserDisplayConfigService service;
    private final UserDisplayConfigConverter mapper;

    @Override
    public UserDisplayConfigDTO save(UserDisplayConfigDTO config, String operator) {
        UserDisplayConfig domain = mapper.toDomain(config);
        return mapper.toDTO(service.save(domain, operator));
    }

    @Override
    public UserDisplayConfigDTO findById(String id) {
        return service.findById(id)
            .map(mapper::toDTO)
            .orElse(null);
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserId(String userId) {
        return service.findByUserId(userId).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return service.findByUserIdAndDataSourceId(userId, dataSourceId).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        return service.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName).stream()
            .map(mapper::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName) {
        return service.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName).stream()
            .map(mapper::toDTO)
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
    public UserDisplayConfigDTO create(UserDisplayConfigDTO config, String operator) {
        return mapper.toDTO(service.create(mapper.toDomain(config), operator));
    }

    @Override
    public UserDisplayConfigDTO update(UserDisplayConfigDTO config, String operator) {
        return mapper.toDTO(service.update(mapper.toDomain(config), operator));
    }

    @Override
    public void updateUsageStatistics(List<UserDisplayConfigDTO> configs) {
        List<UserDisplayConfig> domains = configs.stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
        service.updateUsageStatistics(domains);
    }

    @Override
    public void incrementUsageCount(String id) {
        service.incrementUsageCount(id);
    }
}
