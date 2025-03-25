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
     * 获取当前页码
     *
     * @return 当前页码
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * 设置当前页码
     *
     * @param pageNumber 当前页码
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * 获取每页大小
     *
     * @return 每页大小
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页大小
     *
     * @param pageSize 每页大小
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 获取总页数
     *
     * @return 总页数
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * 设置总页数
     *
     * @param totalPages 总页数
     */
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    /**
     * 是否为第一页
     *
     * @return 是否为第一页
     */
    public boolean isFirst() {
        return isFirst;
    }

    /**
     * 设置是否为第一页
     *
     * @param isFirst 是否为第一页
     */
    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    /**
     * 是否为最后一页
     *
     * @return 是否为最后一页
     */
    public boolean isLast() {
        return isLast;
    }

    /**
     * 设置是否为最后一页
     *
     * @param isLast 是否为最后一页
     */
    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }

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
