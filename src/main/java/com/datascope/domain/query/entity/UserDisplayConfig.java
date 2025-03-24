package com.datascope.domain.query.entity;

import com.datascope.domain.common.model.AuditInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户显示配置实体
 */
@Getter
@Setter
public class UserDisplayConfig {
    private String id;
    private String userId;
    private String dataSourceId;
    private String tableName;
    private String columnName;
    private String displayName;
    private Integer width;
    private String align;
    private String fixed;
    private Boolean visible;
    private Integer orderNum;
    private Boolean sortable;
    private Boolean searchable;
    private Boolean required;
    private String maskType;
    private String maskConfig;
    private Integer usageCount;
    private LocalDateTime lastUsedTime;
    private AuditInfo auditInfo;

    public void updateUsage() {
        if (this.usageCount == null) {
            this.usageCount = 0;
        }
        this.usageCount++;
        this.lastUsedTime = LocalDateTime.now();
    }
}
