package com.datascope.facade.query.impl;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.service.UserDisplayConfigService;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import com.datascope.facade.query.mapper.UserDisplayConfigFacadeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户显示配置Facade实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDisplayConfigFacadeImpl implements UserDisplayConfigFacade {

    private final UserDisplayConfigService service;
    private final UserDisplayConfigFacadeMapper mapper;

    @Override
    public UserDisplayConfigDTO saveConfig(UserDisplayConfigDTO dto) {
        UserDisplayConfig entity = mapper.toEntity(dto);
        entity = service.saveConfig(entity);
        return mapper.toDTO(entity);
    }

    @Override
    public List<UserDisplayConfigDTO> saveConfigs(List<UserDisplayConfigDTO> dtos) {
        List<UserDisplayConfig> entities = mapper.toEntities(dtos);
        entities = service.saveConfigs(entities);
        return mapper.toDTOs(entities);
    }

    @Override
    public List<UserDisplayConfigDTO> getTableConfigs(String userId, String dataSourceId, String tableName) {
        List<UserDisplayConfig> entities = service.getTableConfigs(userId, dataSourceId, tableName);
        return mapper.toDTOs(entities);
    }

    @Override
    public List<UserDisplayConfigDTO> getDataSourceConfigs(String userId, String dataSourceId) {
        List<UserDisplayConfig> entities = service.getDataSourceConfigs(userId, dataSourceId);
        return mapper.toDTOs(entities);
    }

    @Override
    public void deleteConfig(String id) {
        service.deleteConfig(id);
    }

    @Override
    public void deleteTableConfigs(String userId, String dataSourceId, String tableName) {
        service.deleteTableConfigs(userId, dataSourceId, tableName);
    }

    @Override
    public void recordConfigUsage(String id) {
        service.recordConfigUsage(id);
    }

    @Override
    public List<UserDisplayConfigDTO> getMostUsedConfigs(String userId, String dataSourceId, int limit) {
        List<UserDisplayConfig> entities = service.getMostUsedConfigs(userId, dataSourceId, limit);
        return mapper.toDTOs(entities);
    }

    @Override
    public List<UserDisplayConfigDTO> recommendConfigs(String userId, String dataSourceId, String tableName) {
        List<UserDisplayConfig> entities = service.recommendConfigs(userId, dataSourceId, tableName);
        return mapper.toDTOs(entities);
    }

    @Override
    public List<UserDisplayConfigDTO> copyConfigsFromUser(String fromUserId, String toUserId,
                                                        String dataSourceId, String tableName) {
        List<UserDisplayConfig> entities = service.copyConfigsFromUser(fromUserId, toUserId,
                dataSourceId, tableName);
        return mapper.toDTOs(entities);
    }
}