package com.datascope.app.controller.request;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户显示配置请求
 */
@Data
@Schema(description = "用户显示配置请求")
public class UserDisplayConfigRequest {

    @Schema(description = "配置ID")
    private String id;

    @NotBlank(message = "用户ID不能为空")
    @Schema(description = "用户ID", required = true)
    private String userId;

    @NotBlank(message = "数据源ID不能为空")
    @Schema(description = "数据源ID", required = true)
    private String dataSourceId;

    @NotBlank(message = "表名不能为空")
    @Schema(description = "表名", required = true)
    private String tableName;

    @NotBlank(message = "列名不能为空")
    @Schema(description = "列名", required = true)
    private String columnName;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "列宽度")
    private Integer width;

    @Schema(description = "列对齐方式")
    private ColumnAlign align;

    @Schema(description = "列固定位置")
    private ColumnFixed fixed;

    @NotNull(message = "是否可见不能为空")
    @Schema(description = "是否可见", required = true)
    private Boolean visible;

    @NotNull(message = "显示顺序不能为空")
    @Schema(description = "显示顺序", required = true)
    private Integer displayOrder;

    @NotNull(message = "是否为查询条件不能为空")
    @Schema(description = "是否为查询条件", required = true)
    private Boolean isQueryCondition;

    @Schema(description = "查询条件是否必填")
    private Boolean isRequired;

    @Schema(description = "查询条件默认值")
    private String defaultValue;

    @Schema(description = "是否为高级查询条件")
    private Boolean isAdvancedCondition;

    @Schema(description = "数据掩码类型")
    private MaskType maskType;

    @Schema(description = "掩码配置(JSON)")
    private String maskConfig;
}