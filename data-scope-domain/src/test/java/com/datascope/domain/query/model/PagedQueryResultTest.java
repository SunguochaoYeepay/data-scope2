package com.datascope.domain.query.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class PagedQueryResultTest {

    @Test
    void fromQueryResult_ShouldCreatePagedResult() {
        // Given
        QueryResult queryResult = new QueryResult();
        queryResult.setTotalRows(100);
        queryResult.setHasMore(true);
        queryResult.setExecutionTime(150);

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
        queryResult.setColumns(columns);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", i + 1);
            row.put("name", "User " + (i + 1));
            rows.add(row);
        }
        queryResult.setRows(rows);

        int pageNumber = 2;
        int pageSize = 10;

        // When
        PagedQueryResult pagedResult = PagedQueryResult.fromQueryResult(queryResult, pageNumber, pageSize);

        // Then
        assertThat(pagedResult).isNotNull();
        assertThat(pagedResult.getTotalRows()).isEqualTo(100);
        assertThat(pagedResult.isHasMore()).isTrue();
        assertThat(pagedResult.getExecutionTime()).isEqualTo(150);
        assertThat(pagedResult.getColumns()).isEqualTo(columns);
        assertThat(pagedResult.getRows()).isEqualTo(rows);

        assertThat(pagedResult.getPageNumber()).isEqualTo(2);
        assertThat(pagedResult.getPageSize()).isEqualTo(10);
        assertThat(pagedResult.getTotalPages()).isEqualTo(10);
        assertThat(pagedResult.isFirst()).isFalse();
        assertThat(pagedResult.isLast()).isFalse();
        assertThat(pagedResult.getNextPage()).isEqualTo(3);
        assertThat(pagedResult.getPreviousPage()).isEqualTo(1);
    }

    @Test
    void calculateTotalPages_ShouldHandleEdgeCases() {
        // Given
        QueryResult queryResult = new QueryResult();
        queryResult.setTotalRows(0);

        // When - Empty result
        PagedQueryResult emptyResult = PagedQueryResult.fromQueryResult(queryResult, 1, 10);

        // Then
        assertThat(emptyResult.getTotalPages()).isEqualTo(0);
        assertThat(emptyResult.isFirst()).isTrue();
        assertThat(emptyResult.isLast()).isTrue();

        // When - Last page
        queryResult.setTotalRows(100);
        PagedQueryResult lastPageResult = PagedQueryResult.fromQueryResult(queryResult, 10, 10);

        // Then
        assertThat(lastPageResult.getTotalPages()).isEqualTo(10);
        assertThat(lastPageResult.isFirst()).isFalse();
        assertThat(lastPageResult.isLast()).isTrue();
        assertThat(lastPageResult.getNextPage()).isEqualTo(10);
        assertThat(lastPageResult.getPreviousPage()).isEqualTo(9);

        // When - First page
        PagedQueryResult firstPageResult = PagedQueryResult.fromQueryResult(queryResult, 1, 10);

        // Then
        assertThat(firstPageResult.getTotalPages()).isEqualTo(10);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(firstPageResult.isLast()).isFalse();
        assertThat(firstPageResult.getNextPage()).isEqualTo(2);
        assertThat(firstPageResult.getPreviousPage()).isEqualTo(1);
    }
}
