package com.datascope.domain.datasource.service.impl;

import com.datascope.domain.common.enums.SyncStatus;
import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.domain.datasource.enums.DataSourceStatus;
import com.datascope.domain.datasource.enums.DataSourceType;
import com.datascope.domain.datasource.exception.DataSourceException;
import com.datascope.domain.datasource.gateway.DataSourceConnectionGateway;
import com.datascope.domain.datasource.gateway.PasswordEncryptorGateway;
import com.datascope.domain.datasource.repository.DataSourceRepository;
import com.datascope.domain.datasource.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 数据源服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository repository;
    private final PasswordEncryptorGateway passwordEncryptorGateway;
    private final DataSourceConnectionGateway dataSourceConnectionGateway;

    @Override
    @Transactional
    public DataSource create(DataSource entity, String operator) {
        validateDataSource(entity);

        if (repository.existsByName(entity.getName())) {
            throw DataSourceException.nameExists(entity.getName());
        }

        // 生成盐值并加密密码
        String salt = passwordEncryptorGateway.generateSalt();
        String encryptedPassword = passwordEncryptorGateway.encrypt(entity.getPassword(), salt);

        entity.setSalt(salt);
        entity.setPassword(encryptedPassword);
        entity.init(operator);

        return repository.save(entity);
    }

    @Override
    @Transactional
    public DataSource update(DataSource entity, String operator) {
        Assert.notNull(entity.getId(), "数据源ID不能为空");
        validateDataSource(entity);

        DataSource existing = repository.findById(entity.getId())
            .orElseThrow(() -> DataSourceException.notFound(entity.getId()));

        if (!existing.getName().equals(entity.getName())
            && repository.existsByName(entity.getName())) {
            throw DataSourceException.nameExists(entity.getName());
        }

        // 如果密码发生变化，重新加密
        if (!existing.getPassword().equals(entity.getPassword())) {
            String encryptedPassword = passwordEncryptorGateway.encrypt(entity.getPassword(), existing.getSalt());
            entity.setPassword(encryptedPassword);
        }

        // 关闭旧的连接池
        dataSourceConnectionGateway.closeDataSource(entity.getId());

        entity.update(operator);
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public DataSource getById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> DataSourceException.notFound(id));
    }

    @Override
    @Transactional(readOnly = true)
    public DataSource getByName(String name) {
        DataSource dataSource = repository.findByName(name);
        if (dataSource == null) {
            throw DataSourceException.notFound("name=" + name);
        }
        return dataSource;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public void delete(String id, String operator) {
        // 关闭连接池
        dataSourceConnectionGateway.closeDataSource(id);
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public DataSource activate(String id, String operator) {
        DataSource entity = getById(id);
        entity.activate(operator);
        return repository.save(entity);
    }

    @Override
    @Transactional
    public DataSource deactivate(String id, String operator) {
        DataSource entity = getById(id);
        entity.deactivate(operator);
        return repository.save(entity);
    }

    @Override
    public boolean testConnection(String id) {
        DataSource entity = getById(id);
        try {
            return dataSourceConnectionGateway.testConnection(entity);
        } catch (Exception e) {
            log.error("测试数据源连接失败: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public DataSource syncMetadata(String id, String operator) {
        DataSource entity = getById(id);
        try {
            // TODO: 实现元数据同步逻辑
            entity.updateSyncStatus(SyncStatus.SUCCESS, "同步成功");
            return repository.save(entity);
        } catch (Exception e) {
            log.error("同步数据源元数据失败: {}", id, e);
            entity.updateSyncStatus(SyncStatus.FAILED, e.getMessage());
            return repository.save(entity);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getByType(DataSourceType type) {
        return repository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getByStatus(DataSourceStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkNameExists(String name) {
        return repository.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> searchByName(String nameLike) {
        return repository.findByNameLike(nameLike);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DataSource> getByTypeAndStatus(DataSourceType type, DataSourceStatus status) {
        return repository.findByTypeAndStatus(type, status);
    }

    /**
     * 验证数据源信息
     *
     * @param entity 数据源实体
     */
    private void validateDataSource(DataSource entity) {
        Assert.notNull(entity.getType(), "数据源类型不能为空");
        Assert.hasText(entity.getName(), "数据源名称不能为空");
        Assert.hasText(entity.getHost(), "主机地址不能为空");
        Assert.notNull(entity.getPort(), "端口号不能为空");
        Assert.hasText(entity.getDatabase(), "数据库名称不能为空");
        Assert.hasText(entity.getUsername(), "用户名不能为空");
    }
}
