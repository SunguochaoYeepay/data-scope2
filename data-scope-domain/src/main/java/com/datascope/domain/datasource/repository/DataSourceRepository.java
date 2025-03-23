package com.datascope.domain.datasource.repository;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.common.repository.BaseRepository;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;

import java.util.List;

/**
 * 数据源仓储接口
 */
public interface DataSourceRepository extends BaseRepository<DataSource, String> {

    /**
     * 根据名称查询数据源
     *
     * @param name 数据源名称
     * @return 数据源对象
     */
    DataSource findByName(String name);

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
     * @return 数据源对象列表
     */
    List<DataSource> findByType(DataSourceType type);

    /**
     * 根据状态查询数据源列表
     *
     * @param status 数据源状态
     * @return 数据源对象列表
     */
    List<DataSource> findByStatus(DataSourceStatus status);

    /**
     * 根据同步状态查询数据源列表
     *
     * @param syncStatus 同步状态
     * @return 数据源对象列表
     */
    List<DataSource> findByLastSyncStatus(SyncStatus syncStatus);

    /**
     * 根据名称模糊查询数据源列表
     *
     * @param nameLike 数据源名称（模糊匹配）
     * @return 数据源对象列表
     */
    List<DataSource> findByNameLike(String nameLike);

    /**
     * 根据主机地址查询数据源列表
     *
     * @param host 主机地址
     * @return 数据源对象列表
     */
    List<DataSource> findByHost(String host);

    /**
     * 根据数据库名称查询数据源列表
     *
     * @param database 数据库名称
     * @return 数据源对象列表
     */
    List<DataSource> findByDatabase(String database);

    /**
     * 根据类型和状态查询数据源列表
     *
     * @param type   数据源类型
     * @param status 数据源状态
     * @return 数据源对象列表
     */
    List<DataSource> findByTypeAndStatus(DataSourceType type, DataSourceStatus status);
}
