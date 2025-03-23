# 数据源连接测试功能实现计划

## 概述

数据源连接测试功能是数据源管理的重要组成部分，它允许用户在添加或修改数据源时验证连接信息的正确性。本文档详细说明了数据源连接测试功能的实现计划。

## 功能需求

1. 用户可以在添加新数据源前测试连接
2. 用户可以在修改数据源信息后测试连接
3. 系统可以定期测试所有活跃数据源的连接状态
4. 连接测试应该有超时机制，避免长时间阻塞
5. 连接测试结果应该包含详细的错误信息（如果有）

## 技术方案

### 1. 连接测试核心逻辑

连接测试的核心逻辑是创建一个临时的数据库连接，验证连接是否成功，然后关闭连接。为了避免创建完整的连接池，我们可以使用轻量级的连接测试方法。

```java
/**
 * 测试数据库连接
 *
 * @param dataSource 数据源
 * @return 连接测试结果
 */
public ConnectionTestResult testConnection(DataSource dataSource) {
    ConnectionTestResult result = new ConnectionTestResult();
    result.setDataSourceId(dataSource.getId());
    result.setStartTime(LocalDateTime.now());
    
    Connection connection = null;
    try {
        // 解密密码
        String decryptedPassword = passwordEncryptor.decrypt(dataSource.getPassword(), dataSource.getSalt());
        
        // 构建JDBC URL
        String jdbcUrl = buildJdbcUrl(dataSource);
        
        // 加载驱动
        loadDriver(dataSource.getType());
        
        // 设置连接超时
        Properties props = new Properties();
        props.setProperty("user", dataSource.getUsername());
        props.setProperty("password", decryptedPassword);
        props.setProperty("connectTimeout", "5000"); // 5秒连接超时
        
        // 获取连接
        connection = DriverManager.getConnection(jdbcUrl, props);
        
        // 验证连接
        boolean valid = connection.isValid(5); // 5秒验证超时
        
        result.setSuccess(valid);
        result.setEndTime(LocalDateTime.now());
        
        if (valid) {
            // 获取数据库版本信息
            DatabaseMetaData metaData = connection.getMetaData();
            result.setDatabaseProductName(metaData.getDatabaseProductName());
            result.setDatabaseProductVersion(metaData.getDatabaseProductVersion());
            result.setDriverName(metaData.getDriverName());
            result.setDriverVersion(metaData.getDriverVersion());
        }
        
        return result;
    } catch (SQLException e) {
        result.setSuccess(false);
        result.setErrorMessage(e.getMessage());
        result.setErrorCode(String.valueOf(e.getErrorCode()));
        result.setEndTime(LocalDateTime.now());
        return result;
    } finally {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // 忽略关闭连接时的异常
            }
        }
    }
}

/**
 * 加载数据库驱动
 *
 * @param type 数据源类型
 */
private void loadDriver(DataSource.DataSourceType type) throws SQLException {
    try {
        switch (type) {
            case MYSQL:
                Class.forName("com.mysql.cj.jdbc.Driver");
                break;
            case DB2:
                Class.forName("com.ibm.db2.jcc.DB2Driver");
                break;
            default:
                throw new SQLException("不支持的数据源类型: " + type);
        }
    } catch (ClassNotFoundException e) {
        throw new SQLException("数据库驱动加载失败: " + e.getMessage(), e);
    }
}

/**
 * 构建JDBC URL
 *
 * @param dataSource 数据源
 * @return JDBC URL
 */
private String buildJdbcUrl(DataSource dataSource) {
    switch (dataSource.getType()) {
        case MYSQL:
            return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&characterEncoding=utf8",
                    dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
        case DB2:
            return String.format("jdbc:db2://%s:%d/%s",
                    dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
        default:
            throw new IllegalArgumentException("不支持的数据源类型: " + dataSource.getType());
    }
}
```

### 2. 连接测试结果模型

创建一个模型类来表示连接测试的结果，包含成功/失败状态、错误信息、数据库版本等信息。

```java
/**
 * 连接测试结果
 */
@Data
public class ConnectionTestResult {
    /**
     * 数据源ID
     */
    private String dataSourceId;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误消息
     */
    private String errorMessage;
    
    /**
     * 错误码
     */
    private String errorCode;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 数据库产品名称
     */
    private String databaseProductName;
    
    /**
     * 数据库产品版本
     */
    private String databaseProductVersion;
    
    /**
     * 驱动名称
     */
    private String driverName;
    
    /**
     * 驱动版本
     */
    private String driverVersion;
    
    /**
     * 获取连接耗时（毫秒）
     */
    public long getDuration() {
        if (startTime != null && endTime != null) {
            return ChronoUnit.MILLIS.between(startTime, endTime);
        }
        return 0;
    }
}
```

### 3. 服务层集成

在`DataSourceService`接口中添加连接测试方法：

```java
/**
 * 测试数据源连接
 *
 * @param dataSource 数据源对象
 * @return 连接测试结果
 */
ConnectionTestResult testConnection(DataSource dataSource);

/**
 * 测试数据源连接
 *
 * @param id 数据源ID
 * @return 连接测试结果
 */
ConnectionTestResult testConnectionById(String id);
```

在`DataSourceServiceImpl`中实现这些方法：

```java
@Override
public ConnectionTestResult testConnection(DataSource dataSource) {
    return connectionManager.testConnection(dataSource);
}

@Override
public ConnectionTestResult testConnectionById(String id) {
    DataSource entity = getById(id);
    return testConnection(entity);
}
```

### 4. 控制器层集成

