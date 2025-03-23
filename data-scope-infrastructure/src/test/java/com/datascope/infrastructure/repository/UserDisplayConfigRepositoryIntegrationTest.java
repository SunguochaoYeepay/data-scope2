package com.datascope.infrastructure.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.enums.ColumnAlign;
import com.datascope.domain.query.enums.ColumnFixed;
import com.datascope.domain.query.enums.MaskType;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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
        return new UserDisplayConfig()
            .setId(UUID.randomUUID().toString())
            .setUserId(userId)
            .setDataSourceId(dataSourceId)
            .setTableName("test_table")
            .setColumnName("test_column")
            .setDisplayName("Test Column")
            .setWidth(100)
            .setAlign(ColumnAlign.LEFT)
            .setColumnFixed(ColumnFixed.NONE)
            .setVisible(true)
            .setOrderNum(1)
            .setSortable(true)
            .setSearchable(true)
            .setRequired(false)
            .setMaskType(MaskType.NONE)
            .setUsageCount(0)
            .setCreatedBy("test")
            .setCreatedTime(LocalDateTime.now())
            .setUpdatedBy("test")
            .setUpdatedTime(LocalDateTime.now());
    }
}
