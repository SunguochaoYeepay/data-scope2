package com.datascope.domain.common.repository;

import java.util.List;
import java.util.Optional;

/**
 * 基础仓储接口
 *
 * @param <T> 实体类型
 * @param <ID> ID类型
 */
public interface BaseRepository<T, ID> {
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
     * @param id ID
     * @return 实体对象
     */
    Optional<T> findById(ID id);

    /**
     * 根据ID列表查询实体
     *
     * @param ids ID列表
     * @return 实体对象列表
     */
    List<T> findAllById(List<ID> ids);

    /**
     * 查询所有实体
     *
     * @return 实体对象列表
     */
    List<T> findAll();

    /**
     * 根据ID删除实体
     *
     * @param id ID
     */
    void deleteById(ID id);

    /**
     * 根据ID列表批量删除实体
     *
     * @param ids ID列表
     */
    void deleteAllById(List<ID> ids);

    /**
     * 删除所有实体
     */
    void deleteAll();

    /**
     * 判断ID是否存在
     *
     * @param id ID
     * @return 是否存在
     */
    boolean existsById(ID id);

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    long count();
}