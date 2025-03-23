package com.datascope.facade.query;

import com.datascope.facade.query.dto.QueryDTO;
import com.datascope.facade.query.dto.QueryResultDTO;

import java.util.List;
import java.util.Map;

/**
 * 查询门面接口
 */
public interface QueryFacade {

    /**
     * 创建查询
     *
     * @param dto      查询DTO
     * @param operator 操作人
     * @return 创建后的查询DTO
     */
    QueryDTO create(QueryDTO dto, String operator);

    /**
     * 更新查询
     *
     * @param dto      查询DTO
     * @param operator 操作人
     * @return 更新后的查询DTO
     */
    QueryDTO update(QueryDTO dto, String operator);

    /**
     * 根据ID获取查询
     *
     * @param id 查询ID
     * @return 查询DTO
     */
    QueryDTO getById(String id);

    /**
     * 根据名称获取查询
     *
     * @param name 查询名称
     * @return 查询DTO
     */
    QueryDTO getByName(String name);

    /**
     * 获取所有查询
     *
     * @return 查询DTO列表
     */
    List<QueryDTO> getAll();

    /**
     * 删除查询
     *
     * @param id       查询ID
     * @param operator 操作人
     */
    void delete(String id, String operator);

    /**
     * 执行查询
     *
     * @param id         查询ID
     * @param parameters 查询参数
     * @param operator   操作人
     * @return 查询结果
     */
    QueryResultDTO execute(String id, Map<String, Object> parameters, String operator);

    /**
     * 检查查询名称是否存在
     *
     * @param name 查询名称
     * @return 是否存在
     */
    boolean checkNameExists(String name);

    /**
     * 根据名称模糊查询查询列表
     *
     * @param nameLike 查询名称（模糊匹配）
     * @return 查询DTO列表
     */
    List<QueryDTO> searchByName(String nameLike);

    /**
     * 根据数据源ID获取查询列表
     *
     * @param dataSourceId 数据源ID
     * @return 查询DTO列表
     */
    List<QueryDTO> getByDataSourceId(String dataSourceId);
}