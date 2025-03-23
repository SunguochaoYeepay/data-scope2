package com.datascope.domain.datasource.repository;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.valueobject.DataSourceStatus;

import java.util.List;
import java.util.Optional;

/**
 * 数据源仓储接口
 */
public interface DataSourceRepository {
    
    /**
     * 保存数据源
     *
     * @param dataSource 数据源实体
     * @return 保存后的数据源实体
     */
    DataSource save(DataSource dataSource);
    
    /**
     * 根据ID查找数据源
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    Optional<DataSource> findById(String id);
    
    /**
     * 根据名称查找数据源
     *
     * @param name 数据源名称
     * @return 数据源实体
     */
    Optional<DataSource> findByName(String name);
    
    /**
     * 查找所有数据源
     *
     * @return 数据源列表
     */
    List<DataSource> findAll();
    
    /**
     * 根据状态查找数据源
     *
     * @param status 数据源状态
     * @return 数据源列表
     */
    List<DataSource> findByStatus(DataSourceStatus status);
    
    /**
     * 删除数据源
     *
     * @param id 数据源ID
     */
    void deleteById(String id);
    
    /**
     * 检查数据源名称是否已存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 更新数据源状态
     *
     * @param id 数据源ID
     * @param status 新状态
     * @return 更新后的数据源实体
     */
    Optional<DataSource> updateStatus(String id, DataSourceStatus status);
    
    /**
     * 查找需要同步的数据源
     *
     * @param syncIntervalMinutes 同步间隔（分钟）
     * @return 需要同步的数据源列表
     */
    List<DataSource> findNeedSync(int syncIntervalMinutes);
}