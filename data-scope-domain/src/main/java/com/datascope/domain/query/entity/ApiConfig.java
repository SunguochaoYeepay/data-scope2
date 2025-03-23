package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * API接口配置
 */
@Getter
@Setter
public class ApiConfig extends BaseEntity {

    /**
     * API路径
     */
    private String path;

    /**
     * API版本
     */
    private String version;

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
     * 超时时间(ms)
     */
    private Integer timeout;

    /**
     * 最大返回行数
     */
    private Integer maxRows;

    /**
     * 访问频率限制(次/分钟)
     */
    private Integer rateLimit;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}