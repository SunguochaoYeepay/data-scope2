package com.datascope.domain.query.util;

import java.util.HashMap;
import java.util.Map;

import com.datascope.domain.query.enums.ComponentType;

/**
 * 数据库列类型与显示组件映射工具类
 */
public class ComponentTypeMapper {
    private static final Map<String, ComponentType> TYPE_MAPPING = new HashMap<>();

    static {
        // 数值类型
        TYPE_MAPPING.put("TINYINT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("SMALLINT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("MEDIUMINT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("INT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("BIGINT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("FLOAT", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("DOUBLE", ComponentType.NUMBER_INPUT);
        TYPE_MAPPING.put("DECIMAL", ComponentType.NUMBER_INPUT);

        // 字符串类型
        TYPE_MAPPING.put("CHAR", ComponentType.TEXT_INPUT);
        TYPE_MAPPING.put("VARCHAR", ComponentType.TEXT_INPUT);
        TYPE_MAPPING.put("TINYTEXT", ComponentType.TEXTAREA);
        TYPE_MAPPING.put("TEXT", ComponentType.TEXTAREA);
        TYPE_MAPPING.put("MEDIUMTEXT", ComponentType.RICH_TEXT);
        TYPE_MAPPING.put("LONGTEXT", ComponentType.RICH_TEXT);

        // 日期时间类型
        TYPE_MAPPING.put("DATE", ComponentType.DATE_PICKER);
        TYPE_MAPPING.put("TIME", ComponentType.TIME_PICKER);
        TYPE_MAPPING.put("DATETIME", ComponentType.DATETIME_PICKER);
        TYPE_MAPPING.put("TIMESTAMP", ComponentType.DATETIME_PICKER);
        TYPE_MAPPING.put("YEAR", ComponentType.NUMBER_INPUT);

        // 布尔类型
        TYPE_MAPPING.put("BOOLEAN", ComponentType.SWITCH);
        TYPE_MAPPING.put("BOOL", ComponentType.SWITCH);

        // 二进制类型
        TYPE_MAPPING.put("BINARY", ComponentType.FILE_UPLOAD);
        TYPE_MAPPING.put("VARBINARY", ComponentType.FILE_UPLOAD);
        TYPE_MAPPING.put("BLOB", ComponentType.FILE_UPLOAD);
    }

    /**
     * 根据数据库列类型获取对应的显示组件类型
     *
     * @param columnType 数据库列类型
     * @return 显示组件类型
     */
    public static ComponentType getComponentType(String columnType) {
        if (columnType == null) {
            return ComponentType.TEXT_INPUT;
        }
        
        String upperType = columnType.toUpperCase();
        return TYPE_MAPPING.getOrDefault(upperType, ComponentType.TEXT_INPUT);
    }
}