package com.datascope.domain.common.model;

import java.time.LocalDateTime;

/**
 * 审计信息
 */
public class AuditInfo {
    private String createdBy;
    private LocalDateTime createdAt;
    private String modifiedBy;
    private LocalDateTime modifiedAt;

    public AuditInfo(String createdBy) {
        this.createdBy = createdBy;
        this.createdAt = LocalDateTime.now();
        this.modifiedBy = createdBy;
        this.modifiedAt = this.createdAt;
    }

    public void updateModifiedInfo(String modifiedBy) {
        this.modifiedBy = modifiedBy;
        this.modifiedAt = LocalDateTime.now();
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
}