package com.datascope.app.controller.mapper;

import com.datascope.app.controller.request.UserDisplayConfigRequest;
import com.datascope.app.controller.response.UserDisplayConfigResponse;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * User display configuration mapper
 *
 * @author dreambt
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserDisplayConfigMapper {
    /**
     * Convert request to DTO
     *
     * @param request Request object
     * @return DTO object
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
//    @Mapping(target = "nonce", ignore = true)
//    @Mapping(target = "createdTime", ignore = true)
//    @Mapping(target = "createdBy", ignore = true)
//    @Mapping(target = "modifiedTime", ignore = true)
//    @Mapping(target = "modifiedBy", ignore = true)
    UserDisplayConfigDTO toDTO(UserDisplayConfigRequest request);

    /**
     * Convert DTO to response
     *
     * @param dto DTO object
     * @return Response object
     */
    UserDisplayConfigResponse toResponse(UserDisplayConfigDTO dto);

    /**
     * Convert request list to DTO list
     *
     * @param requests Request list
     * @return DTO list
     */
    List<UserDisplayConfigDTO> toDTOs(List<UserDisplayConfigRequest> requests);

    /**
     * Convert DTO list to response list
     *
     * @param dtos DTO list
     * @return Response list
     */
    List<UserDisplayConfigResponse> toResponses(List<UserDisplayConfigDTO> dtos);
}
