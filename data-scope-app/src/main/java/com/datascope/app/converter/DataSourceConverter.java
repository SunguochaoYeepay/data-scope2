package com.datascope.app.converter;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 数据源实体映射器
 */
@Mapper(componentModel = "spring")
public interface DataSourceConverter {

    DataSourceConverter INSTANCE = Mappers.getMapper(DataSourceConverter.class);

    /**
     * DTO转实体
     *
     * @param dataSourceDTO DTO对象
     * @return 实体对象
     */
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
    @Mapping(target = "lastSyncMessage", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "salt", ignore = true)
    DataSource toEntity(DataSourceDTO dataSourceDTO);

    /**
     * 实体转DTO
     *
     * @param dataSource 实体对象
     * @return DTO对象
     */
    DataSourceDTO toDTO(DataSource dataSource);

    /**
     * 更新实体
     *
     * @param dataSourceDTO    DTO对象
     * @param dataSource 实体对象
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedTime", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    @Mapping(target = "lastSyncTime", ignore = true)
    @Mapping(target = "lastSyncStatus", ignore = true)
    @Mapping(target = "lastSyncMessage", ignore = true)
    @Mapping(target = "remark", ignore = true)
    @Mapping(target = "nonce", ignore = true)
    @Mapping(target = "salt", ignore = true)
    void updateEntity(DataSourceDTO dataSourceDTO, @MappingTarget DataSource dataSource);

    /**
     * 实体列表转DTO列表
     *
     * @param dataSources 实体对象列表
     * @return DTO对象列表
     */
    List<DataSourceDTO> toDTOList(List<DataSource> dataSources);
}
