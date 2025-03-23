package com.datascope.facade.query;

import java.util.List;

import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * 用户显示配置Facade接口
 */
public interface UserDisplayConfigFacade {

    /**
     * 保存配置
     *
     * @param dto 配置DTO
     * @return 配置DTO
     */
    UserDisplayConfigDTO saveConfig(UserDisplayConfigDTO dto);

    /**
     * 批量保存配置
     *
     * @param dtos 配置DTO列表
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> saveConfigs(List<UserDisplayConfigDTO> dtos);

    /**
     * 获取表的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> getTableConfigs(String userId, String dataSourceId, String tableName);

    /**
     * 获取数据源的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> getDataSourceConfigs(String userId, String dataSourceId);

    /**
     * 删除配置
     *
     * @param id 配置ID
     */
    void deleteConfig(String id);

    /**
     * 删除表的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     */
    void deleteTableConfigs(String userId, String dataSourceId, String tableName);

    /**
     * 记录配置使用
     *
     * @param id 配置ID
     */
    void recordConfigUsage(String id);

    /**
     * 获取最常用的配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param limit        限制数量
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> getMostUsedConfigs(String userId, String dataSourceId, int limit);

    /**
     * 获取推荐配置
     *
     * @param userId       用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> recommendConfigs(String userId, String dataSourceId, String tableName);

    /**
     * 从其他用户复制配置
     *
     * @param fromUserId   源用户ID
     * @param toUserId     目标用户ID
     * @param dataSourceId 数据源ID
     * @param tableName    表名
     * @return 配置DTO列表
     */
    List<UserDisplayConfigDTO> copyConfigsFromUser(String fromUserId, String toUserId,
                                                 String dataSourceId, String tableName);
}