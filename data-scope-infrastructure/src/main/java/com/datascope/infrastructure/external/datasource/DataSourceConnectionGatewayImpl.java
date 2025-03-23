package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.gateway.DataSourceConnectionGateway;
import com.datascope.domain.datasource.gateway.PasswordEncryptorGateway;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceConnectionGatewayImpl implements DataSourceConnectionGateway {
    private final PasswordEncryptorGateway passwordEncryptor;
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    @Override
    public Connection getConnection(DataSource dataSource) throws SQLException {
        HikariDataSource hikariDataSource = getOrCreateDataSource(dataSource);
        return hikariDataSource.getConnection();
    }

    @Override
    public boolean testConnection(DataSource dataSource) {
        try {
            HikariDataSource hikariDataSource = createDataSource(dataSource);
            try (Connection connection = hikariDataSource.getConnection()) {
                return connection.isValid(5);
            } finally {
                hikariDataSource.close();
            }
        } catch (SQLException e) {
            log.error("测试数据源连接失败: {}", dataSource.getId(), e);
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
        String decryptedPassword = passwordEncryptor.decrypt(dataSource.getPassword(), dataSource.getSalt());

        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dataSource.getUsername());
        config.setPassword(decryptedPassword);

        // 连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setPoolName("HikariPool-" + dataSource.getName());

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
