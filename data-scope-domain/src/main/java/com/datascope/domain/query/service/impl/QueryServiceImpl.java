package com.datascope.domain.query.service.impl;

import com.datascope.domain.query.repository.QueryRepository;
import com.datascope.domain.query.service.QueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueryServiceImpl implements QueryService {

    @Autowired
    private final QueryRepository queryRepository;

    @Override
    public void delete(String id, String operator) {
        // TODO: 实现删除查询的逻辑
        queryRepository.deleteById(id);
    }
}
