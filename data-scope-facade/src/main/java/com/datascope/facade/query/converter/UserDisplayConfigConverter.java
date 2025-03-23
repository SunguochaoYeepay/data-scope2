package com.datascope.facade.query.converter;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * 用户显示配置转换器
 */
@Mapper
public interface UserDisplayConfigConverter {

    UserDisplayConfigConverter INSTANCE = Mappers.getMapper(UserDisplayConfigConverter.class);

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    UserDisplayConfig toEntity(UserDisplayConfigDTO dto);

    /**
     * 实体转DTO
     *
     * @param entity 实体对象
     * @return DTO对象
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig entity);

    /**
     * DTO列表转实体列表
     *
     * @param dtos DTO对象列表
     * @return 实体对象列表
     */
    List<UserDisplayConfig> toEntityList(List<UserDisplayConfigDTO> dtos);

    /**
     * 实体列表转DTO列表
     *
     * @param entities 实体对象列表
     * @return DTO对象列表
     */
    List<UserDisplayConfigDTO> toDTOList(List<UserDisplayConfig> entities);
}