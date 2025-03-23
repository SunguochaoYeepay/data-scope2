package com.datascope.domain.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Base entity class with common audit fields
 * 
 * @author dreambt
 */
@Data
public abstract class BaseEntity {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 乐观锁版本号
     */
    private Integer nonce;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 最后修改时间
     */
    private LocalDateTime modifiedAt;

    /**
     * 最后修改人
     */
    private String modifiedBy;

    /**
     * 初始化审计字段
     */
    public void initAuditFields(String operator) {
        LocalDateTime now = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = now;
            this.createdBy = operator;
        }
        this.modifiedAt = now;
        this.modifiedBy = operator;
        if (this.nonce == null) {
            this.nonce = 0;
        }
    }

    /**
     * 更新审计字段
     */
    public void updateAuditFields(String operator) {
        this.modifiedAt = LocalDateTime.now();
        this.modifiedBy = operator;
        this.nonce++;
    }
}