package com.datascope.app.controller;

import com.datascope.app.response.ApiResponse;
import com.datascope.facade.datasource.DataSourceFacade;
import com.datascope.facade.datasource.dto.DataSourceDTO;
import com.datascope.facade.datasource.dto.TestConnectionRequest;
import com.datascope.facade.datasource.enums.DataSourceStatus;
import com.datascope.facade.datasource.enums.DataSourceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 数据源管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/datasources")
public class DataSourceController {

    @Autowired
    private DataSourceFacade dataSourceFacade;

    /**
     * 获取所有数据源
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<DataSourceDTO>>> getAllDataSources() {
        log.info("Getting all data sources");
        List<DataSourceDTO> dataSources = dataSourceFacade.getAll();
        return ResponseEntity.ok(ApiResponse.success(dataSources));
    }

    /**
     * 根据ID获取数据源
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DataSourceDTO>> getDataSourceById(@PathVariable String id) {
        log.info("Getting data source by id: {}", id);
        DataSourceDTO dataSource = dataSourceFacade.getById(id);
        if (dataSource == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Data source not found with id: " + id));
        }
        return ResponseEntity.ok(ApiResponse.success(dataSource));
    }

    /**
     * 创建数据源
     */
    @PostMapping
    public ResponseEntity<ApiResponse<DataSourceDTO>> createDataSource(
        @RequestBody DataSourceDTO dataSourceDTO,
        @RequestHeader("X-User-Id") String userId) {
        log.info("Creating data source: {}", dataSourceDTO.getName());
        DataSourceDTO createdDataSource = dataSourceFacade.create(dataSourceDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(createdDataSource));
    }

    /**
     * 更新数据源
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DataSourceDTO>> updateDataSource(
            @PathVariable String id,
            @RequestBody DataSourceDTO dataSourceDTO,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Updating data source with id: {}", id);
        dataSourceDTO.setId(id);
        DataSourceDTO updatedDataSource = dataSourceFacade.update(dataSourceDTO, userId);
        return ResponseEntity.ok(ApiResponse.success(updatedDataSource));
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDataSource(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Deleting data source with id: {}", id);
        dataSourceFacade.delete(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 激活数据源
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<DataSourceDTO>> activateDataSource(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Activating data source with id: {}", id);
        DataSourceDTO activatedDataSource = dataSourceFacade.activate(id, userId);
        return ResponseEntity.ok(ApiResponse.success(activatedDataSource));
    }

    /**
     * 停用数据源
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<DataSourceDTO>> deactivateDataSource(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Deactivating data source with id: {}", id);
        DataSourceDTO deactivatedDataSource = dataSourceFacade.deactivate(id, userId);
        return ResponseEntity.ok(ApiResponse.success(deactivatedDataSource));
    }

    /**
     * 测试数据源连接
     */
    @PostMapping("/test-connection")
    public ResponseEntity<ApiResponse<Boolean>> testConnection(
        @RequestBody TestConnectionRequest request) {
        log.info("Testing connection for data source: {}", request.getName());
        boolean success = dataSourceFacade.testConnection(request);
        return ResponseEntity.ok(ApiResponse.success(success));
    }

    /**
     * 同步数据源元数据
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<ApiResponse<DataSourceDTO>> syncMetadata(
            @PathVariable String id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("Syncing metadata for data source with id: {}", id);
        DataSourceDTO syncedDataSource = dataSourceFacade.syncMetadata(id, userId);
        return ResponseEntity.ok(ApiResponse.success(syncedDataSource));
    }

    /**
     * 按名称搜索数据源
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<DataSourceDTO>>> searchDataSources(
        @RequestParam String name) {
        log.info("Searching data sources with name: {}", name);
        List<DataSourceDTO> dataSources = dataSourceFacade.searchByName(name);
        return ResponseEntity.ok(ApiResponse.success(dataSources));
    }

    /**
     * 按类型筛选数据源
     */
    @GetMapping("/filter/type/{type}")
    public ResponseEntity<ApiResponse<List<DataSourceDTO>>> filterByType(
        @PathVariable DataSourceType type) {
        log.info("Filtering data sources by type: {}", type);
        List<DataSourceDTO> dataSources = dataSourceFacade.getByType(type);
        return ResponseEntity.ok(ApiResponse.success(dataSources));
    }

    /**
     * 按状态筛选数据源
     */
    @GetMapping("/filter/status/{status}")
    public ResponseEntity<ApiResponse<List<DataSourceDTO>>> filterByStatus(
        @PathVariable DataSourceStatus status) {
        log.info("Filtering data sources by status: {}", status);
        List<DataSourceDTO> dataSources = dataSourceFacade.getByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(dataSources));
    }

    /**
     * 按类型和状态筛选数据源
     */
    @GetMapping("/filter/type/{type}/status/{status}")
    public ResponseEntity<ApiResponse<List<DataSourceDTO>>> filterByTypeAndStatus(
        @PathVariable DataSourceType type,
        @PathVariable DataSourceStatus status) {
        log.info("Filtering data sources by type: {} and status: {}", type, status);
        List<DataSourceDTO> dataSources = dataSourceFacade.getByTypeAndStatus(type, status);
        return ResponseEntity.ok(ApiResponse.success(dataSources));
    }
}
