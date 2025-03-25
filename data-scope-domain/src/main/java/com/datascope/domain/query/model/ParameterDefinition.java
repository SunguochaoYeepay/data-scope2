package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParameterDefinition {
    private String name;
    private String dataType;
    private boolean required;
    private Object defaultValue;

    /**
     * 设置数据类型
     *
     * @param type 数据类型
     * @return 当前对象
     */
    public static class ParameterDefinitionBuilder {
        public ParameterDefinitionBuilder type(String type) {
            this.dataType = type;
            return this;
        }
    }
}
