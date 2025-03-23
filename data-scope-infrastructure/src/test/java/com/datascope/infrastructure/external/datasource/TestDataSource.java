package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.enums.DataSourceType;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 用于测试的DataSource类
 */
@Data
@Accessors(chain = true)
public class TestDataSource {
    private String id;
    private String name;
    private DataSourceType type;
    private String host;
    private Integer port;
    private String database;
    private String username;
    private String password;
    private String salt;
}
