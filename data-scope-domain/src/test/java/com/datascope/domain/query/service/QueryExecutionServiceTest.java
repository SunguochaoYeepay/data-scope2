package com.datascope.domain.query.service;

import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.QueryExecution;
import com.datascope.domain.query.repository.QueryExecutionRepository;
import com.datascope.domain.query.service.impl.QueryExecutionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryExecutionServiceTest {

    @Mock
    private QueryExecutionRepository queryExecutionRepository;

    private QueryExecutionService queryExecutionService;

    @BeforeEach
    void setUp() {
        queryExecutionService = new QueryExecutionServiceImpl(queryExecutionRepository);
    }

    @Test
    void executeSql_ShouldCreateAndSaveExecution() {
        // Given
        String dataSourceId = UUID.randomUUID().toString();
        String sql = "SELECT * FROM test";
        Map<String, Object> parameters = Collections.emptyMap();

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
    void cancel_ShouldMarkExecutionAsFailed() {
        // Given
        String id = UUID.randomUUID().toString();
        String dataSourceId = UUID.randomUUID().toString();
        QueryExecution execution = new QueryExecution(
            DataSourceId.of(dataSourceId.toString()),
            "SELECT 1",
            Collections.emptyMap()
        );
        when(queryExecutionRepository.findById(id)).thenReturn(Optional.of(execution));
        when(queryExecutionRepository.save(any(QueryExecution.class))).thenReturn(execution);

        // When
        queryExecutionService.cancel(id);

        // Then
        verify(queryExecutionRepository).save(any(QueryExecution.class));
    }
}
