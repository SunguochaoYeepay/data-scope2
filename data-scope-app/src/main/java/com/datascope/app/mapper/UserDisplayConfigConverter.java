package com.datascope.app.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Facade mapper for user display configuration
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDisplayConfigConverter {

    /**
     * Convert domain object to DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig domain);

    /**
     * Convert DTO to domain object
     */
    UserDisplayConfig toDomain(UserDisplayConfigDTO dto);

    /**
     * Convert domain object list to DTO list
     */
    List<UserDisplayConfigDTO> toDTOList(List<UserDisplayConfig> domains);

    /**
     * Convert DTO list to domain object list
     */
    List<UserDisplayConfig> toDomainList(List<UserDisplayConfigDTO> dtos);
}
