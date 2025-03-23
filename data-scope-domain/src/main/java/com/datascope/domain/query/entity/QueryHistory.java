package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import com.datascope.domain.query.enums.QueryStatus;
import com.datascope.domain.query.enums.QueryType;
import lombok.Getter;
import lombok.Setter;

/**
 * 查询历史
 */
@Getter
@Setter
public class QueryHistory extends BaseEntity {

    /**
     * 数据源ID
     */
    private String dataSourceId;

    /**
     * 查询类型
     */
    private QueryType queryType;

    /**
     * 查询文本
     */
    private String queryText;

    /**
     * 生成的SQL
     */
    private String generatedSql;

    /**
     * 执行时间(ms)
     */
    private Integer executionTime;

    /**
     * 结果行数
     */
    private Integer rowCount;

    /**
     * 执行状态
     */
    private QueryStatus status;

    /**
     * 错误信息
     */
    private String errorMessage;
}