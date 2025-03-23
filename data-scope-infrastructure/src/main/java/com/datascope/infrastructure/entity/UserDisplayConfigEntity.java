package com.datascope.infrastructure.entity;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDisplayConfigEntity {
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
    private Integer usageCount;
    private LocalDateTime lastUsedAt;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
