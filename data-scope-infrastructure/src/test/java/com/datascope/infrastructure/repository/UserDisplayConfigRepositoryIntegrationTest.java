package com.datascope.infrastructure.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.infrastructure.config.MyBatisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest(classes = {MyBatisConfig.class, DataSourceAutoConfiguration.class,
    UserDisplayConfigRepositoryImpl.class})
@Transactional
class UserDisplayConfigRepositoryIntegrationTest {

    @Autowired
    private UserDisplayConfigRepository userDisplayConfigRepository;

    @Test
    void testSaveAndFind() {
        // Arrange
        String userId = "testUser";
        String dataSourceId = "testDataSource";
        UserDisplayConfig config = createTestConfig(userId, dataSourceId);

        // Act
        UserDisplayConfig saved = userDisplayConfigRepository.save(config);
        UserDisplayConfig found = userDisplayConfigRepository.findById(saved.getId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(config.getUserId(), found.getUserId());
        assertEquals(config.getDataSourceId(), found.getDataSourceId());
        assertEquals(config.getTableName(), found.getTableName());
        assertEquals(config.getColumnName(), found.getColumnName());
    }

    @Test
    void testFindByUserId() {
        // Arrange
        String userId = "testUser";
        String dataSourceId = "testDataSource";
        UserDisplayConfig config1 = createTestConfig(userId, dataSourceId);
        UserDisplayConfig config2 = createTestConfig(userId, dataSourceId);
        userDisplayConfigRepository.save(config1);
        userDisplayConfigRepository.save(config2);

        // Act
        List<UserDisplayConfig> configs = userDisplayConfigRepository.findByUserId(userId);

        // Assert
        assertEquals(2, configs.size());
        assertTrue(configs.stream().allMatch(c -> c.getUserId().equals(userId)));
    }

    @Test
    void testCopyConfigurations() {
        // Arrange
        String fromUserId = "fromUser";
        String toUserId = "toUser";
        String dataSourceId = "testDataSource";
        UserDisplayConfig config = createTestConfig(fromUserId, dataSourceId);
        userDisplayConfigRepository.save(config);

        // Act
        userDisplayConfigRepository.copyConfigurations(fromUserId, toUserId);
        List<UserDisplayConfig> copiedConfigs = userDisplayConfigRepository.findByUserId(toUserId);

        // Assert
        assertFalse(copiedConfigs.isEmpty());
        UserDisplayConfig copied = copiedConfigs.get(0);
        assertEquals(toUserId, copied.getUserId());
        assertEquals(config.getDataSourceId(), copied.getDataSourceId());
        assertEquals(config.getTableName(), copied.getTableName());
        assertEquals(config.getColumnName(), copied.getColumnName());
        assertEquals(0, copied.getUsageCount());
    }

    private UserDisplayConfig createTestConfig(String userId, String dataSourceId) {
        return UserDisplayConfig.builder()
            .userId(userId)
            .dataSourceId(dataSourceId)
            .tableName("test_table")
            .columnName("test_column")
            .displayName("Test Column")
            .width(100)
            .align(ColumnAlign.LEFT)
            .fixed(ColumnFixed.NONE)
            .visible(true)
            .order(1)
            .sortable(true)
            .searchable(true)
            .required(false)
            .maskType(MaskType.NONE)
            .usageCount(0)
            .createdBy("test")
            .createdTime(LocalDateTime.now())
            .modifiedBy("test")
            .modifiedTime(LocalDateTime.now())
            .build();
    }
}
