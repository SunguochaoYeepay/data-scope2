package com.datascope.facade.query.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper for converting between UserDisplayConfig entity and DTO
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserDisplayConfigFacadeMapper {

    /**
     * Convert entity to DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig entity);

    /**
     * Convert DTO to entity
     */
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);

    /**
     * Update entity from DTO
     */
    void updateEntity(UserDisplayConfigDTO dto, @MappingTarget UserDisplayConfig entity);

    /**
     * Convert entity list to DTO list
     */
    List<UserDisplayConfigDTO> toDTOList(List<UserDisplayConfig> entities);

    /**
     * Convert DTO list to entity list
     */
    List<UserDisplayConfig> toEntityList(List<UserDisplayConfigDTO> dtos);
}
