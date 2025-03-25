package com.datascope.main.config;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.UUID;

/**
 * H2数据源初始化器
 * 在应用启动时自动创建一个H2数据源
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile("test")
public class H2DataSourceInitializer {

    private final DataSourceService dataSourceService;
    private final Environment environment;

    @Bean
    public CommandLineRunner initializeH2DataSource() {
        return args -> {
            try {
                // 检查是否已存在H2数据源
                List<DataSource> h2DataSources = dataSourceService.getByType(DataSourceType.H2);
                if (h2DataSources.isEmpty()) {
                    log.info("正在初始化H2数据源...");

                    // 获取应用配置的H2数据库信息
                    String url = environment.getProperty("spring.datasource.url");
                    String username = environment.getProperty("spring.datasource.username");
                    String password = environment.getProperty("spring.datasource.password", "");

                    // 解析H2 JDBC URL
                    String host = "localhost";
                    int port = 9092; // H2默认TCP端口
                    String database = "datascope";

                    if (url != null && url.startsWith("jdbc:h2:")) {
                        if (url.contains("mem:")) {
                            // 内存模式
                            host = "localhost";
                            database = url.substring(url.indexOf("mem:") + 4);
                            if (database.contains(";")) {
                                database = database.substring(0, database.indexOf(";"));
                            }
                        } else if (url.contains("tcp:")) {
                            // TCP模式
                            String tcpUrl = url.substring(url.indexOf("tcp:") + 4);
                            if (tcpUrl.contains("//")) {
                                tcpUrl = tcpUrl.substring(tcpUrl.indexOf("//") + 2);
                                if (tcpUrl.contains("/")) {
                                    String hostPort = tcpUrl.substring(0, tcpUrl.indexOf("/"));
                                    database = tcpUrl.substring(tcpUrl.indexOf("/") + 1);
                                    if (database.contains(";")) {
                                        database = database.substring(0, database.indexOf(";"));
                                    }

                                    if (hostPort.contains(":")) {
                                        host = hostPort.substring(0, hostPort.indexOf(":"));
                                        port = Integer.parseInt(hostPort.substring(hostPort.indexOf(":") + 1));
                                    } else {
                                        host = hostPort;
                                    }
                                }
                            }
                        }
                    }

                    // 创建H2数据源
                    DataSource h2DataSource = new DataSource();
                    h2DataSource.setId(UUID.randomUUID().toString());
                    h2DataSource.setName("本地H2数据库");
                    h2DataSource.setType(DataSourceType.H2);
                    h2DataSource.setHost(host);
                    h2DataSource.setPort(port);
                    h2DataSource.setDatabase(database);
                    h2DataSource.setUsername(username);
                    h2DataSource.setPassword(password);
                    h2DataSource.setStatus(DataSourceStatus.ACTIVE);
                    h2DataSource.setLastSyncStatus(SyncStatus.NOT_SYNCED);

                    // 创建数据源
                    dataSourceService.create(h2DataSource, "system");
                    log.info("H2数据源初始化完成: {}", h2DataSource.getName());
                } else {
                    log.info("H2数据源已存在，跳过初始化");
                }
            } catch (Exception e) {
                log.error("初始化H2数据源失败", e);
            }
        };
    }
}
