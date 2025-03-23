package com.datascope.app.controller;

import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User display configuration controller
 *
 * @author dreambt
 */
@Tag(name = "User Display Configuration", description = "User display configuration management API")
@RestController
@RequestMapping("/api/v1/display-configs")
@RequiredArgsConstructor
public class UserDisplayConfigController {
    private final UserDisplayConfigFacade facade;

    @Operation(summary = "Create configuration")
    @PostMapping
    public ResponseEntity<UserDisplayConfigDTO> create(
            @Parameter(description = "Configuration to create", required = true)
            @Validated @RequestBody UserDisplayConfigDTO dto) {
        return ResponseEntity.ok(facade.create(dto, "system"));
    }

    @Operation(summary = "Update configuration")
    @PutMapping("/{id}")
    public ResponseEntity<UserDisplayConfigDTO> update(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id,
            @Parameter(description = "Configuration to update", required = true)
            @Validated @RequestBody UserDisplayConfigDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok(facade.update(dto, "system"));
    }

    @Operation(summary = "Get configuration by ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserDisplayConfigDTO> getById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        UserDisplayConfigDTO dto = facade.findById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @Operation(summary = "Get all configurations")
    @GetMapping
    public ResponseEntity<List<UserDisplayConfigDTO>> getAll() {
        return ResponseEntity.ok(facade.findByUserId(null));
    }

    @Operation(summary = "Delete configuration by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        facade.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Find configurations by user ID")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<UserDisplayConfigDTO>> findByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        return ResponseEntity.ok(facade.findByUserId(userId));
    }

    @Operation(summary = "Find configurations by user ID and data source ID")
    @GetMapping("/by-user/{userId}/by-datasource/{dataSourceId}")
    public ResponseEntity<List<UserDisplayConfigDTO>> findByUserIdAndDataSourceId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Data source ID", required = true)
            @PathVariable String dataSourceId) {
        return ResponseEntity.ok(facade.findByUserIdAndDataSourceId(userId, dataSourceId));
    }

    @Operation(summary = "Find configurations by user ID, data source ID and table name")
    @GetMapping("/by-user/{userId}/by-datasource/{dataSourceId}/by-table/{tableName}")
    public ResponseEntity<List<UserDisplayConfigDTO>> findByUserIdAndDataSourceIdAndTableName(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Data source ID", required = true)
            @PathVariable String dataSourceId,
            @Parameter(description = "Table name", required = true)
            @PathVariable String tableName) {
        return ResponseEntity.ok(
                facade.findByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName));
    }

    @Operation(summary = "Find configurations by user ID, data source ID, table name and column name")
    @GetMapping("/by-user/{userId}/by-datasource/{dataSourceId}/by-table/{tableName}/by-column/{columnName}")
    public ResponseEntity<List<UserDisplayConfigDTO>> findByUserIdAndDataSourceIdAndTableNameAndColumnName(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Data source ID", required = true)
            @PathVariable String dataSourceId,
            @Parameter(description = "Table name", required = true)
            @PathVariable String tableName,
            @Parameter(description = "Column name", required = true)
            @PathVariable String columnName) {
        return ResponseEntity.ok(
                facade.findByUserIdAndDataSourceIdAndTableNameAndColumnName(
                        userId, dataSourceId, tableName, columnName));
    }

    @Operation(summary = "Delete configurations by user ID")
    @DeleteMapping("/by-user/{userId}")
    public ResponseEntity<Void> deleteByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId) {
        facade.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete configurations by user ID and data source ID")
    @DeleteMapping("/by-user/{userId}/by-datasource/{dataSourceId}")
    public ResponseEntity<Void> deleteByUserIdAndDataSourceId(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Data source ID", required = true)
            @PathVariable String dataSourceId) {
        facade.deleteByUserIdAndDataSourceId(userId, dataSourceId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Delete configurations by user ID, data source ID and table name")
    @DeleteMapping("/by-user/{userId}/by-datasource/{dataSourceId}/by-table/{tableName}")
    public ResponseEntity<Void> deleteByUserIdAndDataSourceIdAndTableName(
            @Parameter(description = "User ID", required = true)
            @PathVariable String userId,
            @Parameter(description = "Data source ID", required = true)
            @PathVariable String dataSourceId,
            @Parameter(description = "Table name", required = true)
            @PathVariable String tableName) {
        facade.deleteByUserIdAndDataSourceIdAndTableName(userId, dataSourceId, tableName);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Copy configurations from one user to another")
    @PostMapping("/copy")
    public ResponseEntity<Void> copyConfigurations(
            @Parameter(description = "Source user ID", required = true)
            @RequestParam String fromUserId,
            @Parameter(description = "Target user ID", required = true)
            @RequestParam String toUserId) {
        facade.copyConfigurations(fromUserId, toUserId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update usage statistics")
    @PostMapping("/{id}/usage")
    public ResponseEntity<Void> updateUsage(
            @Parameter(description = "Configuration ID", required = true)
            @PathVariable String id) {
        facade.incrementUsageCount(id);
        return ResponseEntity.ok().build();
    }
}
