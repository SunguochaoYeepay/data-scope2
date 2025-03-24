package com.datascope.domain.query.service;

public interface QueryService {

    /**
     * 删除查询
     *
     * @param id       查询ID
     * @param operator 操作人
     */
    void delete(String id, String operator);
}