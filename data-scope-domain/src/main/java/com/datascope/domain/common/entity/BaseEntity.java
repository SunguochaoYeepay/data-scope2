package com.datascope.domain.common.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * 基础实体类，包含所有实体共有的属性
 */
@Getter
@Setter
@ToString
public abstract class BaseEntity {

    /**
     * 主键ID，采用UUID
     */
    private String id;

    /**
     * 乐观锁版本号
     */
    private Long nonce;

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
     * 初始化基础字段
     *
     * @param operator 操作人
     */
    public void init(String operator) {
        LocalDateTime now = LocalDateTime.now();
        this.nonce = 0L;
        this.createdAt = now;
        this.createdBy = operator;
        this.modifiedAt = now;
        this.modifiedBy = operator;
    }

    /**
     * 更新基础字段
     *
     * @param operator 操作人
     */
    public void update(String operator) {
        this.nonce = this.nonce + 1;
        this.modifiedAt = LocalDateTime.now();
        this.modifiedBy = operator;
    }
}