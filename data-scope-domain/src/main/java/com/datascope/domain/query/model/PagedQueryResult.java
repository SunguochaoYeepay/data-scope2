package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 分页查询结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PagedQueryResult extends QueryResult {
    /**
     * 当前页码
     */
    private int pageNumber;

    /**
     * 每页大小
     */
    private int pageSize;

    /**
     * 总页数
     */
    private int totalPages;

    /**
     * 是否为第一页
     */
    private boolean isFirst;

    /**
     * 是否为最后一页
     */
    private boolean isLast;

    /**
     * 从QueryResult创建PagedQueryResult
     *
     * @param result     查询结果
     * @param pageNumber 当前页码
     * @param pageSize   每页大小
     * @return 分页查询结果
     */
    public static PagedQueryResult fromQueryResult(QueryResult result, int pageNumber, int pageSize) {
        PagedQueryResult pagedResult = new PagedQueryResult();

        // 复制QueryResult的属性
        pagedResult.setColumns(result.getColumns());
        pagedResult.setRows(result.getRows());
        pagedResult.setTotalRows(result.getTotalRows());
        pagedResult.setHasMore(result.isHasMore());
        pagedResult.setExecutionTime(result.getExecutionTime());

        // 设置分页属性
        pagedResult.setPageNumber(pageNumber);
        pagedResult.setPageSize(pageSize);

        // 计算总页数
        pagedResult.calculateTotalPages();

        return pagedResult;
    }

    /**
     * 计算总页数
     */
    public void calculateTotalPages() {
        if (pageSize <= 0) {
            this.totalPages = 0;
            return;
        }

        this.totalPages = (int) Math.ceil((double) super.getTotalRows() / pageSize);
        this.isFirst = pageNumber <= 1;
        this.isLast = pageNumber >= totalPages;
    }

    /**
     * 获取下一页页码
     */
    public int getNextPage() {
        return isLast ? pageNumber : pageNumber + 1;
    }

    /**
     * 获取上一页页码
     */
    public int getPreviousPage() {
        return isFirst ? 1 : pageNumber - 1;
    }
}
