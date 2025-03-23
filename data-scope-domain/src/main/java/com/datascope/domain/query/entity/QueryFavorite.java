package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询收藏
 */
@Getter
@Setter
public class QueryFavorite extends BaseEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 查询名称
     */
    private String queryName;

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 查询文本
     */
    private String queryText;

    /**
     * 生成的SQL
     */
    private String generatedSql;

    /**
     * 查询条件配置(JSON)
     */
    private String queryConditions;

    /**
     * 结果列配置(JSON)
     */
    private String resultColumns;

    /**
     * 显示配置(JSON)
     */
    private String displayConfig;

    /**
     * 备注
     */
    private String remark;
}