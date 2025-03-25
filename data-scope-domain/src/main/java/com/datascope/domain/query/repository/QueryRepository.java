package com.datascope.domain.query.repository;

public interface QueryRepository {

    /**
     * 根据ID删除查询
     *
     * @param id 查询ID
     */
    void deleteById(String id);
}