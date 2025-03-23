package com.datascope.domain.query.entity;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Accessors(chain = true)
public class UserDisplayConfig {
    private String id;
    private String userId;
    private String dataSourceId;
    private String tableName;
    private String columnName;
    private String displayName;
    private Integer width;
    private ColumnAlign align;
    private ColumnFixed columnFixed;
    private Boolean visible;
    private Integer orderNum;
    private Boolean sortable;
    private Boolean searchable;
    private Boolean required;
    private MaskType maskType;
    private String maskConfig;
    private Integer usageCount = 0;
    private LocalDateTime lastUsedAt;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;

    public UserDisplayConfig init(String operator) {
        this.createdBy = operator;
        this.createdTime = LocalDateTime.now();
        this.updatedBy = operator;
        this.updatedTime = LocalDateTime.now();
        this.usageCount = 0;
        return this;
    }

    public UserDisplayConfig update(String operator) {
        this.updatedBy = operator;
        this.updatedTime = LocalDateTime.now();
        return this;
    }

    public UserDisplayConfig incrementUsageCount() {
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        this.usageCount++;
        this.lastUsedAt = LocalDateTime.now();
        return this;
    }

    public UserDisplayConfig copy() {
        UserDisplayConfig copy = new UserDisplayConfig();
        copy.dataSourceId = this.dataSourceId;
        copy.tableName = this.tableName;
        copy.columnName = this.columnName;
        copy.displayName = this.displayName;
        copy.width = this.width;
        copy.align = this.align;
        copy.columnFixed = this.columnFixed;
        copy.visible = this.visible;
        copy.orderNum = this.orderNum;
        copy.sortable = this.sortable;
        copy.searchable = this.searchable;
        copy.required = this.required;
        copy.maskType = this.maskType;
        copy.maskConfig = this.maskConfig;
        copy.usageCount = 0;
        return copy;
    }
}
