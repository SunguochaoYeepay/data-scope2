package com.datascope.domain.query.entity;

import com.datascope.domain.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 低代码配置
 */
@Getter
@Setter
public class LowCodeConfig extends BaseEntity {

    /**
     * API配置ID
     */
    private String apiConfigId;

    /**
     * 页面类型
     */
    private String pageType;

    /**
     * 页面标题
     */
    private String pageTitle;

    /**
     * 查询表单配置(JSON)
     * {
     *   "layout": "horizontal|vertical",
     *   "columns": 1,
     *   "conditions": [{
     *     "field": "name",
     *     "label": "姓名",
     *     "component": "TEXT_INPUT",
     *     "required": true,
     *     "visible": true,
     *     "order": 1,
     *     "config": {}
     *   }]
     * }
     */
    private String formConfig;

    /**
     * 结果表格配置(JSON)
     * {
     *   "columns": [{
     *     "field": "name",
     *     "title": "姓名",
     *     "width": 100,
     *     "fixed": false,
     *     "visible": true,
     *     "sensitive": false,
     *     "order": 1
     *   }],
     *   "operations": [{
     *     "type": "view|edit|delete",
     *     "text": "查看",
     *     "icon": "eye",
     *     "permission": "view"
     *   }]
     * }
     */
    private String tableConfig;

    /**
     * 图表配置(JSON)
     * {
     *   "type": "line|bar|pie",
     *   "title": "销售趋势",
     *   "xField": "date",
     *   "yField": "amount",
     *   "seriesField": "type"
     * }
     */
    private String chartConfig;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 备注
     */
    private String remark;
}