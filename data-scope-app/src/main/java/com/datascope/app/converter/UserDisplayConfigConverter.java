package com.datascope.app.converter;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Facade mapper for user display configuration
 */
@Mapper
public interface UserDisplayConfigConverter {

    UserDisplayConfigConverter INSTANCE = Mappers.getMapper(UserDisplayConfigConverter.class);

    /**
     * Convert domain object to DTO
     */
    UserDisplayConfigDTO toDTO(UserDisplayConfig userDisplayConfig);

    /**
     * Convert DTO to domain object
     */
    UserDisplayConfig toDomain(UserDisplayConfigDTO userDisplayConfigDTO);

    /**
     * Convert domain object list to DTO list
     */
    List<UserDisplayConfigDTO> toDTOList(List<UserDisplayConfig> userDisplayConfigs);

    /**
     * Convert DTO list to domain object list
     */
    List<UserDisplayConfig> toDomainList(List<UserDisplayConfigDTO> userDisplayConfigDTOS);
}
