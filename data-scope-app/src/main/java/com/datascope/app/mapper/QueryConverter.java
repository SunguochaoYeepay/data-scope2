package com.datascope.app.mapper;

import com.datascope.domain.query.model.QueryExecution;
import com.datascope.facade.query.dto.QueryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface QueryConverter {

    QueryConverter INSTANCE = Mappers.getMapper(QueryConverter.class);

    @Mapping(source = "dataSourceId.value", target = "dataSourceId")
    QueryDTO toDTO(QueryExecution queryExecution);

    List<QueryDTO> toDTOList(List<QueryExecution> queryExecutionList);
}
