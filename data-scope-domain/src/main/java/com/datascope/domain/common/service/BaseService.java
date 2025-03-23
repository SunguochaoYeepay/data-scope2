package com.datascope.domain.common.service;

import java.util.List;
import java.util.Optional;

/**
 * 基础服务接口
 * 
 * @author dreambt
 */
public interface BaseService<T, ID> {
    /**
     * 创建实体
     *
     * @param entity 实体对象
     * @return 创建后的实体
     */
    T create(T entity);

    /**
     * 更新实体
     *
     * @param entity 实体对象
     * @return 更新后的实体
     */
    T update(T entity);

    /**
     * 根据ID获取实体
     *
     * @param id 实体ID
     * @return 实体对象
     */
    Optional<T> getById(ID id);

    /**
     * 获取所有实体
     *
     * @return 实体列表
     */
    List<T> getAll();

    /**
     * 根据ID删除实体
     *
     * @param id 实体ID
     */
    void deleteById(ID id);

    /**
     * 检查ID是否存在
     *
     * @param id 实体ID
     * @return 是否存在
     */
    boolean exists(ID id);

    /**
     * 统计实体总数
     *
     * @return 实体总数
     */
    long count();

    /**
     * 删除所有实体
     */
    void deleteAll();

    /**
     * 验证实体
     *
     * @param entity 实体对象
     */
    void validate(T entity);
}