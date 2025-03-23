package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.domain.datasource.entity.DataSource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据源Mapper接口
 */
public interface DataSourceMapper {

    /**
     * 插入数据源
     *
     * @param entity 数据源实体
     * @return 影响行数
     */
    int insert(DataSource entity);

    /**
     * 更新数据源
     *
     * @param entity 数据源实体
     * @return 影响行数
     */
    int update(DataSource entity);

    /**
     * 根据ID删除数据源
     *
     * @param id 数据源ID
     * @return 影响行数
     */
    int deleteById(String id);

    /**
     * 根据ID列表批量删除数据源
     *
     * @param ids ID列表
     * @return 影响行数
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 根据ID查询数据源
     *
     * @param id 数据源ID
     * @return 数据源实体
     */
    DataSource selectById(String id);

    /**
     * 根据ID列表批量查询数据源
     *
     * @param ids ID列表
     * @return 数据源实体列表
     */
    List<DataSource> selectByIds(@Param("ids") List<String> ids);

    /**
     * 查询所有数据源
     *
     * @return 数据源实体列表
     */
    List<DataSource> selectAll();

    /**
     * 根据名称查询数据源
     *
     * @param name 数据源名称
     * @return 数据源实体
     */
    DataSource selectByName(String name);

    /**
     * 检查名称是否存在
     *
     * @param name 数据源名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据类型查询数据源列表
     *
     * @param type 数据源类型
     * @return 数据源实体列表
     */
    List<DataSource> selectByType(@Param("type") DataSource.DataSourceType type);

    /**
     * 根据状态查询数据源列表
     *
     * @param status 数据源状态
     * @return 数据源实体列表
     */
    List<DataSource> selectByStatus(@Param("status") DataSource.DataSourceStatus status);

    /**
     * 根据同步状态查询数据源列表
     *
     * @param syncStatus 同步状态
     * @return 数据源实体列表
     */
    List<DataSource> selectByLastSyncStatus(@Param("syncStatus") DataSource.SyncStatus syncStatus);

    /**
     * 根据名称模糊查询数据源列表
     *
     * @param nameLike 数据源名称（模糊匹配）
     * @return 数据源实体列表
     */
    List<DataSource> selectByNameLike(@Param("nameLike") String nameLike);

    /**
     * 根据主机地址查询数据源列表
     *
     * @param host 主机地址
     * @return 数据源实体列表
     */
    List<DataSource> selectByHost(@Param("host") String host);

    /**
     * 根据数据库名称查询数据源列表
     *
     * @param database 数据库名称
     * @return 数据源实体列表
     */
    List<DataSource> selectByDatabase(@Param("database") String database);

    /**
     * 根据类型和状态查询数据源列表
     *
     * @param type   数据源类型
     * @param status 数据源状态
     * @return 数据源实体列表
     */
    List<DataSource> selectByTypeAndStatus(
            @Param("type") DataSource.DataSourceType type,
            @Param("status") DataSource.DataSourceStatus status);
}