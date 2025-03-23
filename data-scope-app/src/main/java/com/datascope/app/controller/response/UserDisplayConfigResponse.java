package com.datascope.app.controller.response;

import java.time.LocalDateTime;

import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

import lombok.Data;

/**
 * User display configuration response
 * 
 * @author dreambt
 */
@Data
public class UserDisplayConfigResponse {
    /**
     * 主键ID
     */
    private String id;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 列名
     */
    private String columnName;

    /**
     * 显示名称
     */
    private String displayName;

    /**
     * 列宽度
     */
    private Integer width;

    /**
     * 列对齐方式
     */
    private ColumnAlign align;

    /**
     * 列固定方式
     */
    private ColumnFixed fixed;

    /**
     * 是否显示
     */
    private Boolean visible;

    /**
     * 排序号
     */
    private Integer orderNum;

    /**
     * 是否可排序
     */
    private Boolean sortable;

    /**
     * 是否可搜索
     */
    private Boolean searchable;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 掩码类型
     */
    private MaskType maskType;

    /**
     * 掩码配置
     */
    private String maskConfig;

    /**
     * 使用次数
     */
    private Long usageCount;

    /**
     * 最后使用时间
     */
    private LocalDateTime lastUsedAt;

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
     * Convert from DTO
     *
     * @param dto DTO object
     * @return Response object
     */
    public static UserDisplayConfigResponse fromDTO(UserDisplayConfigDTO dto) {
        UserDisplayConfigResponse response = new UserDisplayConfigResponse();
        response.setId(dto.getId());
        response.setUserId(dto.getUserId());
        response.setDataSourceId(dto.getDataSourceId());
        response.setTableName(dto.getTableName());
        response.setColumnName(dto.getColumnName());
        response.setDisplayName(dto.getDisplayName());
        response.setWidth(dto.getWidth());
        response.setAlign(dto.getAlign());
        response.setFixed(dto.getFixed());
        response.setVisible(dto.getVisible());
        response.setOrderNum(dto.getOrderNum());
        response.setSortable(dto.getSortable());
        response.setSearchable(dto.getSearchable());
        response.setRequired(dto.getRequired());
        response.setMaskType(dto.getMaskType());
        response.setMaskConfig(dto.getMaskConfig());
        response.setUsageCount(dto.getUsageCount());
        response.setLastUsedAt(dto.getLastUsedAt());
        response.setCreatedAt(dto.getCreatedAt());
        response.setCreatedBy(dto.getCreatedBy());
        response.setModifiedAt(dto.getModifiedAt());
        response.setModifiedBy(dto.getModifiedBy());
        return response;
    }
}