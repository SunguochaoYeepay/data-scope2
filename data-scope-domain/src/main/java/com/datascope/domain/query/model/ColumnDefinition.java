package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ColumnDefinition {
    private String name;
    private String label;
    private String dataType;
    private boolean nullable;
    private boolean autoIncrement;
    private boolean primaryKey;

    /**
     * 设置数据类型
     *
     * @param type 数据类型
     * @return 当前对象
     */
    public static class ColumnDefinitionBuilder {
        public ColumnDefinitionBuilder type(String type) {
            this.dataType = type;
            return this;
        }
    }
}