在`DataSourceController`中添加连接测试接口：

```java
/**
 * 测试数据源连接
 *
 * @param id 数据源ID
 * @return 连接测试结果
 */
@GetMapping("/{id}/test-connection")
public Result<ConnectionTestResultDTO> testConnection(@PathVariable String id) {
    ConnectionTestResult result = dataSourceService.testConnectionById(id);
    return Result.success(connectionTestResultMapper.toDTO(result));
}

/**
 * 测试数据源连接（未保存的数据源）
 *
 * @param request 数据源请求
 * @return 连接测试结果
 */
@PostMapping("/test-connection")
public Result<ConnectionTestResultDTO> testConnection(@RequestBody @Valid DataSourceRequest request) {
    DataSource dataSource = dataSourceMapper.toEntity(request);
    ConnectionTestResult result = dataSourceService.testConnection(dataSource);
    return Result.success(connectionTestResultMapper.toDTO(result));
}
```

### 5. 定期连接测试

创建一个定时任务，定期测试所有活跃数据源的连接状态：

```java
/**
 * 数据源连接状态监控任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceConnectionMonitor {

    private final DataSourceService dataSourceService;
    private final DataSourceConnectionManager connectionManager;
    
    /**
     * 每10分钟检查一次所有活跃数据源的连接状态
     */
    @Scheduled(fixedRate = 600000)
    public void monitorConnections() {
        log.info("开始检查数据源连接状态");
        
        List<DataSource> activeSources = dataSourceService.getByStatus(DataSource.DataSourceStatus.ACTIVE);
        
        for (DataSource dataSource : activeSources) {
            try {
                ConnectionTestResult result = connectionManager.testConnection(dataSource);
                
                if (!result.isSuccess()) {
                    log.warn("数据源[{}]连接失败: {}", dataSource.getName(), result.getErrorMessage());
                    // 可以在这里发送告警通知
                }
            } catch (Exception e) {
                log.error("测试数据源[{}]连接时发生异常", dataSource.getName(), e);
            }
        }
        
        log.info("数据源连接状态检查完成");
    }
}
```

## 前端集成

在数据源添加和编辑页面中，添加"测试连接"按钮，点击后调用后端接口进行连接测试，并显示测试结果。

```html
<!-- 测试连接按钮 -->
<button type="button" class="btn btn-info" @click="testConnection">
    <i class="fa fa-plug"></i> 测试连接
</button>

<!-- 测试结果弹窗 -->
<div class="modal fade" id="connectionTestModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">连接测试结果</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div v-if="testResult.success" class="alert alert-success">
                    <i class="fa fa-check-circle"></i> 连接成功
                </div>
                <div v-else class="alert alert-danger">
                    <i class="fa fa-times-circle"></i> 连接失败: {{ testResult.errorMessage }}
                </div>
                
                <div v-if="testResult.success" class="mt-3">
                    <h6>数据库信息</h6>
                    <table class="table table-sm">
                        <tr>
                            <th>数据库产品</th>
                            <td>{{ testResult.databaseProductName }}</td>
                        </tr>
                        <tr>
                            <th>数据库版本</th>
                            <td>{{ testResult.databaseProductVersion }}</td>
                        </tr>
                        <tr>
                            <th>驱动名称</th>
                            <td>{{ testResult.driverName }}</td>
                        </tr>
                        <tr>
                            <th>驱动版本</th>
                            <td>{{ testResult.driverVersion }}</td>
                        </tr>
                        <tr>
                            <th>连接耗时</th>
                            <td>{{ testResult.duration }}ms</td>
                        </tr>
                    </table>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-dismiss="modal">关闭</button>
            </div>
        </div>
    </div>
</div>
```

JavaScript代码：

```javascript
methods: {
    testConnection() {
        this.loading = true;
        
        // 如果是编辑模式，使用ID测试
        if (this.dataSource.id) {
            axios.get(`/api/datasources/${this.dataSource.id}/test-connection`)
                .then(response => {
                    this.testResult = response.data.data;
                    $('#connectionTestModal').modal('show');
                })
                .catch(error => {
                    this.$notify.error({
                        title: '测试失败',
                        message: error.response?.data?.message || '连接测试失败'
                    });
                })
                .finally(() => {
                    this.loading = false;
                });
        } else {
            // 如果是新增模式，使用表单数据测试
            axios.post('/api/datasources/test-connection', this.dataSource)
                .then(response => {
                    this.testResult = response.data.data;
                    $('#connectionTestModal').modal('show');
                })
                .catch(error => {
                    this.$notify.error({
                        title: '测试失败',
                        message: error.response?.data?.message || '连接测试失败'
                    });
                })
                .finally(() => {
                    this.loading = false;
                });
        }
    }
}
```

## 测试计划

1. 单元测试
  - 测试不同数据库类型的JDBC URL构建
  - 测试连接超时处理
  - 测试错误处理

2. 集成测试
  - 测试与真实数据库的连接
  - 测试错误情况（错误的主机、端口、用户名、密码等）
  - 测试连接池管理

## 安全考虑

1. 密码加密：确保数据源密码在传输和存储过程中都经过加密
2. 错误信息：在生产环境中，避免将详细的错误信息暴露给用户
3. 连接超时：设置合理的连接超时时间，避免长时间阻塞
4. 连接池管理：确保连接使用后正确关闭，避免连接泄漏

## 下一步工作

完成数据源连接测试功能后，下一步将实施：

1. 数据源元数据同步功能
2. 数据源连接池管理优化
3. 数据源监控和告警功能
