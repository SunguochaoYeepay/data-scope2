package com.datascope.app.controller;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.facade.datasource.DataSourceFacade;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源控制器
 */
@Tag(name = "数据源管理", description = "数据源相关接口")
@RestController
@RequestMapping("/api/v1/datasources")
@RequiredArgsConstructor
public class DataSourceController {

    private final DataSourceFacade facade;

    @Operation(summary = "创建数据源")
    @PostMapping
    public ResponseEntity<DataSourceDTO> create(
            @RequestBody @Valid DataSourceDTO dto,
            @RequestHeader("X-User-Id") String operator) {
        return ResponseEntity.ok(facade.create(dto, operator));
    }

    @Operation(summary = "更新数据源")
    @PutMapping("/{id}")
    public ResponseEntity<DataSourceDTO> update(
            @PathVariable String id,
            @RequestBody @Valid DataSourceDTO dto,
            @RequestHeader("X-User-Id") String operator) {
        dto.setId(id);
        return ResponseEntity.ok(facade.update(dto, operator));
    }

    @Operation(summary = "获取数据源")
    @GetMapping("/{id}")
    public ResponseEntity<DataSourceDTO> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(facade.getById(id));
    }

    @Operation(summary = "获取所有数据源")
    @GetMapping
    public ResponseEntity<List<DataSourceDTO>> getAll(
            @Parameter(description = "数据源类型") @RequestParam(required = false) DataSource.DataSourceType type,
            @Parameter(description = "数据源状态") @RequestParam(required = false) DataSource.DataSourceStatus status) {
        if (type != null && status != null) {
            return ResponseEntity.ok(facade.getByTypeAndStatus(type, status));
        } else if (type != null) {
            return ResponseEntity.ok(facade.getByType(type));
        } else if (status != null) {
            return ResponseEntity.ok(facade.getByStatus(status));
        } else {
            return ResponseEntity.ok(facade.getAll());
        }
    }

    @Operation(summary = "删除数据源")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String operator) {
        facade.delete(id, operator);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "激活数据源")
    @PostMapping("/{id}/activate")
    public ResponseEntity<DataSourceDTO> activate(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String operator) {
        return ResponseEntity.ok(facade.activate(id, operator));
    }

    @Operation(summary = "停用数据源")
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<DataSourceDTO> deactivate(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String operator) {
        return ResponseEntity.ok(facade.deactivate(id, operator));
    }

    @Operation(summary = "测试数据源连接")
    @PostMapping("/{id}/test")
    public ResponseEntity<Boolean> testConnection(
            @PathVariable String id) {
        return ResponseEntity.ok(facade.testConnection(id));
    }

    @Operation(summary = "同步数据源元数据")
    @PostMapping("/{id}/sync")
    public ResponseEntity<DataSourceDTO> syncMetadata(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String operator) {
        return ResponseEntity.ok(facade.syncMetadata(id, operator));
    }

    @Operation(summary = "检查数据源名称是否存在")
    @GetMapping("/check-name")
    public ResponseEntity<Boolean> checkNameExists(
            @Parameter(description = "数据源名称") @RequestParam String name) {
        return ResponseEntity.ok(facade.checkNameExists(name));
    }

    @Operation(summary = "搜索数据源")
    @GetMapping("/search")
    public ResponseEntity<List<DataSourceDTO>> searchByName(
            @Parameter(description = "数据源名称（模糊匹配）") @RequestParam String name) {
        return ResponseEntity.ok(facade.searchByName(name));
    }
}