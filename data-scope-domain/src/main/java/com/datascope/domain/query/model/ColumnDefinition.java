package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 列定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefinition {

    /**
     * 列名
     */
    private String name;

    /**
     * 列标签
     */
    private String label;

    /**
     * 字段名（用于数据访问）
     */
    private String field;

    /**
     * 数据类型
     */
    private String dataType;

    /**
     * 数据类型（兼容type字段）
     */
    private String type;

    /**
     * 是否可为空
     */
    private boolean nullable;

    /**
     * 是否自增
     */
    private boolean autoIncrement;

    /**
     * 是否主键
     */
    private boolean primaryKey;

    /**
     * 是否可排序
     */
    private boolean sortable = true;

    /**
     * 是否可过滤
     */
    private boolean filterable = true;

    /**
     * 列宽度
     */
    private int width;

    /**
     * 列描述
     */
    private String description;

    /**
     * 列格式化模式
     */
    private String formatPattern;

    /**
     * 是否可见
     */
    private boolean visible = true;

    /**
     * 列顺序
     */
    private int order;

    /**
     * 创建列定义
     *
     * @param name     列名
     * @param field    字段名
     * @param dataType 数据类型
     */
    public ColumnDefinition(String name, String field, String dataType) {
        this.name = name;
        this.field = field;
        this.dataType = dataType;
        this.type = dataType; // 兼容type字段
    }

    /**
     * 创建列定义
     *
     * @param name  列名
     * @param field 字段名
     */
    public ColumnDefinition(String name, String field) {
        this(name, field, "STRING");
    }

    /**
     * 设置数据类型，同时设置type字段以保持兼容性
     *
     * @param dataType 数据类型
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
        this.type = dataType;
    }

    /**
     * 设置type字段，同时设置dataType字段以保持兼容性
     *
     * @param type 数据类型
     */
    public void setType(String type) {
        this.type = type;
        this.dataType = type;
    }
}
