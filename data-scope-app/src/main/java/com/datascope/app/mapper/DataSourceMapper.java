package com.datascope.app.mapper;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * 数据源实体映射器
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DataSourceMapper {

    /**
     * DTO转实体
     *
     * @param dto DTO对象
     * @return 实体对象
     */
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
//    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
//    @Mapping(target = "lastSyncMessage", ignore = true)
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
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedTime", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
//    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
//    @Mapping(target = "lastSyncMessage", ignore = true)
    void updateEntity(DataSourceDTO dto, @MappingTarget DataSource entity);

    /**
     * 实体列表转DTO列表
     *
     * @param entities 实体对象列表
     * @return DTO对象列表
     */
    List<DataSourceDTO> toDTOList(List<DataSource> entities);
}
