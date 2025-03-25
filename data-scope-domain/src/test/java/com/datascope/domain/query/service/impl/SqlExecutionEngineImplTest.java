package com.datascope.domain.query.service.impl;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.gateway.DataSourceConnectionGateway;
import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.domain.query.model.QueryResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SqlExecutionEngineImplTest {

    @Mock
    private DataSourceRepository dataSourceRepository;

    @Mock
    private DataSourceConnectionGateway connectionGateway;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private ResultSetMetaData resultSetMetaData;

    @InjectMocks
    private SqlExecutionEngineImpl sqlExecutionEngine;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // 设置基本的Mock行为
        DataSource dataSource = new DataSource();
        dataSource.setType(DataSourceType.MYSQL);
        when(dataSourceRepository.findById(anyString())).thenReturn(Optional.of(dataSource));
        when(connectionGateway.getConnection(any(DataSource.class))).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.getMetaData()).thenReturn(resultSetMetaData);
        when(resultSetMetaData.getColumnCount()).thenReturn(0);
    }

    @Test
    void execute_WithSortParameters_ShouldBuildSortQuery() throws Exception {
        // 准备测试数据
        String dataSourceId = "test-datasource";
        String sql = "SELECT * FROM users";

        // 创建排序参数
        Map<String, Object> parameters = new HashMap<>();
        List<Map<String, Object>> sortFields = new ArrayList<>();

        Map<String, Object> sortField1 = new HashMap<>();
        sortField1.put("fieldName", "name");
        sortField1.put("direction", "ASC");
        sortFields.add(sortField1);

        Map<String, Object> sortField2 = new HashMap<>();
        sortField2.put("fieldName", "age");
        sortField2.put("direction", "DESC");
        sortFields.add(sortField2);

        parameters.put("_sort_fields", sortFields);

        // 执行测试
        QueryResult result = sqlExecutionEngine.execute(DataSourceId.of(dataSourceId), sql, parameters);

        // 验证结果
        assertNotNull(result);

        // 验证SQL中包含ORDER BY子句
        verify(connection).prepareStatement(contains("ORDER BY name ASC, age DESC"));
    }

    @Test
    void execute_WithSortAndPageParameters_ShouldBuildSortAndPageQuery() throws Exception {
        // 准备测试数据
        String dataSourceId = "test-datasource";
        String sql = "SELECT * FROM users";

        // 创建排序和分页参数
        Map<String, Object> parameters = new HashMap<>();

        // 排序参数
        List<Map<String, Object>> sortFields = new ArrayList<>();
        Map<String, Object> sortField = new HashMap<>();
        sortField.put("fieldName", "name");
        sortField.put("direction", "ASC");
        sortFields.add(sortField);
        parameters.put("_sort_fields", sortFields);

        // 分页参数
        parameters.put("_page_number", 2);
        parameters.put("_page_size", 10);
        parameters.put("_offset", 10);
        parameters.put("_limit", 10);
        parameters.put("_db_type", "MYSQL");

        // 执行测试
        QueryResult result = sqlExecutionEngine.execute(DataSourceId.of(dataSourceId), sql, parameters);

        // 验证结果
        assertNotNull(result);

        // 验证SQL中包含ORDER BY和LIMIT子句
        verify(connection).prepareStatement(contains("ORDER BY name ASC"));
        verify(connection).prepareStatement(contains("LIMIT 10 OFFSET 10"));
    }
}
