package com.datascope.domain.query.enums;

/**
 * 页面类型
 */
public enum PageType {
    /**
     * 查询表单页面
     */
    QUERY_FORM,

    /**
     * 查看详情页面
     */
    VIEW_DETAIL,

    /**
     * 表格展示页面
     */
    TABLE_VIEW,

    /**
     * 图表展示页面
     */
    CHART_VIEW,

    /**
     * 复合页面(表单+表格)
     */
    COMPOSITE_FORM_TABLE,

    /**
     * 复合页面(表单+图表)
     */
    COMPOSITE_FORM_CHART,

    /**
     * 复合页面(表格+图表)
     */
    COMPOSITE_TABLE_CHART,

    /**
     * 复合页面(表单+表格+图表)
     */
    COMPOSITE_ALL
}