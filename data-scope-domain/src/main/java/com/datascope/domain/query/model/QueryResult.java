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

    /**
     * 获取总行数
     *
     * @return 总行数
     */
    public long getTotalRows() {
        return totalRows;
    }

    /**
     * 设置总行数
     *
     * @param totalRows 总行数
     */
    public void setTotalRows(long totalRows) {
        this.totalRows = totalRows;
    }

    /**
     * 获取列定义列表
     *
     * @return 列定义列表
     */
    public List<ColumnDefinition> getColumns() {
        return columns;
    }

    /**
     * 设置列定义列表
     *
     * @param columns 列定义列表
     */
    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }

    /**
     * 获取数据行列表
     *
     * @return 数据行列表
     */
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    /**
     * 设置数据行列表
     *
     * @param rows 数据行列表
     */
    public void setRows(List<Map<String, Object>> rows) {
        this.rows = rows;
    }

    /**
     * 是否有更多数据
     *
     * @return 是否有更多数据
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * 设置是否有更多数据
     *
     * @param hasMore 是否有更多数据
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * 获取执行时间
     *
     * @return 执行时间
     */
    public long getExecutionTime() {
        return executionTime;
    }

    /**
     * 设置执行时间
     *
     * @param executionTime 执行时间
     */
    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }
}
