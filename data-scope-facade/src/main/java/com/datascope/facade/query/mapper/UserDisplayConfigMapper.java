package com.datascope.facade.query.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDisplayConfigMapper {
    UserDisplayConfigDTO toDto(UserDisplayConfig entity);
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);
}
