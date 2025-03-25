package com.datascope.facade.query.dto;

import com.datascope.facade.query.enums.QueryExecutionStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 查询DTO
 */
@Data
public class QueryDTO {

    /**
     * 查询ID
     */
    private String id;

    /**
     * 查询名称
     */
    @NotBlank(message = "查询名称不能为空")
    private String name;

    /**
     * 数据源ID
     */
    @NotBlank(message = "数据源ID不能为空")
    private String dataSourceId;

    /**
     * SQL语句
     */
    private String queryText;

    /**
     * SQL语句
     */
    @NotBlank(message = "SQL语句不能为空")
    private String sql;

    /**
     * 查询参数
     */
    private Map<String, Object> parameters;

    /**
     * 查询描述
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 修改时间
     */
    private LocalDateTime modifiedAt;

    /**
     * 修改人
     */
    private String modifiedBy;

    /**
     * 最后执行时间
     */
    private LocalDateTime lastExecutedAt;

    /**
     * 最后执行状态
     */
    private QueryExecutionStatus lastExecutionStatus;

    /**
     * 最后执行消息
     */
    private String lastExecutionMessage;
}
