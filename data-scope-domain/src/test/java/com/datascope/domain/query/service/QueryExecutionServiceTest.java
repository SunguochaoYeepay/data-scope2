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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryExecutionServiceTest {

    @Mock
    private QueryExecutionRepository queryExecutionRepository;

    @Mock
    private SqlExecutionEngine sqlExecutionEngine;

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

        // Mock SQL validation
        when(sqlExecutionEngine.validate(any(DataSourceId.class), eq(sql))).thenReturn(true);

        // Mock SQL execution
        QueryResult mockResult = new QueryResult();
        mockResult.setTotalRows(10L);
        when(sqlExecutionEngine.execute(any(DataSourceId.class), eq(sql), eq(parameters)))
            .thenReturn(mockResult);

        when(queryExecutionRepository.save(any(QueryExecution.class)))
            .thenAnswer(invocation -> {
                QueryExecution saved = invocation.getArgument(0);
                assertThat(saved.getSql()).isEqualTo(sql);
                return saved;
            });

        // When
        QueryExecution result = queryExecutionService.executeSql(dataSourceId, sql, parameters);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getSql()).isEqualTo(sql);
        verify(sqlExecutionEngine).validate(any(DataSourceId.class), eq(sql));
        verify(sqlExecutionEngine).execute(any(DataSourceId.class), eq(sql), eq(parameters));
        verify(queryExecutionRepository, times(2)).save(any(QueryExecution.class));
    }

    @Test
    void getById_ShouldReturnExecution() {
        // Given
        String id = UUID.randomUUID().toString();
        String dataSourceId = UUID.randomUUID().toString();
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId.toString()),
            "SELECT 1",
            Collections.emptyMap()
        );
        when(queryExecutionRepository.findById(id)).thenReturn(Optional.of(execution));

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
            DataSourceId.of(dataSourceId.toString()),
            "SELECT 1",
            Collections.emptyMap()
        );
        when(queryExecutionRepository.findRecentByUserId(userId, limit))
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
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId.toString()),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 设置查询为运行中状态
        execution.markAsStarted();

        when(queryExecutionRepository.findById(id)).thenReturn(Optional.of(execution));
        when(queryExecutionRepository.save(any(QueryExecution.class))).thenReturn(execution);

        // 模拟SQL执行引擎的cancel方法
        doNothing().when(sqlExecutionEngine).cancel(execution.getId());

        // When
        queryExecutionService.cancel(id);

        // Then
        verify(sqlExecutionEngine).cancel(execution.getId());
        verify(queryExecutionRepository).save(any(QueryExecution.class));
        assertThat(execution.isCancelled()).isTrue();
    }

    @Test
    void executeNaturalLanguage_ShouldConvertAndExecuteSQL() {
        // Given
        String dataSourceId = UUID.randomUUID().toString();
        String naturalLanguageQuery = "Show me all users";
        String convertedSql = "SELECT 1 AS result";

        // Mock repository save
        when(queryExecutionRepository.save(any(QueryExecution.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        // Mock SQL validation
        when(sqlExecutionEngine.validate(any(DataSourceId.class), eq(convertedSql))).thenReturn(true);

        // Mock SQL execution
        QueryResult mockResult = new QueryResult();
        mockResult.setTotalRows(5L);
        when(sqlExecutionEngine.execute(any(DataSourceId.class), eq(convertedSql), any()))
            .thenReturn(mockResult);

        // When
        QueryExecution result = queryExecutionService.executeNaturalLanguage(dataSourceId, naturalLanguageQuery);

        // Then
        assertThat(result).isNotNull();
        verify(sqlExecutionEngine).validate(any(DataSourceId.class), any());
        verify(sqlExecutionEngine).execute(any(DataSourceId.class), any(), any());
        verify(queryExecutionRepository, times(2)).save(any(QueryExecution.class));
    }

    @Test
    void exportResult_ShouldReturnExportPath() {
        // Given
        String id = UUID.randomUUID().toString();
        String format = "csv";
        String dataSourceId = UUID.randomUUID().toString();

        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId),
            "SELECT 1",
            Collections.emptyMap()
        );

        // 设置查询为已完成状态
        execution.markAsCompleted(10L);

        // 设置查询结果
        QueryResult mockResult = new QueryResult();
        mockResult.setTotalRows(10L);
        execution.setResult(mockResult);

        when(queryExecutionRepository.findById(id)).thenReturn(Optional.of(execution));

        // When
        String exportPath = queryExecutionService.exportResult(id, format);

        // Then
        assertThat(exportPath).isNotNull();
        assertThat(exportPath).contains(id);
        assertThat(exportPath).endsWith(".csv");
        verify(queryExecutionRepository).findById(id);
    }
}
