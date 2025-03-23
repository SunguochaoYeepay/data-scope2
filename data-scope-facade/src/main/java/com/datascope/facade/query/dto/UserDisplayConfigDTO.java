package com.datascope.facade.query.dto;

import java.time.LocalDateTime;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;

import lombok.Data;

/**
 * User display configuration DTO
 * 
 * @author dreambt
 */
@Data
public class UserDisplayConfigDTO {
    /**
     * Primary key ID
     */
    private String id;

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
    private Long usageCount;

    /**
     * Last used time
     */
    private LocalDateTime lastUsedAt;

    /**
     * Creation time
     */
    private LocalDateTime createdAt;

    /**
     * Creator
     */
    private String createdBy;

    /**
     * Last modification time
     */
    private LocalDateTime modifiedAt;

    /**
     * Last modifier
     */
    private String modifiedBy;
}