package com.datascope.infrastructure.config;

import com.datascope.infrastructure.mybatis.typehandler.DataSourceIdTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan(basePackages = "com.datascope.infrastructure.mybatis.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);

        // 设置mapper.xml文件位置
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        factoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml"));

        // 设置实体类别名包
        factoryBean.setTypeAliasesPackage("com.datascope.domain.**.model,com.datascope.domain.**.entity");

        // 注册自定义TypeHandler
        factoryBean.setTypeHandlers(new DataSourceIdTypeHandler());

        return factoryBean.getObject();
    }
}
