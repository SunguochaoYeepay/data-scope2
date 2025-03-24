package com.datascope.app.mapper;

import com.datascope.domain.query.model.QueryExecution;
import com.datascope.facade.query.dto.QueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface QueryMapper {

    QueryMapper INSTANCE = Mappers.getMapper(QueryMapper.class);

    QueryDTO toDTO(QueryExecution queryExecution);

    List<QueryDTO> toDTOList(List<QueryExecution> queryExecutionList);
}