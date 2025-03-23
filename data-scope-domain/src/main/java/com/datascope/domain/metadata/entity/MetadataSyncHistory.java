package com.datascope.domain.metadata.entity;

import com.datascope.domain.common.entity.BaseEntity;
import com.datascope.domain.metadata.enums.SyncStatus;
import com.datascope.domain.metadata.enums.SyncType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 元数据同步历史
 */
@Getter
@Setter
public class MetadataSyncHistory extends BaseEntity {

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 同步类型
     */
    private SyncType syncType;

    /**
     * 同步状态
     */
    private SyncStatus status;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 错误信息
     */
    private String errorMessage;
}