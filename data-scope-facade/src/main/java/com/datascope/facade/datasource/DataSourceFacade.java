package com.datascope.facade.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.facade.datasource.dto.DataSourceDTO;

import java.util.List;

/**
 * 数据源门面接口
 */
public interface DataSourceFacade {

    /**
     * 创建数据源
     *
     * @param dto      数据源DTO
     * @param operator 操作人
     * @return 创建后的数据源DTO
     */
    DataSourceDTO create(DataSourceDTO dto, String operator);

    /**
     * 更新数据源
     *
     * @param dto      数据源DTO
     * @param operator 操作人
     * @return 更新后的数据源DTO
     */
    DataSourceDTO update(DataSourceDTO dto, String operator);

    /**
     * 根据ID获取数据源
     *
     * @param id 数据源ID
     * @return 数据源DTO
     */
    DataSourceDTO getById(String id);

    /**
     * 根据名称获取数据源
     *
     * @param name 数据源名称
     * @return 数据源DTO
     */
    DataSourceDTO getByName(String name);

    /**
     * 获取所有数据源
     *
     * @return 数据源DTO列表
     */
    List<DataSourceDTO> getAll();

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
     * @return 更新后的数据源DTO
     */
    DataSourceDTO activate(String id, String operator);

    /**
     * 停用数据源
     *
     * @param id       数据源ID
     * @param operator 操作人
     * @return 更新后的数据源DTO
     */
    DataSourceDTO deactivate(String id, String operator);

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
     * @return 更新后的数据源DTO
     */
    DataSourceDTO syncMetadata(String id, String operator);

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
     * @return 数据源DTO列表
     */
    List<DataSourceDTO> searchByName(String nameLike);

    /**
     * 根据类型获取数据源列表
     *
     * @param type 数据源类型
     * @return 数据源DTO列表
     */
    List<DataSourceDTO> getByType(DataSource.DataSourceType type);

    /**
     * 根据状态获取数据源列表
     *
     * @param status 数据源状态
     * @return 数据源DTO列表
     */
    List<DataSourceDTO> getByStatus(DataSource.DataSourceStatus status);

    /**
     * 根据类型和状态获取数据源列表
     *
     * @param type   数据源类型
     * @param status 数据源状态
     * @return 数据源DTO列表
     */
    List<DataSourceDTO> getByTypeAndStatus(DataSource.DataSourceType type, DataSource.DataSourceStatus status);
}