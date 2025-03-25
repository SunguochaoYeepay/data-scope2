package com.datascope.domain.query.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SqlMetadata {
    private String sqlType;
    private List<String> tableNames;
    private List<ColumnDefinition> columns;
    private List<ParameterDefinition> parameters;
    private boolean hasAggregation;
    private boolean hasGroupBy;
    private boolean hasOrderBy;

    /**
     * 设置SQL类型
     *
     * @param type SQL类型
     * @return 当前对象
     */
    public static class SqlMetadataBuilder {
        public SqlMetadataBuilder type(String type) {
            this.sqlType = type;
            return this;
        }
    }
}
