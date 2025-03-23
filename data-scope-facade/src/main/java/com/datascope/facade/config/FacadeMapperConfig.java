package com.datascope.facade.config;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.datascope.facade.query.mapper.UserDisplayConfigFacadeMapper;

/**
 * Facade层Mapper配置类
 */
@Configuration
public class FacadeMapperConfig {

    @Bean
    public UserDisplayConfigFacadeMapper userDisplayConfigFacadeMapper() {
        return Mappers.getMapper(UserDisplayConfigFacadeMapper.class);
    }
}