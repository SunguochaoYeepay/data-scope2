package com.datascope.domain.query.service;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.domain.query.model.PagedQueryResult;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 查询执行服务集成测试
 * 注意：此测试需要连接到实际的数据库，请确保测试数据库已正确配置
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class QueryExecutionServiceIntegrationTest {

    @Autowired
    private QueryExecutionService queryExecutionService;

    @Autowired
    private DataSourceRepository dataSourceRepository;

    @Autowired
    private QueryExecutionRepository queryExecutionRepository;

    @Autowired
    private SqlExecutionEngine sqlExecutionEngine;

    private DataSource testDataSource;
    private String dataSourceId;

    @BeforeEach
    void setUp() {
        // 创建测试数据源
        testDataSource = new DataSource();
        testDataSource.setName("Test H2 Database");
        testDataSource.setType(DataSourceType.H2);
        testDataSource.setHost("localhost");
        testDataSource.setPort(9092);
        testDataSource.setDatabase("testdb");
        testDataSource.setUsername("sa");
        testDataSource.setPassword("");
        testDataSource.setStatus(DataSourceStatus.ACTIVE);

        // 保存测试数据源
        dataSourceRepository.save(testDataSource);
        dataSourceId = testDataSource.getId();

        // 创建测试表和数据
        String createTableSql = "CREATE TABLE IF NOT EXISTS test_users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "name VARCHAR(100), " +
            "age INT, " +
            "email VARCHAR(100)" +
            ")";

        sqlExecutionEngine.execute(DataSourceId.of(dataSourceId), createTableSql, Collections.emptyMap());

        // 插入测试数据
        String insertDataSql = "INSERT INTO test_users (name, age, email) VALUES " +
            "('Alice', 30, 'alice@example.com'), " +
            "('Bob', 25, 'bob@example.com'), " +
            "('Charlie', 35, 'charlie@example.com'), " +
            "('David', 28, 'david@example.com'), " +
            "('Eve', 22, 'eve@example.com')";

        sqlExecutionEngine.execute(DataSourceId.of(dataSourceId), insertDataSql, Collections.emptyMap());
    }

    @Test
    void executePagedSql_WithSorting_ShouldReturnSortedResults() {
        // Given
        String sql = "SELECT * FROM test_users";
        Map<String, Object> parameters = Collections.emptyMap();
        int pageNumber = 1;
        int pageSize = 10;

        // 创建排序字段 - 按年龄降序排序
        List<QueryResultSortService.SortField> sortFields = List.of(
            new QueryResultSortService.SortField("age", QueryResultSortService.SortDirection.DESC)
        );

        // When
        QueryExecution execution = queryExecutionService.executePagedSql(
            dataSourceId, sql, parameters, pageNumber, pageSize, sortFields);

        // Then
        assertThat(execution).isNotNull();
        assertThat(execution.isCompleted()).isTrue();

        // 获取分页结果
        PagedQueryResult pagedResult = queryExecutionService.getPagedResult(
            execution.getId(), pageNumber, pageSize);

        // 验证结果
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.getRows()).hasSize(5);

        // 验证排序 - 第一行应该是年龄最大的Charlie (35)
        Map<String, Object> firstRow = pagedResult.getRows().get(0);
        assertThat(firstRow.get("name")).isEqualTo("Charlie");
        assertThat(firstRow.get("age")).isEqualTo(35);

        // 验证排序 - 最后一行应该是年龄最小的Eve (22)
        Map<String, Object> lastRow = pagedResult.getRows().get(4);
        assertThat(lastRow.get("name")).isEqualTo("Eve");
        assertThat(lastRow.get("age")).isEqualTo(22);
    }

    @Test
    void executePagedSql_WithMultipleSortFields_ShouldRespectSortOrder() {
        // Given
        // 先插入一些有相同年龄的测试数据
        String insertMoreDataSql = "INSERT INTO test_users (name, age, email) VALUES " +
            "('Frank', 30, 'frank@example.com'), " +
            "('Grace', 30, 'grace@example.com')";

        sqlExecutionEngine.execute(DataSourceId.of(dataSourceId), insertMoreDataSql, Collections.emptyMap());

        String sql = "SELECT * FROM test_users";
        Map<String, Object> parameters = Collections.emptyMap();
        int pageNumber = 1;
        int pageSize = 10;

        // 创建多个排序字段 - 先按年龄降序，然后按姓名升序
        List<QueryResultSortService.SortField> sortFields = List.of(
            new QueryResultSortService.SortField("age", QueryResultSortService.SortDirection.DESC),
            new QueryResultSortService.SortField("name", QueryResultSortService.SortDirection.ASC)
        );

        // When
        QueryExecution execution = queryExecutionService.executePagedSql(
            dataSourceId, sql, parameters, pageNumber, pageSize, sortFields);

        // Then
        assertThat(execution).isNotNull();
        assertThat(execution.isCompleted()).isTrue();

        // 获取分页结果
        PagedQueryResult pagedResult = queryExecutionService.getPagedResult(
            execution.getId(), pageNumber, pageSize);

        // 验证结果
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.getRows()).hasSize(7);

        // 验证排序 - 第一行应该是年龄最大的Charlie (35)
        Map<String, Object> firstRow = pagedResult.getRows().get(0);
        assertThat(firstRow.get("name")).isEqualTo("Charlie");
        assertThat(firstRow.get("age")).isEqualTo(35);

        // 验证相同年龄的记录按姓名排序 - Alice应该在Frank之前
        List<Map<String, Object>> age30Rows = new ArrayList<>();
        for (Map<String, Object> row : pagedResult.getRows()) {
            if (row.get("age").equals(30)) {
                age30Rows.add(row);
            }
        }

        assertThat(age30Rows).hasSize(3);
        assertThat(age30Rows.get(0).get("name")).isEqualTo("Alice");
        assertThat(age30Rows.get(1).get("name")).isEqualTo("Frank");
        assertThat(age30Rows.get(2).get("name")).isEqualTo("Grace");
    }

    @Test
    void executePagedSql_WithDifferentSortDirections_ShouldRespectDirections() {
        // Given
        String sql = "SELECT * FROM test_users";
        Map<String, Object> parameters = Collections.emptyMap();
        int pageNumber = 1;
        int pageSize = 10;

        // 创建排序字段 - 按年龄升序排序
        List<QueryResultSortService.SortField> ascSortFields = List.of(
            new QueryResultSortService.SortField("age", QueryResultSortService.SortDirection.ASC)
        );

        // When - 执行升序排序
        QueryExecution ascExecution = queryExecutionService.executePagedSql(
            dataSourceId, sql, parameters, pageNumber, pageSize, ascSortFields);

        // 创建排序字段 - 按年龄降序排序
        List<QueryResultSortService.SortField> descSortFields = List.of(
            new QueryResultSortService.SortField("age", QueryResultSortService.SortDirection.DESC)
        );

        // When - 执行降序排序
        QueryExecution descExecution = queryExecutionService.executePagedSql(
            dataSourceId, sql, parameters, pageNumber, pageSize, descSortFields);

        // Then - 验证升序结果
        PagedQueryResult ascResult = queryExecutionService.getPagedResult(
            ascExecution.getId(), pageNumber, pageSize);

        assertThat(ascResult).isNotNull();
        assertThat(ascResult.getRows()).hasSize(5);

        // 验证升序排序 - 第一行应该是年龄最小的Eve (22)
        Map<String, Object> ascFirstRow = ascResult.getRows().get(0);
        assertThat(ascFirstRow.get("name")).isEqualTo("Eve");
        assertThat(ascFirstRow.get("age")).isEqualTo(22);

        // Then - 验证降序结果
        PagedQueryResult descResult = queryExecutionService.getPagedResult(
            descExecution.getId(), pageNumber, pageSize);

        assertThat(descResult).isNotNull();
        assertThat(descResult.getRows()).hasSize(5);

        // 验证降序排序 - 第一行应该是年龄最大的Charlie (35)
        Map<String, Object> descFirstRow = descResult.getRows().get(0);
        assertThat(descFirstRow.get("name")).isEqualTo("Charlie");
        assertThat(descFirstRow.get("age")).isEqualTo(35);

        // 验证两个结果的顺序相反
        for (int i = 0; i < 5; i++) {
            assertThat(ascResult.getRows().get(i).get("age"))
                .isEqualTo(descResult.getRows().get(4 - i).get("age"));
        }
    }
}
