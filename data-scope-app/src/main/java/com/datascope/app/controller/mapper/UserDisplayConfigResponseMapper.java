package com.datascope.app.controller.mapper;

import com.datascope.app.controller.response.UserDisplayConfigResponse;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper for converting between UserDisplayConfigDTO and Response
 */
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface UserDisplayConfigResponseMapper {

    /**
     * Convert DTO to response
     */
    UserDisplayConfigResponse toResponse(UserDisplayConfigDTO dto);

    /**
     * Convert DTO list to response list
     */
    List<UserDisplayConfigResponse> toResponseList(List<UserDisplayConfigDTO> dtos);
}
