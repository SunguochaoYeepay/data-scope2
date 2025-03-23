package com.datascope.infrastructure.repository;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.infrastructure.mybatis.mapper.UserDisplayConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDisplayConfigRepositoryImplTest {

    @Mock
    private UserDisplayConfigMapper mapper;

    private UserDisplayConfigRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        repository = new UserDisplayConfigRepositoryImpl();
        repository.setMybatisMapper(mapper);
    }

    @Test
    void testSave_Insert() {
        // Arrange
        UserDisplayConfig config = new UserDisplayConfig();
        config.setUserId("testUser");

        // Act
        UserDisplayConfig result = repository.save(config);

        // Assert
        verify(mapper).insert(config);
        assertEquals(config, result);
    }

    @Test
    void testSave_Update() {
        // Arrange
        UserDisplayConfig config = new UserDisplayConfig();
        config.setId(UUID.randomUUID().toString());
        config.setUserId("testUser");

        // Act
        UserDisplayConfig result = repository.save(config);

        // Assert
        verify(mapper).update(config);
        assertEquals(config, result);
    }

    @Test
    void testFindById() {
        // Arrange
        String id = UUID.randomUUID().toString();
        UserDisplayConfig config = new UserDisplayConfig();
        config.setId(id);
        when(mapper.selectById(id)).thenReturn(config);

        // Act
        Optional<UserDisplayConfig> result = repository.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testFindByUserId() {
        // Arrange
        String userId = "testUser";
        UserDisplayConfig config = new UserDisplayConfig();
        config.setUserId(userId);
        when(mapper.selectByUserId(userId)).thenReturn(Arrays.asList(config));

        // Act
        List<UserDisplayConfig> results = repository.findByUserId(userId);

        // Assert
        assertFalse(results.isEmpty());
        assertEquals(userId, results.get(0).getUserId());
    }

    @Test
    void testDeleteByUserId() {
        // Arrange
        String userId = "testUser";

        // Act
        repository.deleteByUserId(userId);

        // Assert
        verify(mapper).deleteByUserId(userId);
    }

    @Test
    void testCopyConfigurations() {
        // Arrange
        String fromUserId = "fromUser";
        String toUserId = "toUser";
        UserDisplayConfig sourceConfig = new UserDisplayConfig();
        sourceConfig.setUserId(fromUserId);
        when(mapper.selectByUserId(fromUserId)).thenReturn(Arrays.asList(sourceConfig));

        // Act
        repository.copyConfigurations(fromUserId, toUserId);

        // Assert
        verify(mapper).selectByUserId(fromUserId);
        verify(mapper).insert(any(UserDisplayConfig.class));
    }
}
