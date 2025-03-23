package com.datascope.domain.common.entity;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * Base entity class with common fields
 * 
 * @author dreambt
 */
@Data
public abstract class BaseEntity {
    /**
     * Primary key ID (UUID)
     */
    private String id;

    /**
     * Optimistic lock version
     */
    private Integer nonce;

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

    /**
     * Pre-persist hook
     */
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.modifiedAt == null) {
            this.modifiedAt = LocalDateTime.now();
        }
        if (this.nonce == null) {
            this.nonce = 0;
        }
    }

    /**
     * Pre-update hook
     */
    public void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
        if (this.nonce == null) {
            this.nonce = 0;
        }
        this.nonce++;
    }
}