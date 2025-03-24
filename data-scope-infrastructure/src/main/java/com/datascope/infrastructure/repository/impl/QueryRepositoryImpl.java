package com.datascope.infrastructure.repository.impl;

import com.datascope.domain.query.repository.QueryRepository;
import com.datascope.infrastructure.mybatis.mapper.QueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class QueryRepositoryImpl implements QueryRepository {

    @Autowired
    private QueryMapper queryMapper;

    @Override
    public void deleteById(String id) {
        queryMapper.deleteById(id);
    }
}
