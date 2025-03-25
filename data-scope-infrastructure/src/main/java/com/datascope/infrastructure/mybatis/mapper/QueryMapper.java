package com.datascope.infrastructure.mybatis.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QueryMapper {

    void deleteById(@Param("id") String id);
}
