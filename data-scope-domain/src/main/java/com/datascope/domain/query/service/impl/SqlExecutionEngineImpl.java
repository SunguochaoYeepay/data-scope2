package com.datascope.domain.query.service.impl;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.gateway.DataSourceConnectionGateway;
import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.domain.query.exception.DataExecutionException;
import com.datascope.domain.query.model.ColumnDefinition;
import com.datascope.domain.query.model.ParameterDefinition;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.model.SqlMetadata;
import com.datascope.domain.query.service.SqlExecutionEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL执行引擎实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SqlExecutionEngineImpl implements SqlExecutionEngine {

    // 用于存储正在执行的查询，以支持取消操作
    private final Map<String, Statement> activeStatements = new ConcurrentHashMap<>();

    private final DataSourceRepository dataSourceRepository;
    private final DataSourceConnectionGateway connectionGateway;

    @Override
    public QueryResult execute(DataSourceId dataSourceId, String sql, Map<String, Object> parameters) throws DataExecutionException {
        log.info("Executing SQL query on dataSource: {}, SQL: {}", dataSourceId, sql);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 获取数据源
            Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(dataSourceId.getValue());
            if (dataSourceOpt.isEmpty()) {
                throw new DataExecutionException("Data source not found: " + dataSourceId.getValue());
            }

            // 获取数据库连接
            conn = connectionGateway.getConnection(dataSourceOpt.get());

            // 检查是否需要排序
            boolean isSorted = parameters != null && parameters.containsKey("_sort_fields");

            // 检查是否需要分页
            boolean isPaged = parameters != null && parameters.containsKey("_offset") && parameters.containsKey("_limit");

            // 如果需要分页，先获取总行数
            long estimatedTotalRows = 0;
            if (isPaged) {
                estimatedTotalRows = estimateRowCount(dataSourceId, sql);
            }

            // 构建排序SQL
            String sortedSql = isSorted ? buildSortQuery(sql, parameters) : sql;

            // 构建分页SQL
            String executeSql = isPaged ? buildPagedQuery(sortedSql, parameters) : sortedSql;
            log.info("Executing SQL: {}", executeSql);

            // 创建预编译语句
            stmt = conn.prepareStatement(executeSql);

            // 设置参数
            if (parameters != null && !parameters.isEmpty()) {
                setParameters(stmt, parameters);
            }

            // 记录开始时间
            long startTime = System.currentTimeMillis();

            // 将语句添加到活动语句映射中，以支持取消操作
            String executionId = UUID.randomUUID().toString();
            activeStatements.put(executionId, stmt);

            try {
                // 执行查询
                rs = stmt.executeQuery();

                // 创建查询结果
                QueryResult result = new QueryResult();

                // 处理结果集
                processResultSet(rs, result);

                // 计算执行时间
                long executionTime = System.currentTimeMillis() - startTime;
                result.setExecutionTime(executionTime);

                // 如果是分页查询，设置总行数
                if (parameters != null && parameters.containsKey("_page_number") && parameters.containsKey("_page_size")) {
                    int pageNumber = (int) parameters.get("_page_number");
                    int pageSize = (int) parameters.get("_page_size");
                    long totalRows = isPaged ? estimatedTotalRows : result.getTotalRows();

                    // 计算是否有更多数据
                    boolean hasMore = pageNumber * pageSize < totalRows;
                    result.setHasMore(hasMore);

                    // 如果总行数是通过估算得到的，更新为实际值
                    if (isPaged) {
                        result.setTotalRows(totalRows);
                    }
                }

                log.info("SQL execution completed in {} ms, rows: {}", executionTime, result.getTotalRows());

                return result;
            } finally {
                // 从活动语句映射中移除语句
                activeStatements.remove(executionId);
            }
        } catch (SQLException e) {
            log.error("SQL execution error: {}", e.getMessage(), e);
            throw new DataExecutionException("SQL execution error: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing connection: {}", e.getMessage(), e);
            }
        }
    }

    private void setParameters(PreparedStatement stmt, Map<String, Object> parameters) throws SQLException {
        // 创建一个新的参数Map，排除特殊参数
        Map<String, Object> filteredParams = new HashMap<>();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            // 排除以下划线开头的特殊参数
            if (!entry.getKey().startsWith("_")) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }

        // 处理命名参数
        if (filteredParams.keySet().stream().anyMatch(k -> k.startsWith(":"))) {
            // 命名参数处理逻辑
            Pattern pattern = Pattern.compile(":(\\w+)");
            Matcher matcher = pattern.matcher(stmt.toString());
            int paramIndex = 1;

            while (matcher.find()) {
                String paramName = matcher.group(1);
                Object value = filteredParams.get(":" + paramName);
                if (value != null) {
                    setParameter(stmt, paramIndex++, value);
                }
            }
        } else {
            // 索引参数处理逻辑
            int paramIndex = 1;
            for (Object value : filteredParams.values()) {
                setParameter(stmt, paramIndex++, value);
            }
        }
    }

    private void setParameter(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.NULL);
        } else if (value instanceof String) {
            stmt.setString(index, (String) value);
        } else if (value instanceof Integer) {
            stmt.setInt(index, (Integer) value);
        } else if (value instanceof Long) {
            stmt.setLong(index, (Long) value);
        } else if (value instanceof Double) {
            stmt.setDouble(index, (Double) value);
        } else if (value instanceof Boolean) {
            stmt.setBoolean(index, (Boolean) value);
        } else if (value instanceof java.util.Date) {
            stmt.setDate(index, new java.sql.Date(((java.util.Date) value).getTime()));
        } else if (value instanceof java.time.LocalDate) {
            stmt.setDate(index, java.sql.Date.valueOf((java.time.LocalDate) value));
        } else if (value instanceof java.time.LocalDateTime) {
            stmt.setTimestamp(index, java.sql.Timestamp.valueOf((java.time.LocalDateTime) value));
        } else {
            stmt.setObject(index, value);
        }
    }

    private void processResultSet(ResultSet rs, QueryResult result) throws SQLException {
        // 获取结果集元数据
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // 创建列定义列表
        List<ColumnDefinition> columns = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            ColumnDefinition column = ColumnDefinition.builder()
                .name(metaData.getColumnName(i))
                .label(metaData.getColumnLabel(i))
                .type(metaData.getColumnTypeName(i))
                .nullable(metaData.isNullable(i) == ResultSetMetaData.columnNullable)
                .autoIncrement(metaData.isAutoIncrement(i))
                .primaryKey(false) // 需要额外查询才能确定是否为主键
                .build();
            columns.add(column);
        }
        result.setColumns(columns);

        // 处理行数据
        List<Map<String, Object>> rows = new ArrayList<>();
        long rowCount = 0;

        while (rs.next()) {
            rowCount++;
            Map<String, Object> row = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                row.put(columnName, value);
            }

            rows.add(row);
        }

        result.setRows(rows);
        result.setTotalRows(rowCount);
        result.setHasMore(false); // 默认没有更多数据
    }

    private void closeResources(ResultSet rs, Statement stmt) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error("Error closing ResultSet: {}", e.getMessage(), e);
        }

        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            log.error("Error closing Statement: {}", e.getMessage(), e);
        }
    }

    @Override
    public void cancel(String executionId) throws DataExecutionException {
        log.info("Cancelling query execution: {}", executionId);

        Statement stmt = activeStatements.get(executionId);
        if (stmt != null) {
            try {
                stmt.cancel();
                activeStatements.remove(executionId);
                log.info("Query execution cancelled successfully: {}", executionId);
            } catch (SQLException e) {
                log.error("Error cancelling query execution: {}", e.getMessage(), e);
                throw new DataExecutionException("Error cancelling query execution: " + e.getMessage(), e);
            }
        } else {
            log.warn("No active statement found for execution ID: {}", executionId);
        }
    }

    @Override
    public boolean validate(DataSourceId dataSourceId, String sql) throws DataExecutionException {
        log.info("Validating SQL query on dataSource: {}, SQL: {}", dataSourceId, sql);

        if (sql == null || sql.trim().isEmpty()) {
            return false;
        }

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            // 获取数据源
            Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(dataSourceId.getValue());
            if (dataSourceOpt.isEmpty()) {
                throw new DataExecutionException("Data source not found: " + dataSourceId.getValue());
            }

            // 获取数据库连接
            conn = connectionGateway.getConnection(dataSourceOpt.get());

            // 尝试解析SQL语句
            stmt = conn.prepareStatement(sql);

            // 如果能够成功解析，则认为SQL语句有效
            log.info("SQL validation successful");
            return true;
        } catch (SQLException e) {
            log.warn("SQL validation failed: {}", e.getMessage());
            return false;
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing resources: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    public SqlMetadata getMetadata(DataSourceId dataSourceId, String sql) throws DataExecutionException {
        log.info("Getting metadata for SQL query on dataSource: {}, SQL: {}", dataSourceId, sql);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 获取数据源
            Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(dataSourceId.getValue());
            if (dataSourceOpt.isEmpty()) {
                throw new DataExecutionException("Data source not found: " + dataSourceId.getValue());
            }

            // 获取数据库连接
            conn = connectionGateway.getConnection(dataSourceOpt.get());

            // 创建预编译语句
            stmt = conn.prepareStatement(sql);

            // 获取参数元数据
            ParameterMetaData paramMetaData = stmt.getParameterMetaData();
            int paramCount = paramMetaData.getParameterCount();

            List<ParameterDefinition> parameters = new ArrayList<>();
            for (int i = 1; i <= paramCount; i++) {
                try {
                    ParameterDefinition param = ParameterDefinition.builder()
                        .name("param" + i)
                        .type(paramMetaData.getParameterTypeName(i))
                        .required(paramMetaData.isNullable(i) == ParameterMetaData.parameterNoNulls)
                        .defaultValue(null)
                        .build();
                    parameters.add(param);
                } catch (SQLException e) {
                    // 某些JDBC驱动可能不支持参数元数据的某些方法
                    log.warn("Could not get complete parameter metadata: {}", e.getMessage());
                    ParameterDefinition param = ParameterDefinition.builder()
                        .name("param" + i)
                        .type("UNKNOWN")
                        .required(true)
                        .defaultValue(null)
                        .build();
                    parameters.add(param);
                }
            }

            // 获取结果集元数据
            rs = stmt.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            List<ColumnDefinition> columns = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                ColumnDefinition column = ColumnDefinition.builder()
                    .name(metaData.getColumnName(i))
                    .label(metaData.getColumnLabel(i))
                    .type(metaData.getColumnTypeName(i))
                    .nullable(metaData.isNullable(i) == ResultSetMetaData.columnNullable)
                    .autoIncrement(metaData.isAutoIncrement(i))
                    .primaryKey(false) // 需要额外查询才能确定是否为主键
                    .build();
                columns.add(column);
            }

            // 分析SQL类型
            String sqlType = determineSqlType(sql);

            // 分析表名
            List<String> tableNames = extractTableNames(sql);

            // 分析是否有聚合、分组和排序
            boolean hasAggregation = sql.toUpperCase().contains("COUNT(") ||
                sql.toUpperCase().contains("SUM(") ||
                sql.toUpperCase().contains("AVG(") ||
                sql.toUpperCase().contains("MIN(") ||
                sql.toUpperCase().contains("MAX(");
            boolean hasGroupBy = sql.toUpperCase().contains("GROUP BY");
            boolean hasOrderBy = sql.toUpperCase().contains("ORDER BY");

            // 创建SQL元数据
            SqlMetadata metadata = SqlMetadata.builder()
                .type(sqlType)
                .tableNames(tableNames)
                .columns(columns)
                .parameters(parameters)
                .hasAggregation(hasAggregation)
                .hasGroupBy(hasGroupBy)
                .hasOrderBy(hasOrderBy)
                .build();

            log.info("SQL metadata retrieved successfully");
            return metadata;
        } catch (SQLException e) {
            log.error("Error getting SQL metadata: {}", e.getMessage(), e);
            throw new DataExecutionException("Error getting SQL metadata: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing connection: {}", e.getMessage(), e);
            }
        }
    }

    private String determineSqlType(String sql) {
        String upperSql = sql.trim().toUpperCase();
        if (upperSql.startsWith("SELECT")) {
            return "SELECT";
        } else if (upperSql.startsWith("INSERT")) {
            return "INSERT";
        } else if (upperSql.startsWith("UPDATE")) {
            return "UPDATE";
        } else if (upperSql.startsWith("DELETE")) {
            return "DELETE";
        } else if (upperSql.startsWith("CREATE")) {
            return "CREATE";
        } else if (upperSql.startsWith("ALTER")) {
            return "ALTER";
        } else if (upperSql.startsWith("DROP")) {
            return "DROP";
        } else {
            return "UNKNOWN";
        }
    }

    private List<String> extractTableNames(String sql) {
        List<String> tableNames = new ArrayList<>();

        // 简单的表名提取逻辑，实际项目中可能需要更复杂的SQL解析
        Pattern pattern = Pattern.compile("FROM\\s+([\\w\\.]+)|JOIN\\s+([\\w\\.]+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(sql);

        while (matcher.find()) {
            String tableName = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            if (tableName != null && !tableName.isEmpty()) {
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }

    @Override
    public long estimateRowCount(DataSourceId dataSourceId, String sql) throws DataExecutionException {
        log.info("Estimating row count for SQL query on dataSource: {}, SQL: {}", dataSourceId, sql);

        // 对于简单的SELECT查询，可以使用COUNT(*)来估算行数
        if (!sql.trim().toUpperCase().startsWith("SELECT")) {
            log.warn("Row count estimation is only supported for SELECT queries");
            return -1;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            // 获取数据源
            Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(dataSourceId.getValue());
            if (dataSourceOpt.isEmpty()) {
                throw new DataExecutionException("Data source not found: " + dataSourceId.getValue());
            }

            // 获取数据库连接
            conn = connectionGateway.getConnection(dataSourceOpt.get());

            // 获取数据库类型
            String dbType = dataSourceOpt.get().getType().name();

            // 构造COUNT查询
            String countSql = buildCountQueryForDbType(sql, dbType);

            // 创建预编译语句
            stmt = conn.prepareStatement(countSql);

            // 执行查询
            rs = stmt.executeQuery();

            // 获取行数
            if (rs.next()) {
                long count = rs.getLong(1);
                log.info("Estimated row count: {}", count);
                return count;
            } else {
                log.warn("Count query returned no results");
                return 0;
            }
        } catch (SQLException e) {
            log.error("Error estimating row count: {}", e.getMessage(), e);
            throw new DataExecutionException("Error estimating row count: " + e.getMessage(), e);
        } finally {
            closeResources(rs, stmt);
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("Error closing connection: {}", e.getMessage(), e);
            }
        }
    }

    private String buildCountQuery(String sql) {
        // 简单的COUNT查询构造逻辑，实际项目中可能需要更复杂的SQL解析
        String upperSql = sql.toUpperCase();

        // 移除ORDER BY子句，因为它对COUNT查询没有影响，但可能会影响性能
        int orderByIndex = upperSql.lastIndexOf("ORDER BY");
        String sqlWithoutOrderBy = orderByIndex > 0 ? sql.substring(0, orderByIndex) : sql;

        // 移除LIMIT和OFFSET子句，确保COUNT查询计算所有行
        int limitIndex = upperSql.lastIndexOf("LIMIT");
        if (limitIndex > 0) {
            sqlWithoutOrderBy = sqlWithoutOrderBy.substring(0, limitIndex);
        }

        // 构建COUNT查询
        return "SELECT COUNT(*) FROM (" + sqlWithoutOrderBy + ") AS count_query";
    }

    /**
     * 根据数据库类型构建COUNT查询
     *
     * @param sql    原始SQL
     * @param dbType 数据库类型
     * @return COUNT查询SQL
     */
    private String buildCountQueryForDbType(String sql, String dbType) {
        // 移除ORDER BY子句
        String upperSql = sql.toUpperCase();
        int orderByIndex = upperSql.lastIndexOf("ORDER BY");
        String sqlWithoutOrderBy = orderByIndex > 0 ? sql.substring(0, orderByIndex) : sql;

        // 根据数据库类型构建COUNT查询
        switch (dbType.toUpperCase()) {
            case "ORACLE":
                // Oracle可能需要特殊处理
                return "SELECT COUNT(*) FROM (" + sqlWithoutOrderBy + ")";

            case "SQLSERVER":
                // SQL Server可能需要特殊处理
                return "SELECT COUNT(*) FROM (" + sqlWithoutOrderBy + ") AS count_query";

            case "DB2":
                // DB2可能需要特殊处理
                return "SELECT COUNT(*) FROM (" + sqlWithoutOrderBy + ") AS count_query";

            default:
                // 默认语法适用于MySQL、PostgreSQL等
                return "SELECT COUNT(*) FROM (" + sqlWithoutOrderBy + ") AS count_query";
        }
    }

    /**
     * 构建排序SQL查询
     *
     * @param sql        原始SQL
     * @param parameters 查询参数
     * @return 排序SQL
     */
    private String buildSortQuery(String sql, Map<String, Object> parameters) {
        // 检查是否包含排序参数
        if (!parameters.containsKey("_sort_fields")) {
            return sql;
        }

        // 获取排序参数
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> sortFields = (List<Map<String, Object>>) parameters.get("_sort_fields");
        if (sortFields == null || sortFields.isEmpty()) {
            return sql;
        }

        // 构建ORDER BY子句
        StringBuilder orderByClause = new StringBuilder();
        orderByClause.append(" ORDER BY ");

        for (int i = 0; i < sortFields.size(); i++) {
            Map<String, Object> sortField = sortFields.get(i);
            String fieldName = (String) sortField.get("fieldName");
            String direction = (String) sortField.get("direction");

            if (i > 0) {
                orderByClause.append(", ");
            }

            // 防止SQL注入，简单验证字段名
            if (!fieldName.matches("[a-zA-Z0-9_\\.]+")) {
                throw new IllegalArgumentException("Invalid field name: " + fieldName);
            }

            orderByClause.append(fieldName);

            // 排序方向
            if ("DESC".equalsIgnoreCase(direction)) {
                orderByClause.append(" DESC");
            } else {
                orderByClause.append(" ASC");
            }
        }

        // 检查SQL是否已经包含ORDER BY子句
        String upperSql = sql.toUpperCase();
        if (upperSql.contains("ORDER BY")) {
            // 如果已经包含ORDER BY子句，则替换它
            int orderByIndex = upperSql.lastIndexOf("ORDER BY");
            return sql.substring(0, orderByIndex) + orderByClause.toString();
        } else {
            // 如果不包含ORDER BY子句，则添加它
            return sql + orderByClause.toString();
        }
    }

    /**
     * 构建分页SQL查询
     *
     * @param sql        原始SQL
     * @param parameters 查询参数
     * @return 分页SQL
     */
    private String buildPagedQuery(String sql, Map<String, Object> parameters) {
        // 检查是否包含分页参数
        if (!parameters.containsKey("_offset") || !parameters.containsKey("_limit")) {
            return sql;
        }

        // 获取分页参数
        int offset = (int) parameters.get("_offset");
        int limit = (int) parameters.get("_limit");

        // 获取数据源类型
        String dbType = "MYSQL"; // 默认使用MySQL语法
        if (parameters.containsKey("_db_type")) {
            dbType = (String) parameters.get("_db_type");
        }

        // 根据数据库类型构建分页SQL
        switch (dbType.toUpperCase()) {
            case "MYSQL":
                return sql + " LIMIT " + limit + " OFFSET " + offset;

            case "POSTGRESQL":
                return sql + " LIMIT " + limit + " OFFSET " + offset;

            case "DB2":
                // DB2分页语法
                return "SELECT * FROM (" +
                    "SELECT INNER_QUERY.*, ROW_NUMBER() OVER() AS ROW_NUM FROM (" +
                    sql +
                    ") AS INNER_QUERY" +
                    ") AS PAGED_QUERY WHERE ROW_NUM BETWEEN " + (offset + 1) + " AND " + (offset + limit);

            case "ORACLE":
                // Oracle 12c+分页语法
                return sql + " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

            case "SQLSERVER":
                // SQL Server 2012+分页语法
                if (!sql.toUpperCase().contains("ORDER BY")) {
                    // SQL Server需要ORDER BY子句
                    sql += " ORDER BY (SELECT NULL)";
                }
                return sql + " OFFSET " + offset + " ROWS FETCH NEXT " + limit + " ROWS ONLY";

            case "HIVE":
                // Hive分页语法
                return sql + " LIMIT " + limit + " OFFSET " + offset;

            case "CLICKHOUSE":
                // ClickHouse分页语法
                return sql + " LIMIT " + offset + ", " + limit;

            default:
                // 默认使用MySQL语法
                log.warn("Unknown database type: {}. Using MySQL syntax for pagination.", dbType);
                return sql + " LIMIT " + limit + " OFFSET " + offset;
        }
    }
}


