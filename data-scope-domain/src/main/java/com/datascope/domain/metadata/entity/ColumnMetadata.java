package com.datascope.domain.metadata.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 列元数据
 */
@Getter
@Setter
public class ColumnMetadata extends BaseEntity {

    /**
     * 表元数据ID
     */
    private String tableMetadataId;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 列大小
     */
    private Integer columnSize;

    /**
     * 小数位数
     */
    private Integer decimalDigits;

    /**
     * 是否可空
     */
    private Boolean nullable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 列描述
     */
    private String description;
}