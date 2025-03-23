package com.datascope.domain.metadata.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 表关系
 */
@Getter
@Setter
public class TableRelation extends BaseEntity {

    /**
     * 源表ID
     */
    private String sourceTableId;

    /**
     * 目标表ID
     */
    private String targetTableId;

    /**
     * 关系类型
     */
    private String relationType;

    /**
     * 置信度
     */
    private BigDecimal confidence;

    /**
     * 源表关联列(JSON)
     */
    private String sourceColumns;

    /**
     * 目标表关联列(JSON)
     */
    private String targetColumns;
}