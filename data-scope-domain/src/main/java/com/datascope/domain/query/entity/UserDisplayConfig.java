package com.datascope.domain.query.entity;

import java.time.LocalDateTime;

import com.datascope.domain.common.entity.BaseEntity;
import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * User display configuration entity
 * 
 * @author dreambt
 */
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserDisplayConfig extends BaseEntity {
    /**
     * User ID
     */
    private String userId;

    /**
     * Data source ID
     */
    private String dataSourceId;

    /**
     * Table name
     */
    private String tableName;

    /**
     * Column name
     */
    private String columnName;

    /**
     * Display name
     */
    private String displayName;

    /**
     * Column width
     */
    private Integer width;

    /**
     * Column alignment
     */
    private ColumnAlign align;

    /**
     * Column fixed position
     */
    private ColumnFixed fixed;

    /**
     * Whether the column is visible
     */
    private Boolean visible;

    /**
     * Column order number
     */
    private Integer orderNum;

    /**
     * Whether the column is sortable
     */
    private Boolean sortable;

    /**
     * Whether the column is searchable
     */
    private Boolean searchable;

    /**
     * Whether the column is required
     */
    private Boolean required;

    /**
     * Column mask type
     */
    private MaskType maskType;

    /**
     * Column mask configuration
     */
    private String maskConfig;

    /**
     * Usage count
     */
    private Long usageCount = 0L;

    /**
     * Last used time
     */
    private LocalDateTime lastUsedAt;

    /**
     * Update usage statistics
     */
    public void updateUsage() {
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
    }
}
