package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户显示配置实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDisplayConfig extends BaseEntity {

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
     * 使用次数
     */
    private Long usageCount;

    /**
     * 最后使用时间
     */
    private Long lastUsedAt;
}