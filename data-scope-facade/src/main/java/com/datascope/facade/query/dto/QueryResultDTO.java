package com.datascope.facade.query.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 查询结果DTO
 */
@Data
public class QueryResultDTO {

    /**
     * 查询ID
     */
    private String queryId;

    /**
     * 执行时间
     */
    private LocalDateTime executedAt;

    /**
     * 执行人
     */
    private String executedBy;

    /**
     * 执行状态
     */
    private QueryDTO.QueryExecutionStatus status;

    /**
     * 执行消息
     */
    private String message;

    /**
     * 执行耗时（毫秒）
     */
    private Long duration;

    /**
     * 结果列名列表
     */
    private List<String> columnNames;

    /**
     * 结果数据列表
     */
    private List<Map<String, Object>> data;

    /**
     * 总记录数
     */
    private Long totalCount;

    /**
     * 是否有更多数据
     */
    private Boolean hasMore;
}