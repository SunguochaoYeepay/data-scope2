package com.datascope.domain.datasource.service;

import java.util.List;
import java.util.Optional;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.valueobject.DataSourceStatus;

/**
 * 数据源领域服务接口
 */
public interface DataSourceService {
    
    /**
     * 创建数据源
     *
     * @param dataSource 数据源实体
     * @return 创建后的数据源实体
     * @throws IllegalArgumentException 如果数据源名称已存在
     */
    DataSource createDataSource(DataSource dataSource);
    
    /**
     * 更新数据源
     *
     * @param dataSource 数据源实体
     * @return 更新后的数据源实体
     * @throws IllegalArgumentException 如果数据源不存在
     */
    DataSource updateDataSource(DataSource dataSource);
    
    /**
     * 删除数据源
     *
     * @param id 数据源ID
     * @throws IllegalArgumentException 如果数据源不存在
     */
    void deleteDataSource(String id);
    
    /**
     * 获取数据源
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    Optional<DataSource> getDataSource(String id);
    
    /**
     * 获取所有数据源
     *
     * @return 数据源列表
     */
    List<DataSource> getAllDataSources();
    
    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 连接是否成功
     * @throws IllegalArgumentException 如果数据源不存在
     */
    boolean testConnection(String id);
    
    /**
     * 同步数据源元数据
     *
     * @param id 数据源ID
     * @return 同步是否成功
     * @throws IllegalArgumentException 如果数据源不存在
     */
    boolean syncMetadata(String id);
    
    /**
     * 获取需要同步的数据源列表
     *
     * @param syncIntervalMinutes 同步间隔（分钟）
     * @return 需要同步的数据源列表
     */
    List<DataSource> getNeedSyncDataSources(int syncIntervalMinutes);
    
    /**
     * 更新数据源状态
     *
     * @param id 数据源ID
     * @param status 新状态
     * @return 更新后的数据源实体
     * @throws IllegalArgumentException 如果数据源不存在
     */
    DataSource updateDataSourceStatus(String id, DataSourceStatus status);
    
    /**
     * 验证数据源配置
     *
     * @param dataSource 数据源实体
     * @return 验证结果消息列表
     */
    List<String> validateDataSource(DataSource dataSource);
}