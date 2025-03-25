package com.datascope.app.controller.request;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * User display configuration request
 *
 * @author dreambt
 */
@Data
public class UserDisplayConfigRequest {
    /**
     * 用户ID
     */
    @NotBlank(message = "用户ID不能为空")
    private String userId;

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
    @NotBlank(message = "显示名称不能为空")
    private String displayName;

    /**
     * 列宽度
     */
    @NotNull(message = "列宽度不能为空")
    private Integer width;

    /**
     * 列对齐方式
     */
    @NotNull(message = "列对齐方式不能为空")
    private ColumnAlign align;

    /**
     * 列固定方式
     */
    private ColumnFixed fixed;

    /**
     * 是否显示
     */
    @NotNull(message = "是否显示不能为空")
    private Boolean visible;

    /**
     * 排序号
     */
    @NotNull(message = "排序号不能为空")
    private Integer orderNum;

    /**
     * 是否可排序
     */
    private Boolean sortable;

    /**
     * 是否可搜索
     */
    private Boolean searchable;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 掩码类型
     */
    private MaskType maskType;

    /**
     * 掩码配置
     */
    private String maskConfig;

}
