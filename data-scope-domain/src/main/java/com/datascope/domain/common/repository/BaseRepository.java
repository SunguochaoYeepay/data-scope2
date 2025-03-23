package com.datascope.domain.common.repository;

import com.datascope.domain.common.entity.BaseEntity;

import java.util.List;
import java.util.Optional;

/**
 * 基础仓储接口，定义所有仓储共有的方法
 *
 * @param <T> 实体类型
 */
public interface BaseRepository<T extends BaseEntity> {

    /**
     * 保存实体
     *
     * @param entity 实体对象
     * @return 保存后的实体
     */
    T save(T entity);

    /**
     * 批量保存实体
     *
     * @param entities 实体对象列表
     * @return 保存后的实体列表
     */
    List<T> saveAll(List<T> entities);

    /**
     * 根据ID查询实体
     *
     * @param id 实体ID
     * @return 实体对象
     */
    Optional<T> findById(String id);

    /**
     * 根据ID列表查询实体列表
     *
     * @param ids ID列表
     * @return 实体对象列表
     */
    List<T> findAllById(List<String> ids);

    /**
     * 查询所有实体
     *
     * @return 实体对象列表
     */
    List<T> findAll();

    /**
     * 根据ID删除实体
     *
     * @param id 实体ID
     */
    void deleteById(String id);

    /**
     * 根据ID列表批量删除实体
     *
     * @param ids ID列表
     */
    void deleteAllById(List<String> ids);

    /**
     * 删除所有实体
     */
    void deleteAll();

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    long count();

    /**
     * 检查ID是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    boolean existsById(String id);
}