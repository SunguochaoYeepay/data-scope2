package com.datascope.infrastructure.mybatis.typehandler;

import com.datascope.domain.datasource.model.DataSourceId;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MyBatis TypeHandler for DataSourceId
 */
@MappedTypes(DataSourceId.class)
public class DataSourceIdTypeHandler extends BaseTypeHandler<DataSourceId> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, DataSourceId parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public DataSourceId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value == null ? null : new DataSourceId(value);
    }

    @Override
    public DataSourceId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value == null ? null : new DataSourceId(value);
    }

    @Override
    public DataSourceId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value == null ? null : new DataSourceId(value);
    }
}