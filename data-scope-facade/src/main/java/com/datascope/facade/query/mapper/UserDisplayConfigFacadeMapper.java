package com.datascope.facade.query.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * User display configuration facade mapper
 * 
 * @author dreambt
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserDisplayConfigFacadeMapper {
    /**
     * Convert entity to DTO
     *
     * @param entity Entity
     * @return DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig entity);

    /**
     * Convert DTO to entity
     *
     * @param dto DTO
     * @return Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);

    /**
     * Update entity from DTO
     *
     * @param dto DTO
     * @param entity Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    void updateEntity(UserDisplayConfigDTO dto, @MappingTarget UserDisplayConfig entity);

    /**
     * Convert entity list to DTO list
     *
     * @param entities Entity list
     * @return DTO list
     */
    List<UserDisplayConfigDTO> toDTOList(List<UserDisplayConfig> entities);

    /**
     * Convert DTO list to entity list
     *
     * @param dtos DTO list
     * @return Entity list
     */
    List<UserDisplayConfig> toEntityList(List<UserDisplayConfigDTO> dtos);
}
