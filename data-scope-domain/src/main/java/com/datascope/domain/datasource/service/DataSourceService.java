package com.datascope.domain.datasource.service;

import com.datascope.domain.datasource.entity.DataSource;

import java.util.List;

/**
 * 数据源服务接口
 */
public interface DataSourceService {

    /**
     * 创建数据源
     *
     * @param entity   数据源实体
     * @param operator 操作人
     * @return 创建后的数据源
     */
    DataSource create(DataSource entity, String operator);

    /**
     * 更新数据源
     *
     * @param entity   数据源实体
     * @param operator 操作人
     * @return 更新后的数据源
     */
    DataSource update(DataSource entity, String operator);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    DataSource getById(String id);

    /**
     * 根据名称获取数据源
     *
     * @param name 数据源名称
     * @return 数据源实体
     */
    DataSource getByName(String name);

    /**
     * 获取所有数据源
     *
     * @return 数据源实体列表
     */
    List<DataSource> getAll();

    /**
     * 根据类型获取数据源列表
     *
     * @param type 数据源类型
     * @return 数据源实体列表
     */
    List<DataSource> getByType(DataSource.DataSourceType type);

    /**
     * 根据状态获取数据源列表
     *
     * @param status 数据源状态
     * @return 数据源实体列表
     */
    List<DataSource> getByStatus(DataSource.DataSourceStatus status);

    /**
     * 删除数据源
     *
     * @param id       数据源ID
     * @param operator 操作人
     */
    void delete(String id, String operator);

    /**
     * 激活数据源
     *
     * @param id       数据源ID
     * @param operator 操作人
     * @return 更新后的数据源
     */
    DataSource activate(String id, String operator);

    /**
     * 停用数据源
     *
     * @param id       数据源ID
     * @param operator 操作人
     * @return 更新后的数据源
     */
    DataSource deactivate(String id, String operator);

    /**
     * 测试数据源连接
     *
     * @param id 数据源ID
     * @return 是否连接成功
     */
    boolean testConnection(String id);

    /**
     * 同步数据源元数据
     *
     * @param id       数据源ID
     * @param operator 操作人
     * @return 更新后的数据源
     */
    DataSource syncMetadata(String id, String operator);

    /**
     * 检查数据源名称是否存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    boolean checkNameExists(String name);

    /**
     * 根据名称模糊查询数据源列表
     *
     * @param nameLike 数据源名称（模糊匹配）
     * @return 数据源实体列表
     */
    List<DataSource> searchByName(String nameLike);

    /**
     * 根据类型和状态查询数据源列表
     *
     * @param type   数据源类型
     * @param status 数据源状态
     * @return 数据源实体列表
     */
    List<DataSource> getByTypeAndStatus(DataSource.DataSourceType type, DataSource.DataSourceStatus status);
}