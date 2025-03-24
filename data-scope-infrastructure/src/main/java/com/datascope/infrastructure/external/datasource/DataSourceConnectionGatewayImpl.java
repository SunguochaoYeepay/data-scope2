package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.gateway.DataSourceConnectionGateway;
import com.datascope.domain.datasource.gateway.PasswordEncryptorGateway;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class DataSourceConnectionGatewayImpl implements DataSourceConnectionGateway {

    @Setter(onMethod_ = @Autowired)
    private PasswordEncryptorGateway passwordEncryptor;

    private Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    @Override
    public Connection getConnection(DataSource dataSource) throws SQLException {
        HikariDataSource hikariDataSource = getOrCreateDataSource(dataSource);
        return hikariDataSource.getConnection();
    }

    @Override
    public boolean testConnection(DataSource dataSource) {
        try {
            HikariDataSource hikariDataSource = createDataSource(dataSource);
            try {
                Connection connection = hikariDataSource.getConnection();
                boolean isValid = connection.isValid(5);
                connection.close();
                return isValid;
            } catch (SQLException e) {
                log.error("测试数据源连接失败: {}, 错误信息: {}", dataSource.getName(), e.getMessage());
                return false;
            } finally {
                hikariDataSource.close();
            }
        } catch (Exception e) {
            log.error("创建数据源连接池失败: {}, 错误信息: {}", dataSource.getName(), e.getMessage());
            return false;
        }
    }

    @Override
    public void closeDataSource(String dataSourceId) {
        HikariDataSource hikariDataSource = dataSources.remove(dataSourceId);
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

    @Override
    public void closeAllDataSources() {
        dataSources.forEach((id, dataSource) -> {
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
        });
        dataSources.clear();
    }

    private HikariDataSource getOrCreateDataSource(DataSource dataSource) {
        return dataSources.computeIfAbsent(dataSource.getId(), id -> createDataSource(dataSource));
    }

    private HikariDataSource createDataSource(DataSource dataSource) {
        HikariConfig config = new HikariConfig();

        String jdbcUrl = buildJdbcUrl(dataSource);
        String password = dataSource.getPassword();

        // 如果是已保存的数据源，需要解密密码
        if (dataSource.getSalt() != null && !dataSource.getSalt().isEmpty()) {
            password = passwordEncryptor.decrypt(dataSource.getPassword(), dataSource.getSalt());
        }

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dataSource.getUsername());
        config.setPassword(password);

        // 连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(5000); // 减少连接超时时间，加快测试响应
        config.setMaxLifetime(1800000);
        config.setPoolName("HikariPool-" + dataSource.getName());

        // 设置连接验证超时时间
        config.setValidationTimeout(3000);

        // 根据数据源类型设置驱动类
        switch (dataSource.getType()) {
            case MYSQL:
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case DB2:
                config.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
                break;
            default:
                throw new IllegalArgumentException("不支持的数据源类型: " + dataSource.getType());
        }

        return new HikariDataSource(config);
    }

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
}
