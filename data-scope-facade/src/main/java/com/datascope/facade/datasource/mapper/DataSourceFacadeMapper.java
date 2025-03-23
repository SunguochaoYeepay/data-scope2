package com.datascope.facade.datasource.mapper;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * 数据源门面映射器
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DataSourceFacadeMapper {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
    @Mapping(target = "lastSyncMessage", ignore = true)
    DataSource toEntity(DataSourceDTO dto);

    /**
     * 实体转DTO
     *
     * @param entity 实体对象
     * @return DTO对象
     */
    DataSourceDTO toDTO(DataSource entity);

    /**
     * 更新实体
     *
     * @param dto    DTO对象
     * @param entity 实体对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "lastSyncAt", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
    @Mapping(target = "lastSyncMessage", ignore = true)
    void updateEntity(DataSourceDTO dto, @MappingTarget DataSource entity);

    /**
     * 实体列表转DTO列表
     *
     * @param entities 实体对象列表
     * @return DTO对象列表
     */
    List<DataSourceDTO> toDTOList(List<DataSource> entities);
}