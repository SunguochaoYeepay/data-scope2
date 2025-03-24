package com.datascope.facade.query;

import com.datascope.facade.query.dto.QueryDTO;

import java.util.List;

/**
 * 查询门面接口
 */
public interface QueryFacade {

    /**
     * 执行查询
     *
     * @param query        查询语句
     * @param dataSourceId 数据源ID
     * @return 查询结果DTO
     */
    QueryDTO executeQuery(String query, String dataSourceId);

    /**
     * 根据ID获取查询
     *
     * @param id 查询ID
     * @return 查询DTO
     */
    QueryDTO getById(String id);

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
}
