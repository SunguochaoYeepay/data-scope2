package com.datascope.domain.query.dto;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserDisplayConfigDTO {
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
    private LocalDateTime lastUsedTime;
    private String createdBy;
    private LocalDateTime createdTime;
    private String modifiedBy;
    private LocalDateTime modifiedTime;
}
