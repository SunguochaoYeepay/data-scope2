package com.datascope.domain.query.enums;

/**
 * 表单布局类型
 */
public enum FormLayout {
    /**
     * 水平布局
     * label和控件在同一行
     */
    HORIZONTAL,

    /**
     * 垂直布局
     * label在控件上方
     */
    VERTICAL,

    /**
     * 内联布局
     * 所有控件在同一行
     */
    INLINE,

    /**
     * 网格布局
     * 按照网格系统布局
     */
    GRID,

    /**
     * 分组布局
     * 按照字段分组布局
     */
    GROUP,

    /**
     * 标签布局
     * 使用标签页分组布局
     */
    TAB,

    /**
     * 步骤布局
     * 按照步骤分组布局
     */
    STEP,

    /**
     * 自定义布局
     * 完全自定义布局
     */
    CUSTOM
}