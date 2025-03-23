package com.datascope.facade.query.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * 用户显示配置Facade Mapper
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDisplayConfigFacadeMapper {

    /**
     * DTO转实体
     *
     * @param dto DTO
     * @return 实体
     */
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);

    /**
     * 实体转DTO
     *
     * @param entity 实体
     * @return DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig entity);

    /**
     * DTO列表转实体列表
     *
     * @param dtos DTO列表
     * @return 实体列表
     */
    List<UserDisplayConfig> toEntities(List<UserDisplayConfigDTO> dtos);

    /**
     * 实体列表转DTO列表
     *
     * @param entities 实体列表
     * @return DTO列表
     */
    List<UserDisplayConfigDTO> toDTOs(List<UserDisplayConfig> entities);
}