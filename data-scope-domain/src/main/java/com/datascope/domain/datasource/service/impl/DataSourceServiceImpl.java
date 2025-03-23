package com.datascope.domain.datasource.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.domain.datasource.service.DataSourceService;
import com.datascope.domain.datasource.valueobject.DataSourceStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据源领域服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    @Override
    @Transactional
    public DataSource createDataSource(DataSource dataSource) {
        // 验证数据源配置
        List<String> validationErrors = validateDataSource(dataSource);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", validationErrors));
        }

        // 检查名称是否已存在
        if (dataSourceRepository.existsByName(dataSource.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSource.getName());
        }

        // 设置初始状态
        dataSource.setStatus(DataSourceStatus.INACTIVE);
        dataSource.setCreatedAt(LocalDateTime.now());
        dataSource.setModifiedAt(LocalDateTime.now());

        // 保存数据源
        return dataSourceRepository.save(dataSource);
    }

    @Override
    @Transactional
    public DataSource updateDataSource(DataSource dataSource) {
        // 验证数据源配置
        List<String> validationErrors = validateDataSource(dataSource);
        if (!validationErrors.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", validationErrors));
        }

        // 检查数据源是否存在
        DataSource existingDataSource = dataSourceRepository.findById(dataSource.getId())
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在: " + dataSource.getId()));

        // 检查名称是否已被其他数据源使用
        if (!existingDataSource.getName().equals(dataSource.getName()) &&
                dataSourceRepository.existsByName(dataSource.getName())) {
            throw new IllegalArgumentException("数据源名称已存在: " + dataSource.getName());
        }

        // 更新修改时间
        dataSource.setModifiedAt(LocalDateTime.now());
        
        // 保存更新
        return dataSourceRepository.save(dataSource);
    }

    @Override
    @Transactional
    public void deleteDataSource(String id) {
        // 检查数据源是否存在
        if (!dataSourceRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("数据源不存在: " + id);
        }

        // 删除数据源
        dataSourceRepository.deleteById(id);
    }

    @Override
    public Optional<DataSource> getDataSource(String id) {
        return dataSourceRepository.findById(id);
    }

    @Override
    public List<DataSource> getAllDataSources() {
        return dataSourceRepository.findAll();
    }

    @Override
    public boolean testConnection(String id) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在: " + id));
        
        boolean success = dataSource.testConnection();
        
        // 更新连接状态
        if (success) {
            dataSource.updateStatus(DataSourceStatus.ACTIVE);
        } else {
            dataSource.updateStatus(DataSourceStatus.ERROR);
        }
        dataSourceRepository.save(dataSource);
        
        return success;
    }

    @Override
    @Transactional
    public boolean syncMetadata(String id) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在: " + id));

        try {
            // 设置同步状态
            dataSource.updateStatus(DataSourceStatus.SYNCING);
            dataSourceRepository.save(dataSource);

            // TODO: 实现元数据同步逻辑

            // 更新同步状态
            dataSource.updateStatus(DataSourceStatus.ACTIVE);
            dataSourceRepository.save(dataSource);
            
            return true;
        } catch (Exception e) {
            log.error("同步元数据失败: " + id, e);
            dataSource.updateStatus(DataSourceStatus.ERROR);
            dataSourceRepository.save(dataSource);
            return false;
        }
    }

    @Override
    public List<DataSource> getNeedSyncDataSources(int syncIntervalMinutes) {
        return dataSourceRepository.findNeedSync(syncIntervalMinutes);
    }

    @Override
    @Transactional
    public DataSource updateDataSourceStatus(String id, DataSourceStatus status) {
        DataSource dataSource = dataSourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("数据源不存在: " + id));
        
        dataSource.updateStatus(status);
        return dataSourceRepository.save(dataSource);
    }

    @Override
    public List<String> validateDataSource(DataSource dataSource) {
        List<String> errors = new ArrayList<>();

        // 验证必填字段
        if (StringUtils.isBlank(dataSource.getName())) {
            errors.add("数据源名称不能为空");
        }
        if (dataSource.getType() == null) {
            errors.add("数据源类型不能为空");
        }
        if (StringUtils.isBlank(dataSource.getHost())) {
            errors.add("数据库主机不能为空");
        }
        if (dataSource.getPort() == null || dataSource.getPort() <= 0) {
            errors.add("数据库端口无效");
        }
        if (StringUtils.isBlank(dataSource.getDatabaseName())) {
            errors.add("数据库名称不能为空");
        }
        if (StringUtils.isBlank(dataSource.getUsername())) {
            errors.add("数据库用户名不能为空");
        }
        if (StringUtils.isBlank(dataSource.getPasswordEncrypted())) {
            errors.add("数据库密码不能为空");
        }
        if (StringUtils.isBlank(dataSource.getPasswordSalt())) {
            errors.add("密码盐值不能为空");
        }

        return errors;
    }
}