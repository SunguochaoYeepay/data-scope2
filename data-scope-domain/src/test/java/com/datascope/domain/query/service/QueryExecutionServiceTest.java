package com.datascope.domain.query.service;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.domain.query.service.impl.QueryExecutionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryExecutionServiceTest {

    @Mock
    private QueryExecutionRepository queryExecutionRepository;

    @Mock
    private SqlExecutionEngine sqlExecutionEngine;

    @Mock
    private QueryResultCacheService queryResultCacheService;

    @Mock
    private QueryResultSortService queryResultSortService;

    @Mock
    private QueryResultFilterService queryResultFilterService;

    @Mock
    private QueryResultStatisticsService queryResultStatisticsService;

    @InjectMocks
    private QueryExecutionServiceImpl queryExecutionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void executeSql_ShouldCreateAndSaveExecution() {
        // Given
        String dataSourceId = UUID.randomUUID().toString();
        String sql = "SELECT * FROM test";
        Map<String, Object> parameters = Collections.emptyMap();

        // Create a real QueryExecution that will be returned by the repository
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            sql,
            parameters
        );

        // Mock SQL validation
        when(sqlExecutionEngine.validate(any(DataSourceId.class), any(String.class))).thenReturn(true);

        // Create a real QueryResult
        QueryResult queryResult = new QueryResult();
        queryResult.setTotalRows(10L);

        when(sqlExecutionEngine.execute(any(DataSourceId.class), any(String.class), any(Map.class)))
            .thenReturn(queryResult);

        // Mock repository save to return the execution
        when(queryExecutionRepository.save(any(QueryExecution.class)))
            .thenReturn(execution);

        // When
        QueryExecution result = queryExecutionService.executeSql(dataSourceId, sql, parameters);

        // Then
        assertThat(result).isNotNull();
        verify(sqlExecutionEngine).validate(any(DataSourceId.class), any(String.class));
        verify(sqlExecutionEngine).execute(any(DataSourceId.class), any(String.class), any(Map.class));
        verify(queryExecutionRepository, times(2)).save(any(QueryExecution.class));
    }

    @Test
    void getById_ShouldReturnExecution() {
        // Given
        String id = UUID.randomUUID().toString();
        String dataSourceId = UUID.randomUUID().toString();
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 确保 findById 返回一个非空的 Optional
        when(queryExecutionRepository.findById(any(String.class))).thenReturn(Optional.of(execution));

        // When
        Optional<QueryExecution> result = queryExecutionService.getById(id);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(execution);
        verify(queryExecutionRepository).findById(id);
    }

    @Test
    void getRecentByUserId_ShouldReturnExecutions() {
        // Given
        String userId = "test-user";
        int limit = 10;
        String dataSourceId = UUID.randomUUID().toString();
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 确保 findRecentByUserId 返回一个非空的列表
        when(queryExecutionRepository.findRecentByUserId(any(String.class), anyInt()))
            .thenReturn(Collections.singletonList(execution));

        // When
        List<QueryExecution> result = queryExecutionService.getRecentByUserId(userId, limit);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(execution);
        verify(queryExecutionRepository).findRecentByUserId(userId, limit);
    }

    @Test
    void cancel_ShouldMarkExecutionAsCancelled() {
        // Given
        String id = UUID.randomUUID().toString();
        String dataSourceId = UUID.randomUUID().toString();

        // 创建一个真实的QueryExecution对象
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 设置查询为运行中状态
        execution.markAsStarted();

        // 确保 findById 返回一个非空的 Optional
        when(queryExecutionRepository.findById(any(String.class))).thenReturn(Optional.of(execution));
        when(queryExecutionRepository.save(any(QueryExecution.class))).thenReturn(execution);
        doNothing().when(sqlExecutionEngine).cancel(any(String.class));

        // When
        queryExecutionService.cancel(id);

        // Then
        verify(queryExecutionRepository).findById(id);
        verify(sqlExecutionEngine).cancel(any(String.class));
        verify(queryExecutionRepository).save(any(QueryExecution.class));
        assertThat(execution.isCancelled()).isTrue();
    }

    @Test
    void executeNaturalLanguage_ShouldConvertAndExecuteSQL() {
        // Given
        String dataSourceId = UUID.randomUUID().toString();
        String naturalLanguageQuery = "Show me all users";
        String convertedSql = "SELECT * FROM users";

        // Create a real QueryExecution that will be returned by the repository
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "NATURAL_LANGUAGE: " + naturalLanguageQuery,
            Map.of()
        );

        // Mock repository save to return the execution
        when(queryExecutionRepository.save(any(QueryExecution.class)))
            .thenReturn(execution);

        // Mock SQL validation
        when(sqlExecutionEngine.validate(any(DataSourceId.class), any(String.class))).thenReturn(true);

        // Create a real QueryResult
        QueryResult queryResult = new QueryResult();
        queryResult.setTotalRows(5L);

        when(sqlExecutionEngine.execute(any(DataSourceId.class), any(String.class), any(Map.class)))
            .thenReturn(queryResult);

        // When
        QueryExecution result = queryExecutionService.executeNaturalLanguage(dataSourceId, naturalLanguageQuery);

        // Then
        assertThat(result).isNotNull();
        verify(queryExecutionRepository, times(2)).save(any(QueryExecution.class));
    }

    @Test
    void exportResult_ShouldReturnExportPath() {
        // Given
        String id = UUID.randomUUID().toString();
        String format = "csv";
        String dataSourceId = UUID.randomUUID().toString();

        // 创建一个真实的QueryExecution对象
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 设置查询为已完成状态
        execution.markAsCompleted(10L);

        // 设置查询结果
        QueryResult queryResult = new QueryResult();
        queryResult.setTotalRows(10L);
        execution.setResult(queryResult);

        // 确保 findById 返回一个非空的 Optional
        when(queryExecutionRepository.findById(any(String.class))).thenReturn(Optional.of(execution));

        // When
        String exportPath = queryExecutionService.exportResult(id, format);

        // Then
        assertThat(exportPath).isNotNull();
        assertThat(exportPath).contains(id);
        assertThat(exportPath).endsWith(".csv");
        verify(queryExecutionRepository).findById(id);
    }
}
