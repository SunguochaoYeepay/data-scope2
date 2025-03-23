package com.datascope.facade.query.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * 用户显示配置Mapper
 */
@Mapper
public interface UserDisplayConfigMapper {

    UserDisplayConfigMapper INSTANCE = Mappers.getMapper(UserDisplayConfigMapper.class);

    /**
     * 实体转DTO
     *
     * @param entity 实体
     * @return DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig entity);

    /**
     * DTO转实体
     *
     * @param dto DTO
     * @return 实体
     */
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);
}