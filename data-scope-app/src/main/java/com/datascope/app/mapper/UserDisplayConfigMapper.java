package com.datascope.app.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.datascope.app.controller.request.UserDisplayConfigRequest;
import com.datascope.app.controller.response.UserDisplayConfigResponse;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

/**
 * 用户显示配置Mapper
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDisplayConfigMapper {

    /**
     * 请求转DTO
     *
     * @param request 请求
     * @return DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfigRequest request);

    /**
     * DTO转响应
     *
     * @param dto DTO
     * @return 响应
     */
    UserDisplayConfigResponse toResponse(UserDisplayConfigDTO dto);

    /**
     * 请求列表转DTO列表
     *
     * @param requests 请求列表
     * @return DTO列表
     */
    List<UserDisplayConfigDTO> toDTOs(List<UserDisplayConfigRequest> requests);

    /**
     * DTO列表转响应列表
     *
     * @param dtos DTO列表
     * @return 响应列表
     */
    List<UserDisplayConfigResponse> toResponses(List<UserDisplayConfigDTO> dtos);
}