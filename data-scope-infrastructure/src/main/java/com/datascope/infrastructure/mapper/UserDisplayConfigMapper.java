package com.datascope.infrastructure.mapper;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.infrastructure.entity.UserDisplayConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserDisplayConfigMapper {

    UserDisplayConfigEntity toEntity(UserDisplayConfig domain);

    UserDisplayConfig toDomain(UserDisplayConfigEntity entity);

    List<UserDisplayConfigEntity> toEntityList(List<UserDisplayConfig> domainList);

    List<UserDisplayConfig> toDomainList(List<UserDisplayConfigEntity> entityList);

    void updateEntity(UserDisplayConfig domain, @MappingTarget UserDisplayConfigEntity entity);

    void updateDomain(UserDisplayConfigEntity entity, @MappingTarget UserDisplayConfig domain);
}
