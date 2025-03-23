package com.datascope.facade.query.dto.request;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户显示配置请求DTO
 */
@Data
public class UserDisplayConfigRequest {

    /**
     * 数据源ID
     */
    @NotBlank(message = "数据源ID不能为空")
    private String dataSourceId;

    /**
     * 表名
     */
    @NotBlank(message = "表名不能为空")
    private String tableName;

    /**
     * 列名
     */
    @NotBlank(message = "列名不能为空")
    private String columnName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 列宽度
     */
    private Integer width;

    /**
     * 列对齐方式
     */
    private ColumnAlign align;

    /**
     * 列固定位置
     */
    private ColumnFixed fixed;

    /**
     * 是否可见
     */
    @NotNull(message = "是否可见不能为空")
    private Boolean visible;

    /**
     * 显示顺序
     */
    @NotNull(message = "显示顺序不能为空")
    private Integer displayOrder;

    /**
     * 是否为查询条件
     */
    private Boolean isQueryCondition;

    /**
     * 查询条件是否必填
     */
    private Boolean isRequired;

    /**
     * 查询条件默认值
     */
    private String defaultValue;

    /**
     * 是否为高级查询条件
     */
    private Boolean isAdvancedCondition;

    /**
     * 数据掩码类型
     */
    private MaskType maskType;

    /**
     * 掩码配置(JSON)
     */
    private String maskConfig;
}