package com.datascope.facade.query.dto.response;

import com.datascope.facade.query.enums.ColumnAlign;
import com.datascope.facade.query.enums.ColumnFixed;
import com.datascope.facade.query.enums.MaskType;
import lombok.Data;

/**
 * 用户显示配置响应DTO
 */
@Data
public class UserDisplayConfigResponse {

    /**
     * 配置ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列名
     */
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
    private Boolean visible;

    /**
     * 显示顺序
     */
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

    /**
     * 使用频率
     */
    private Integer useFrequency;

    /**
     * 最后使用时间
     */
    private Long lastUsedAt;

    /**
     * 创建时间
     */
    private Long createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 最后修改时间
     */
    private Long modifiedAt;

    /**
     * 最后修改人
     */
    private String modifiedBy;
}
