package com.datascope.app.facade.impl;

import com.datascope.app.mapper.QueryConverter;
import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.service.QueryExecutionService;
import com.datascope.domain.query.service.QueryService;
import com.datascope.domain.query.service.SqlExecutionEngine;
import com.datascope.facade.query.QueryFacade;
import com.datascope.facade.query.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 查询门面实现类
 */
@Service
@RequiredArgsConstructor
public class QueryFacadeImpl implements QueryFacade {

    private final QueryExecutionService service;
    private final QueryService queryService;
    private final QueryConverter mapper;
    private final SqlExecutionEngine sqlExecutionEngine;

    @Override
    public QueryDTO executeQuery(String query, String dataSourceId) {
        // TODO: 实现查询执行逻辑
        sqlExecutionEngine.execute(new DataSourceId(dataSourceId), query, Map.of());
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setQueryText(query);
        queryDTO.setDataSourceId(dataSourceId);
        return queryDTO;
    }

    @Override
    public QueryDTO getById(String id) {
        return service.getById(id).map(mapper::toDTO).orElse(null);
    }

    @Override
    public List<QueryDTO> getAll() {
        return service.getRecentByUserId("user", 10).stream().map(mapper::toDTO).toList();
    }

    @Override
    public void delete(String id, String operator) {
        queryService.delete(id, operator);
    }
}
