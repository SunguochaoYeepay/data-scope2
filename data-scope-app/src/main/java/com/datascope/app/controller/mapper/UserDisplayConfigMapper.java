package com.datascope.app.controller.mapper;

import com.datascope.app.controller.request.UserDisplayConfigRequest;
import com.datascope.app.controller.response.UserDisplayConfigResponse;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 用户显示配置映射器
 */
@Mapper
public interface UserDisplayConfigMapper {

    UserDisplayConfigMapper INSTANCE = Mappers.getMapper(UserDisplayConfigMapper.class);

    /**
     * 请求转DTO
     *
     * @param userId 用户ID
     * @param request 请求对象
     * @return DTO对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "useFrequency", ignore = true)
    @Mapping(target = "lastUsedAt", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    UserDisplayConfigDTO toDTO(String userId, UserDisplayConfigRequest request);

    /**
     * DTO转响应
     *
     * @param dto DTO对象
     * @return 响应对象
     */
    UserDisplayConfigResponse toResponse(UserDisplayConfigDTO dto);
}