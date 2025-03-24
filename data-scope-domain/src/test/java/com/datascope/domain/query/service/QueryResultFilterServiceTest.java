package com.datascope.domain.query.service;

import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultFilterService.FilterCondition;
import com.datascope.domain.query.service.QueryResultFilterService.FilterGroup;
import com.datascope.domain.query.service.QueryResultFilterService.FilterLogic;
import com.datascope.domain.query.service.QueryResultFilterService.FilterOperator;
import com.datascope.domain.query.service.impl.DefaultQueryResultFilterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryResultFilterServiceTest {

    private QueryResultFilterService filterService;
    private QueryResult testResult;

    @BeforeEach
    void setUp() {
        // 创建过滤服务
        filterService = new DefaultQueryResultFilterServiceImpl();

        // 创建测试数据
        testResult = new QueryResult();

        // 创建列定义
        List<ColumnDefinition> columns = new ArrayList<>();
        columns.add(ColumnDefinition.builder()
            .name("id")
            .dataType("INTEGER")
            .label("ID")
            .build());
        columns.add(ColumnDefinition.builder()
            .name("name")
            .dataType("VARCHAR")
            .label("Name")
            .build());
        columns.add(ColumnDefinition.builder()
            .name("age")
            .dataType("INTEGER")
            .label("Age")
            .build());
        columns.add(ColumnDefinition.builder()
            .name("salary")
            .dataType("DECIMAL")
            .label("Salary")
            .build());
        testResult.setColumns(columns);

        // 创建行数据
        List<Map<String, Object>> rows = new ArrayList<>();

        Map<String, Object> row1 = new HashMap<>();
        row1.put("id", 1);
        row1.put("name", "Alice");
        row1.put("age", 30);
        row1.put("salary", 5000.0);
        rows.add(row1);

        Map<String, Object> row2 = new HashMap<>();
        row2.put("id", 2);
        row2.put("name", "Bob");
        row2.put("age", 25);
        row2.put("salary", 4500.0);
        rows.add(row2);

        Map<String, Object> row3 = new HashMap<>();
        row3.put("id", 3);
        row3.put("name", "Charlie");
        row3.put("age", 35);
        row3.put("salary", 6000.0);
        rows.add(row3);

        Map<String, Object> row4 = new HashMap<>();
        row4.put("id", 4);
        row4.put("name", "David");
        row4.put("age", 28);
        row4.put("salary", 5200.0);
        rows.add(row4);

        Map<String, Object> row5 = new HashMap<>();
        row5.put("id", 5);
        row5.put("name", "Eve");
        row5.put("age", 32);
        row5.put("salary", 5800.0);
        rows.add(row5);

        testResult.setRows(rows);
        testResult.setTotalRows(rows.size());
    }

    @Test
    void filter_WithEqualsCondition_ShouldReturnMatchingRows() {
        // Given
        FilterCondition condition = new FilterCondition("name", FilterOperator.EQUALS, "Bob");

        // When
        QueryResult filteredResult = filterService.filter(testResult, condition);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(1);
        assertThat(filteredResult.getRows().get(0).get("name")).isEqualTo("Bob");
    }

    @Test
    void filter_WithGreaterThanCondition_ShouldReturnMatchingRows() {
        // Given
        FilterCondition condition = new FilterCondition("age", FilterOperator.GREATER_THAN, 30);

        // When
        QueryResult filteredResult = filterService.filter(testResult, condition);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(2);
        assertThat(filteredResult.getRows().get(0).get("name")).isEqualTo("Charlie");
        assertThat(filteredResult.getRows().get(1).get("name")).isEqualTo("Eve");
    }

    @Test
    void filter_WithMultipleConditionsAndLogic_ShouldReturnMatchingRows() {
        // Given
        List<FilterCondition> conditions = new ArrayList<>();
        conditions.add(new FilterCondition("age", FilterOperator.GREATER_THAN, 25));
        conditions.add(new FilterCondition("salary", FilterOperator.LESS_THAN, 6000.0));
        FilterGroup filterGroup = new FilterGroup(conditions, FilterLogic.AND);

        // When
        QueryResult filteredResult = filterService.filter(testResult, filterGroup);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(3);
        assertThat(filteredResult.getRows().get(0).get("name")).isEqualTo("Alice");
        assertThat(filteredResult.getRows().get(1).get("name")).isEqualTo("David");
        assertThat(filteredResult.getRows().get(2).get("name")).isEqualTo("Eve");
    }

    @Test
    void filter_WithMultipleConditionsOrLogic_ShouldReturnMatchingRows() {
        // Given
        List<FilterCondition> conditions = new ArrayList<>();
        conditions.add(new FilterCondition("age", FilterOperator.LESS_THAN, 26));
        conditions.add(new FilterCondition("salary", FilterOperator.GREATER_THAN, 5500.0));
        FilterGroup filterGroup = new FilterGroup(conditions, FilterLogic.OR);

        // When
        QueryResult filteredResult = filterService.filter(testResult, filterGroup);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(3);
        // Bob (age < 26), Charlie (salary > 5500), Eve (salary > 5500)
    }

    @Test
    void filter_WithContainsCondition_ShouldReturnMatchingRows() {
        // Given
        FilterCondition condition = new FilterCondition("name", FilterOperator.CONTAINS, "a");

        // When
        QueryResult filteredResult = filterService.filter(testResult, condition);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(3);
        // Alice, Charlie, David
    }

    @Test
    void filter_WithInCondition_ShouldReturnMatchingRows() {
        // Given
        FilterCondition condition = new FilterCondition("id", FilterOperator.IN, List.of(1, 3, 5));

        // When
        QueryResult filteredResult = filterService.filter(testResult, condition);

        // Then
        assertThat(filteredResult).isNotNull();
        assertThat(filteredResult.getRows()).hasSize(3);
        assertThat(filteredResult.getRows().get(0).get("name")).isEqualTo("Alice");
        assertThat(filteredResult.getRows().get(1).get("name")).isEqualTo("Charlie");
        assertThat(filteredResult.getRows().get(2).get("name")).isEqualTo("Eve");
    }
}
