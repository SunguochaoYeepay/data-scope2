package com.datascope.infrastructure.mybatis.mapper;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据源Mapper接口
 */
public interface DataSourceMapper {

    /**
     * 插入数据源
     */
    int insert(DataSource entity);

    /**
     * 更新数据源
     */
    int update(DataSource entity);

    /**
     * 根据ID删除数据源
     */
    int deleteById(String id);

    /**
     * 删除所有数据源
     */
    int deleteAll();

    /**
     * 批量删除数据源
     */
    int deleteAllById(@Param("ids") List<String> ids);

    /**
     * 根据ID查询数据源
     */
    DataSource selectById(String id);

    /**
     * 根据ID列表批量查询数据源
     */
    List<DataSource> findAllById(@Param("ids") List<String> ids);

    /**
     * 查询所有数据源
     */
    List<DataSource> selectAll();

    /**
     * 统计数据源总数
     */
    long count();

    /**
     * 检查ID是否存在
     */
    boolean existsById(String id);

    /**
     * 根据名称查询数据源
     */
    DataSource selectByName(String name);

    /**
     * 检查名称是否存在
     */
    boolean existsByName(String name);

    /**
     * 根据类型查询数据源列表
     */
    List<DataSource> selectByType(@Param("type") DataSourceType type);

    /**
     * 根据状态查询数据源列表
     */
    List<DataSource> selectByStatus(@Param("status") DataSourceStatus status);

    /**
     * 根据同步状态查询数据源列表
     */
    List<DataSource> selectByLastSyncStatus(@Param("syncStatus") SyncStatus syncStatus);

    /**
     * 根据名称模糊查询数据源列表
     */
    List<DataSource> selectByNameLike(@Param("nameLike") String nameLike);

    /**
     * 根据主机地址查询数据源列表
     */
    List<DataSource> selectByHost(@Param("host") String host);

    /**
     * 根据数据库名称查询数据源列表
     */
    List<DataSource> selectByDatabase(@Param("database") String database);

    /**
     * 根据类型和状态查询数据源列表
     */
    List<DataSource> selectByTypeAndStatus(
        @Param("type") DataSourceType type,
        @Param("status") DataSourceStatus status);

    /**
     * 统计指定类型的数据源数量
     */
    long countByType(@Param("type") DataSourceType type);

    /**
     * 统计指定状态的数据源数量
     */
    long countByStatus(@Param("status") DataSourceStatus status);
}
