package com.datascope.domain.metadata.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 表元数据
 */
@Getter
@Setter
public class TableMetadata extends BaseEntity {

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表类型
     */
    private String tableType;

    /**
     * 表描述
     */
    private String description;

    /**
     * 列元数据列表
     */
    private List<ColumnMetadata> columns;
}