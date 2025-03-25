package com.datascope.app.controller;

import com.datascope.app.controller.request.ExecuteQueryRequest;
import com.datascope.app.response.ApiResponse;
import com.datascope.domain.datasource.model.DataSourceId;
import com.datascope.domain.query.model.QueryResult;
import com.datascope.domain.query.service.QueryExecutionService;
import com.datascope.domain.query.service.SqlExecutionEngine;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/queries")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Tag(name = "查询管理", description = "查询执行和管理相关接口")
public class QueryController {

    private final SqlExecutionEngine sqlExecutionEngine;
    private final QueryExecutionService queryExecutionService;

    /**
     * 执行SQL查询
     *
     * @param request 查询请求
     * @return 查询结果
     */
    @PostMapping("/execute")
    @Operation(summary = "执行SQL查询", description = "执行SQL查询并返回结果")
    public ResponseEntity<ApiResponse<QueryResult>> executeQuery(
        @RequestBody ExecuteQueryRequest request) {
        log.info("执行SQL查询: {}", request);

        // 构建查询参数
        Map<String, Object> parameters = new HashMap<>();
        if (request.getParameters() != null) {
            parameters.putAll(request.getParameters());
        }

        // 添加分页和排序参数
        if (request.getPage() != null) {
            parameters.put("_page", request.getPage());
        }
        if (request.getSize() != null) {
            parameters.put("_size", request.getSize());
        }
        if (request.getSortColumn() != null && !request.getSortColumn().isEmpty()) {
            parameters.put("_sort", request.getSortColumn());
            parameters.put("_order", request.getSortDirection() != null ? request.getSortDirection() : "asc");
        }

        // 执行查询
        QueryResult result = sqlExecutionEngine.execute(
            new DataSourceId(request.getDataSourceId()),
            request.getSql(),
            parameters
        );

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 取消正在执行的查询
     *
     * @param executionId 查询执行ID
     * @return 操作结果
     */
    @PostMapping("/cancel/{executionId}")
    @Operation(summary = "取消查询", description = "取消正在执行的查询")
    public ResponseEntity<ApiResponse<Void>> cancelQuery(
        @Parameter(description = "查询执行ID", required = true)
        @PathVariable String executionId) {
        log.info("取消查询: {}", executionId);
        queryExecutionService.cancel(executionId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 验证SQL查询
     *
     * @param request 查询请求
     * @return 验证结果
     */
    @PostMapping("/validate")
    @Operation(summary = "验证SQL查询", description = "验证SQL查询的语法和结构")
    public ResponseEntity<ApiResponse<Boolean>> validateQuery(
        @RequestBody ExecuteQueryRequest request) {
        log.info("验证SQL查询: {}", request);
        boolean isValid = sqlExecutionEngine.validate(
            new DataSourceId(request.getDataSourceId()),
            request.getSql()
        );
        return ResponseEntity.ok(ApiResponse.success(isValid));
    }

    /**
     * 获取SQL查询的元数据
     *
     * @param request 查询请求
     * @return 查询元数据
     */
    @PostMapping("/metadata")
    @Operation(summary = "获取查询元数据", description = "获取SQL查询的元数据信息")
    public ResponseEntity<ApiResponse<Object>> getQueryMetadata(
        @RequestBody ExecuteQueryRequest request) {
        log.info("获取查询元数据: {}", request);
        Object metadata = sqlExecutionEngine.getMetadata(
            new DataSourceId(request.getDataSourceId()),
            request.getSql()
        );
        return ResponseEntity.ok(ApiResponse.success(metadata));
    }

    /**
     * 估算查询结果行数
     *
     * @param request 查询请求
     * @return 估算的行数
     */
    @PostMapping("/estimate")
    @Operation(summary = "估算查询结果行数", description = "估算SQL查询结果的行数")
    public ResponseEntity<ApiResponse<Long>> estimateRowCount(
        @RequestBody ExecuteQueryRequest request) {
        log.info("估算查询结果行数: {}", request);
        long rowCount = sqlExecutionEngine.estimateRowCount(
            new DataSourceId(request.getDataSourceId()),
            request.getSql()
        );
        return ResponseEntity.ok(ApiResponse.success(rowCount));
    }
}
