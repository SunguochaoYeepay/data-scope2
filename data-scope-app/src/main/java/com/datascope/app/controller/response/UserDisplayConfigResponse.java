package com.datascope.app.controller.response;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户显示配置响应
 */
@Data
@Schema(description = "用户显示配置响应")
public class UserDisplayConfigResponse {

    @Schema(description = "配置ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "数据源ID")
    private String dataSourceId;

    @Schema(description = "表名")
    private String tableName;

    @Schema(description = "列名")
    private String columnName;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "列宽度")
    private Integer width;

    @Schema(description = "列对齐方式")
    private ColumnAlign align;

    @Schema(description = "列固定位置")
    private ColumnFixed fixed;

    @Schema(description = "是否可见")
    private Boolean visible;

    @Schema(description = "显示顺序")
    private Integer displayOrder;

    @Schema(description = "是否为查询条件")
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

    @Schema(description = "使用次数")
    private Long usageCount;

    @Schema(description = "最后使用时间")
    private Long lastUsedAt;

    @Schema(description = "创建时间")
    private Long createdAt;

    @Schema(description = "创建人")
    private String createdBy;

    @Schema(description = "最后修改时间")
    private Long modifiedAt;

    @Schema(description = "最后修改人")
    private String modifiedBy;
}