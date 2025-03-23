package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryResult {
    private List<ColumnDefinition> columns;
    private List<Map<String, Object>> rows;
    private long totalRows;
    private boolean hasMore;
    private long executionTime;
}
