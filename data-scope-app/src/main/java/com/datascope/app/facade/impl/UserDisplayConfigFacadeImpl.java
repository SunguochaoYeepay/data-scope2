package com.datascope.app.facade.impl;

import com.datascope.app.converter.UserDisplayConfigConverter;
import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.service.UserDisplayConfigService;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDisplayConfigFacadeImpl implements UserDisplayConfigFacade {

    @Setter(onMethod_ = @Autowired)
    private UserDisplayConfigService userDisplayConfigService;

    @Setter(onMethod_ = @Autowired)
    private UserDisplayConfigConverter userDisplayConfigConverter;

    @Override
    public UserDisplayConfigDTO save(UserDisplayConfigDTO config, String operator) {
        UserDisplayConfig domain = userDisplayConfigConverter.toDomain(config);
        return userDisplayConfigConverter.toDTO(userDisplayConfigService.save(domain, operator));
    }

    @Override
    public UserDisplayConfigDTO findById(String id) {
        return userDisplayConfigService.findById(id)
            .map(userDisplayConfigConverter::toDTO)
            .orElse(null);
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserId(String userId) {
        return userDisplayConfigService.findByUserId(userId).stream()
            .map(userDisplayConfigConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceId(String userId, String dataSourceId) {
        return userDisplayConfigService.findByUserIdAndDataSourceId(userId, dataSourceId).stream()
            .map(userDisplayConfigConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        return userDisplayConfigService.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName).stream()
            .map(userDisplayConfigConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserDisplayConfigDTO> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
        String userId, String dataSourceId, String tableName, String columnName) {
        return userDisplayConfigService.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                userId, dataSourceId, tableName, columnName).stream()
            .map(userDisplayConfigConverter::toDTO)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        userDisplayConfigService.deleteById(id);
    }

    @Override
    public void deleteByUserId(String userId) {
        userDisplayConfigService.deleteByUserId(userId);
    }

    @Override
    public void deleteByUserIdAndDataSourceId(String userId, String dataSourceId) {
        userDisplayConfigService.deleteByUserIdAndDataSourceId(userId, dataSourceId);
    }

    @Override
    public void deleteByUserIdAndDataSourceIdAndTableName(
        String userId, String dataSourceId, String tableName) {
        userDisplayConfigService.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
    }

    @Override
    public void copyConfigurations(String fromUserId, String toUserId) {
        userDisplayConfigService.copyConfigurations(fromUserId, toUserId);
    }

    @Override
    public UserDisplayConfigDTO create(UserDisplayConfigDTO config, String operator) {
        return userDisplayConfigConverter.toDTO(userDisplayConfigService.create(userDisplayConfigConverter.toDomain(config), operator));
    }

    @Override
    public UserDisplayConfigDTO update(UserDisplayConfigDTO config, String operator) {
        return userDisplayConfigConverter.toDTO(userDisplayConfigService.update(userDisplayConfigConverter.toDomain(config), operator));
    }

    @Override
    public void updateUsageStatistics(List<UserDisplayConfigDTO> configs) {
        List<UserDisplayConfig> domains = configs.stream()
            .map(userDisplayConfigConverter::toDomain)
            .collect(Collectors.toList());
        userDisplayConfigService.updateUsageStatistics(domains);
    }

    @Override
    public void incrementUsageCount(String id) {
        userDisplayConfigService.incrementUsageCount(id);
    }
}
