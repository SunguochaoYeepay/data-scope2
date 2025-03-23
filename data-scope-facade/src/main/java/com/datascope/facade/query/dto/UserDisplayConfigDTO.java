package com.datascope.facade.query.dto;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for user display configuration
 */
@Data
@Builder
public class UserDisplayConfigDTO {
    private String id;
    private String userId;
    private String dataSourceId;
    private String tableName;
    private String columnName;
    private String displayName;
    private Integer columnWidth;
    private ColumnFixed fixed;
    private ColumnAlign align;
    private Boolean visible;
    private Integer order;
    private MaskType maskType;
    private String maskChar;
    private Boolean sortable;
    private String sortType;
    private Integer usageCount;
    private LocalDateTime lastUsedAt;
    private String createdBy;
    private LocalDateTime createdTime;
    private String updatedBy;
    private LocalDateTime updatedTime;
}
