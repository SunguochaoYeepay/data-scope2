package com.datascope.app.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 执行查询请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "执行查询请求")
public class ExecuteQueryRequest {

    @NotBlank(message = "数据源ID不能为空")
    @Schema(description = "数据源ID", required = true, example = "ds_123456")
    private String dataSourceId;

    @NotBlank(message = "SQL查询语句不能为空")
    @Schema(description = "SQL查询语句", required = true, example = "SELECT * FROM users WHERE age > 18")
    private String sql;

    @Schema(description = "查询参数", example = "{\"age\": 18}")
    private Map<String, Object> parameters;

    @Schema(description = "页码（从0开始）", example = "0")
    private Integer page;

    @Schema(description = "每页记录数", example = "10")
    private Integer size;

    @Schema(description = "排序字段", example = "id")
    private String sortColumn;

    @Schema(description = "排序方向（asc或desc）", example = "asc")
    private String sortDirection;
}
