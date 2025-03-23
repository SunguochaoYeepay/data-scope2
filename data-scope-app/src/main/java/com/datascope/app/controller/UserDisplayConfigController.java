package com.datascope.app.controller;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.datascope.app.controller.request.UserDisplayConfigRequest;
import com.datascope.app.controller.response.UserDisplayConfigResponse;
import com.datascope.app.mapper.UserDisplayConfigMapper;
import com.datascope.facade.query.UserDisplayConfigFacade;
import com.datascope.facade.query.dto.UserDisplayConfigDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户显示配置Controller
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/display-configs")
@Tag(name = "用户显示配置", description = "用户显示配置相关接口")
public class UserDisplayConfigController {

    private final UserDisplayConfigFacade facade;
    private final UserDisplayConfigMapper mapper;

    @PostMapping
    @Operation(summary = "保存配置")
    public UserDisplayConfigResponse saveConfig(@RequestBody @Valid UserDisplayConfigRequest request) {
        UserDisplayConfigDTO dto = mapper.toDTO(request);
        dto = facade.saveConfig(dto);
        return mapper.toResponse(dto);
    }

    @PostMapping("/batch")
    @Operation(summary = "批量保存配置")
    public List<UserDisplayConfigResponse> saveConfigs(@RequestBody @Valid List<UserDisplayConfigRequest> requests) {
        List<UserDisplayConfigDTO> dtos = mapper.toDTOs(requests);
        dtos = facade.saveConfigs(dtos);
        return mapper.toResponses(dtos);
    }

    @GetMapping("/tables/{tableName}")
    @Operation(summary = "获取表的配置")
    public List<UserDisplayConfigResponse> getTableConfigs(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "数据源ID", required = true) @RequestParam String dataSourceId,
            @Parameter(description = "表名", required = true) @PathVariable String tableName) {
        List<UserDisplayConfigDTO> dtos = facade.getTableConfigs(userId, dataSourceId, tableName);
        return mapper.toResponses(dtos);
    }

    @GetMapping("/data-sources/{dataSourceId}")
    @Operation(summary = "获取数据源的配置")
    public List<UserDisplayConfigResponse> getDataSourceConfigs(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "数据源ID", required = true) @PathVariable String dataSourceId) {
        List<UserDisplayConfigDTO> dtos = facade.getDataSourceConfigs(userId, dataSourceId);
        return mapper.toResponses(dtos);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除配置")
    public void deleteConfig(@Parameter(description = "配置ID", required = true) @PathVariable String id) {
        facade.deleteConfig(id);
    }

    @DeleteMapping("/tables/{tableName}")
    @Operation(summary = "删除表的配置")
    public void deleteTableConfigs(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "数据源ID", required = true) @RequestParam String dataSourceId,
            @Parameter(description = "表名", required = true) @PathVariable String tableName) {
        facade.deleteTableConfigs(userId, dataSourceId, tableName);
    }

    @PostMapping("/{id}/usage")
    @Operation(summary = "记录配置使用")
    public void recordConfigUsage(@Parameter(description = "配置ID", required = true) @PathVariable String id) {
        facade.recordConfigUsage(id);
    }

    @GetMapping("/most-used")
    @Operation(summary = "获取最常用的配置")
    public List<UserDisplayConfigResponse> getMostUsedConfigs(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "数据源ID", required = true) @RequestParam String dataSourceId,
            @Parameter(description = "限制数量", required = true) @RequestParam int limit) {
        List<UserDisplayConfigDTO> dtos = facade.getMostUsedConfigs(userId, dataSourceId, limit);
        return mapper.toResponses(dtos);
    }

    @GetMapping("/recommend")
    @Operation(summary = "推荐配置")
    public List<UserDisplayConfigResponse> recommendConfigs(
            @Parameter(description = "用户ID", required = true) @RequestParam String userId,
            @Parameter(description = "数据源ID", required = true) @RequestParam String dataSourceId,
            @Parameter(description = "表名", required = true) @RequestParam String tableName) {
        List<UserDisplayConfigDTO> dtos = facade.recommendConfigs(userId, dataSourceId, tableName);
        return mapper.toResponses(dtos);
    }

    @PostMapping("/copy")
    @Operation(summary = "从其他用户复制配置")
    public List<UserDisplayConfigResponse> copyConfigsFromUser(
            @Parameter(description = "源用户ID", required = true) @RequestParam String fromUserId,
            @Parameter(description = "目标用户ID", required = true) @RequestParam String toUserId,
            @Parameter(description = "数据源ID", required = true) @RequestParam String dataSourceId,
            @Parameter(description = "表名", required = true) @RequestParam String tableName) {
        List<UserDisplayConfigDTO> dtos = facade.copyConfigsFromUser(fromUserId, toUserId, dataSourceId, tableName);
        return mapper.toResponses(dtos);
    }
}