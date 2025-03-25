package com.datascope.domain.datasource.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 数据源ID值对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataSourceId {
    private String value;

    /**
     * 创建数据源ID
     *
     * @param id 数据源ID字符串
     * @return 数据源ID对象
     */
    public static DataSourceId of(String id) {
        return new DataSourceId(id);
    }

    /**
     * 获取数据源ID值
     *
     * @return 数据源ID字符串
     */
    public String getValue() {
        return value;
    }
}
