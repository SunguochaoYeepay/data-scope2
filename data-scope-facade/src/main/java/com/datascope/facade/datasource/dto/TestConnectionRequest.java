package com.datascope.facade.datasource.dto;

import com.datascope.facade.datasource.enums.DataSourceType;
import lombok.Data;

/**
 * 测试数据源连接请求
 */
@Data
public class TestConnectionRequest {
    private String name;
    private DataSourceType type;
    private String host;
    private Integer port;
    private String database;
    private String schema;
    private String username;
    private String password;
}
