package com.datascope.domain.query.service;

import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsFunction;
import com.datascope.domain.query.service.QueryResultStatisticsService.StatisticsResult;
import com.datascope.domain.query.service.impl.DefaultQueryResultStatisticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class QueryResultStatisticsServiceTest {

    private QueryResultStatisticsService statisticsService;
    private QueryResult testResult;

    @BeforeEach
    void setUp() {
        // 创建统计服务
        statisticsService = new DefaultQueryResultStatisticsServiceImpl();

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
    void calculate_Count_ShouldReturnCorrectCount() {
        // When
        StatisticsResult result = statisticsService.calculate(testResult, "age", StatisticsFunction.COUNT);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(5);
    }

    @Test
    void calculate_Sum_ShouldReturnCorrectSum() {
        // When
        StatisticsResult result = statisticsService.calculate(testResult, "age", StatisticsFunction.SUM);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isInstanceOf(BigDecimal.class);
        assertThat(((BigDecimal) result.getValue()).intValue()).isEqualTo(150); // 30+25+35+28+32
    }

    @Test
    void calculate_Average_ShouldReturnCorrectAverage() {
        // When
        StatisticsResult result = statisticsService.calculate(testResult, "age", StatisticsFunction.AVG);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isInstanceOf(BigDecimal.class);
        assertThat(((BigDecimal) result.getValue()).doubleValue()).isEqualTo(30.0); // (30+25+35+28+32)/5
    }

    @Test
    void calculate_Min_ShouldReturnMinimumValue() {
        // When
        StatisticsResult result = statisticsService.calculate(testResult, "age", StatisticsFunction.MIN);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(25);
    }

    @Test
    void calculate_Max_ShouldReturnMaximumValue() {
        // When
        StatisticsResult result = statisticsService.calculate(testResult, "age", StatisticsFunction.MAX);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo(35);
    }

    @Test
    void calculateBasicStatistics_ShouldReturnAllBasicStatistics() {
        // When
        List<StatisticsResult> results = statisticsService.calculateBasicStatistics(testResult, "salary");

        // Then
        assertThat(results).hasSize(5); // COUNT, SUM, AVG, MIN, MAX

        // Find COUNT result
        StatisticsResult countResult = findResultByFunction(results, StatisticsFunction.COUNT);
        assertThat(countResult.getValue()).isEqualTo(5);

        // Find SUM result
        StatisticsResult sumResult = findResultByFunction(results, StatisticsFunction.SUM);
        assertThat(((BigDecimal) sumResult.getValue()).doubleValue()).isEqualTo(26500.0); // 5000+4500+6000+5200+5800

        // Find AVG result
        StatisticsResult avgResult = findResultByFunction(results, StatisticsFunction.AVG);
        assertThat(((BigDecimal) avgResult.getValue()).doubleValue()).isEqualTo(5300.0); // 26500/5
    }

    @Test
    void calculateGroupBy_ShouldReturnStatisticsByGroup() {
        // When
        Map<Object, StatisticsResult> results = statisticsService.calculateGroupBy(
            testResult, "age", "salary", StatisticsFunction.AVG);

        // Then
        assertThat(results).hasSize(5); // 5 different ages

        // Check specific group
        StatisticsResult result30 = results.get(30); // Alice's age
        assertThat(result30).isNotNull();
        assertThat(((BigDecimal) result30.getValue()).doubleValue()).isEqualTo(5000.0);
    }

    // Helper method to find a result by function
    private StatisticsResult findResultByFunction(List<StatisticsResult> results, StatisticsFunction function) {
        return results.stream()
            .filter(result -> result.getFunction() == function)
            .findFirst()
            .orElseThrow(() -> new AssertionError("Result for function " + function + " not found"));
    }
}
