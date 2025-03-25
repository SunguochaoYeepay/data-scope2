package com.datascope.domain.query.service;

import com.datascope.domain.query.entity.UserDisplayConfig;
import com.datascope.domain.query.repository.UserDisplayConfigRepository;
import com.datascope.domain.query.service.impl.UserDisplayConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDisplayConfigServiceTest {

    @Mock
    private UserDisplayConfigRepository repository;

    @InjectMocks
    private UserDisplayConfigServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        // Arrange
        UserDisplayConfig config = new UserDisplayConfig();
        config.setUserId("testUser");
        config.setDataSourceId("testDataSource");
        when(repository.save(any(UserDisplayConfig.class))).thenAnswer(i -> {
            UserDisplayConfig saved = (UserDisplayConfig) i.getArguments()[0];
            if (saved.getId() == null) {
                saved.setId(UUID.randomUUID().toString());
            }
            return saved;
        });

        // Act
        UserDisplayConfig result = service.create(config, "testOperator");

        // Assert
        assertNotNull(result.getId());
        assertEquals("testOperator", result.getCreatedBy());
        assertEquals("testOperator", result.getModifiedBy());
        assertNotNull(result.getCreatedTime());
        assertNotNull(result.getModifiedTime());
        verify(repository).save(any(UserDisplayConfig.class));
    }

    @Test
    void testUpdate() {
        // Arrange
        UserDisplayConfig config = new UserDisplayConfig();
        config.setId(UUID.randomUUID().toString());
        config.setUserId("testUser");
        when(repository.save(any(UserDisplayConfig.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        UserDisplayConfig result = service.update(config, "testOperator");

        // Assert
        assertEquals("testOperator", result.getModifiedBy());
        assertNotNull(result.getModifiedTime());
        verify(repository).save(any(UserDisplayConfig.class));
    }

    @Test
    void testFindById() {
        // Arrange
        String id = UUID.randomUUID().toString();
        UserDisplayConfig config = new UserDisplayConfig();
        config.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(config));

        // Act
        Optional<UserDisplayConfig> result = service.findById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(id, result.get().getId());
    }

    @Test
    void testUpdateUsageStatistics() {
        // Arrange
        UserDisplayConfig config = new UserDisplayConfig();
        config.setId(UUID.randomUUID().toString());
        config.setUsageCount(0);
        when(repository.save(any(UserDisplayConfig.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        service.updateUsageStatistics(Collections.singletonList(config));

        // Assert
        verify(repository).save(any(UserDisplayConfig.class));
        assertEquals(1, config.getUsageCount());
        assertNotNull(config.getLastUsedTime());
    }
}
