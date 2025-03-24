package com.datascope.domain.query.entity;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Domain entity for user display configuration
 */
@Data
@Builder
@With
@NoArgsConstructor
@AllArgsConstructor
public class UserDisplayConfig {
    private String id;
    private String userId;
    private String dataSourceId;
    private String tableName;
    private String columnName;
    private String displayName;
    private Integer width;
    private ColumnFixed fixed;
    private ColumnAlign align;
    private Boolean visible;
    private Integer order;
    private Boolean searchable;
    private Boolean required;
    private MaskType maskType;
    private String maskConfig;
    private Boolean sortable;
    private String sortType;
    private Integer usageCount;
    private LocalDateTime lastUsedTime;
    private String createdBy;
    private LocalDateTime createdTime;
    private String modifiedBy;
    private LocalDateTime modifiedTime;

    public UserDisplayConfig copy() {
        UserDisplayConfig copy = new UserDisplayConfig();
        copy.userId = this.userId;
        copy.dataSourceId = this.dataSourceId;
        copy.tableName = this.tableName;
        copy.columnName = this.columnName;
        copy.displayName = this.displayName;
        copy.width = this.width;
        copy.align = this.align;
        copy.fixed = this.fixed;
        copy.visible = this.visible;
        copy.order = this.order;
        copy.sortable = this.sortable;
        copy.searchable = this.searchable;
        copy.required = this.required;
        copy.maskType = this.maskType;
        copy.maskConfig = this.maskConfig;
        copy.usageCount = this.usageCount;
        copy.createdBy = this.createdBy;
        copy.createdTime = this.createdTime;
        copy.modifiedBy = this.modifiedBy;
        copy.modifiedTime = this.modifiedTime;
        return copy;
    }

}
