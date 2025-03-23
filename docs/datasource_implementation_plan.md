# 数据源管理功能实施计划

## 概述

数据源管理是DataScope系统的核心功能之一，它允许用户添加、编辑、删除和管理不同类型的数据库连接。本文档详细说明了数据源管理功能的实施计划。

## 当前状态

通过代码分析，我们发现数据源管理功能已经有了基本的框架，包括：

1. 领域模型：`DataSource`实体类及相关枚举
2. 仓储接口：`DataSourceRepository`及其实现类
3. 服务接口：`DataSourceService`及其实现类
4. 异常处理：`DataSourceException`

但仍有一些关键功能需要完成：

1. 数据源连接测试功能
2. 密码加密和解密功能
3. 数据源连接池管理
4. 元数据同步功能

## 实施计划

### 1. 密码加密和解密功能

创建密码加密工具类，使用AES加密算法和盐值对数据源密码进行加密和解密。

```java
// PasswordEncryptor.java
package com.datascope.infrastructure.security.encryption;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */
@Component
public class PasswordEncryptor {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String SECRET_KEY_ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATION_COUNT = 65536;
    private static final int KEY_LENGTH = 256;
    private static final String MASTER_KEY = "DataScope-Secret-Key"; // 应从配置中读取

    /**
     * 生成随机盐值
     *
     * @return Base64编码的盐值
     */
    public String generateSalt() {
        byte[] salt = new byte[16];
        new SecureRandom().nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     *
     * @param password 原始密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public String encrypt(String password, String salt) {
        try {
            byte[] saltBytes = Base64.getDecoder().decode(salt);
            byte[] iv = new byte[16];
            new SecureRandom().nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(MASTER_KEY.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);
            byte[] encryptedBytes = cipher.doFinal(password.getBytes(StandardCharsets.UTF_8));

            // 将IV和加密后的密码合并
            byte[] combined = new byte[iv.length + encryptedBytes.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(encryptedBytes, 0, combined, iv.length, encryptedBytes.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码
     * @param salt              盐值
     * @return 原始密码
     */
    public String decrypt(String encryptedPassword, String salt) {
        try {
            byte[] combined = Base64.getDecoder().decode(encryptedPassword);
            byte[] saltBytes = Base64.getDecoder().decode(salt);

            // 提取IV
            byte[] iv = new byte[16];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            // 提取加密后的密码
            byte[] encryptedBytes = new byte[combined.length - iv.length];
            System.arraycopy(combined, iv.length, encryptedBytes, 0, encryptedBytes.length);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(SECRET_KEY_ALGORITHM);
            PBEKeySpec spec = new PBEKeySpec(MASTER_KEY.toCharArray(), saltBytes, ITERATION_COUNT, KEY_LENGTH);
            SecretKey secretKey = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("密码解密失败", e);
        }
    }
}
```

### 2. 数据源连接池管理

创建数据源连接池管理器，负责创建、管理和关闭数据库连接池。

```java
// DataSourceConnectionManager.java
package com.datascope.infrastructure.external.datasource;

import com.datascope.domain.datasource.entity.DataSource;
import com.datascope.infrastructure.security.encryption.PasswordEncryptor;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据源连接池管理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSourceConnectionManager {

    private final PasswordEncryptor passwordEncryptor;
    private final Map<String, HikariDataSource> dataSources = new ConcurrentHashMap<>();

    /**
     * 获取数据库连接
     *
     * @param dataSource 数据源
     * @return 数据库连接
     * @throws SQLException 连接异常
     */
    public Connection getConnection(DataSource dataSource) throws SQLException {
        HikariDataSource hikariDataSource = getOrCreateDataSource(dataSource);
        return hikariDataSource.getConnection();
    }

    /**
     * 测试数据库连接
     *
     * @param dataSource 数据源
     * @return 是否连接成功
     */
    public boolean testConnection(DataSource dataSource) {
        try {
            HikariDataSource hikariDataSource = createDataSource(dataSource);
            try (Connection connection = hikariDataSource.getConnection()) {
                return connection.isValid(5);
            } finally {
                hikariDataSource.close();
            }
        } catch (Exception e) {
            log.error("测试数据源连接失败: {}", dataSource.getId(), e);
            return false;
        }
    }

    /**
     * 关闭数据源连接池
     *
     * @param dataSourceId 数据源ID
     */
    public void closeDataSource(String dataSourceId) {
        HikariDataSource hikariDataSource = dataSources.remove(dataSourceId);
        if (hikariDataSource != null && !hikariDataSource.isClosed()) {
            hikariDataSource.close();
        }
    }

    /**
     * 关闭所有数据源连接池
     */
    public void closeAllDataSources() {
        dataSources.forEach((id, dataSource) -> {
            if (!dataSource.isClosed()) {
                dataSource.close();
            }
        });
        dataSources.clear();
    }

    /**
     * 获取或创建数据源连接池
     *
     * @param dataSource 数据源
     * @return 数据源连接池
     */
    private HikariDataSource getOrCreateDataSource(DataSource dataSource) {
        return dataSources.computeIfAbsent(dataSource.getId(), id -> createDataSource(dataSource));
    }

    /**
     * 创建数据源连接池
     *
     * @param dataSource 数据源
     * @return 数据源连接池
     */
    private HikariDataSource createDataSource(DataSource dataSource) {
        HikariConfig config = new HikariConfig();
        
        String jdbcUrl = buildJdbcUrl(dataSource);
        String decryptedPassword = passwordEncryptor.decrypt(dataSource.getPassword(), dataSource.getSalt());
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(dataSource.getUsername());
        config.setPassword(decryptedPassword);
        
        // 连接池配置
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);
        config.setPoolName("HikariPool-" + dataSource.getName());
        
        // 根据数据源类型设置驱动类
        switch (dataSource.getType()) {
            case MYSQL:
                config.setDriverClassName("com.mysql.cj.jdbc.Driver");
                break;
            case DB2:
                config.setDriverClassName("com.ibm.db2.jcc.DB2Driver");
                break;
            default:
                throw new IllegalArgumentException("不支持的数据源类型: " + dataSource.getType());
        }
        
        return new HikariDataSource(config);
    }

    /**
     * 构建JDBC URL
     *
     * @param dataSource 数据源
     * @return JDBC URL
     */
    private String buildJdbcUrl(DataSource dataSource) {
        switch (dataSource.getType()) {
            case MYSQL:
                return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&characterEncoding=utf8",
                        dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
            case DB2:
                return String.format("jdbc:db2://%s:%d/%s",
                        dataSource.getHost(), dataSource.getPort(), dataSource.getDatabase());
            default:
                throw new IllegalArgumentException("不支持的数据源类型: " + dataSource.getType());
        }
    }
}
```

### 3. 完善数据源服务实现

修改`DataSourceServiceImpl`类，实现连接测试和密码加密功能。

```java
// DataSourceServiceImpl.java 修改部分
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceRepository repository;
    private final PasswordEncryptor passwordEncryptor;
    private final DataSourceConnectionManager connectionManager;

    @Override
    @Transactional
    public DataSource create(DataSource entity, String operator) {
        validateDataSource(entity);
        
        if (repository.existsByName(entity.getName())) {
            throw DataSourceException.nameExists(entity.getName());
        }

        // 生成盐值并加密密码
        String salt = passwordEncryptor.generateSalt();
        String encryptedPassword = passwordEncryptor.encrypt(entity.getPassword(), salt);
        
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
            String encryptedPassword = passwordEncryptor.encrypt(entity.getPassword(), existing.getSalt());
            entity.setPassword(encryptedPassword);
        }
        
        // 关闭旧的连接池
        connectionManager.closeDataSource(entity.getId());
        
        entity.update(operator);
        return repository.save(entity);
    }

    @Override
    public boolean testConnection(String id) {
        DataSource entity = getById(id);
        try {
            return connectionManager.testConnection(entity);
        } catch (Exception e) {
            log.error("测试数据源连接失败: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional
    public void delete(String id, String operator) {
        // 关闭连接池
        connectionManager.closeDataSource(id);
        repository.deleteById(id);
    }
}
```

### 4. 修复数据源仓储实现类

修改`DataSourceRepositoryImpl`类，解决与Mapper接口方法名不匹配的问题。

```java
// DataSourceRepositoryImpl.java 修改部分
@Repository
@RequiredArgsConstructor
public class DataSourceRepositoryImpl implements DataSourceRepository {
    private final DataSourceMapper mapper;

    @Override
    public DataSource findByName(String name) {
        return mapper.selectByName(name);
    }

    @Override
    public List<DataSource> findByType(DataSource.DataSourceType type) {
        return mapper.selectByType(type);
    }

    @Override
    public List<DataSource> findByStatus(DataSource.DataSourceStatus status) {
        return mapper.selectByStatus(status);
    }

    @Override
    public List<DataSource> findByLastSyncStatus(DataSource.SyncStatus syncStatus) {
        return mapper.selectByLastSyncStatus(syncStatus);
    }

    @Override
    public List<DataSource> findByNameLike(String nameLike) {
        return mapper.selectByNameLike(nameLike);
    }

    @Override
    public List<DataSource> findByHost(String host) {
        return mapper.selectByHost(host);
    }

    @Override
    public List<DataSource> findByDatabase(String database) {
        return mapper.selectByDatabase(database);
    }

    @Override
    public List<DataSource> findByTypeAndStatus(DataSource.DataSourceType type, DataSource.DataSourceStatus status) {
        return mapper.selectByTypeAndStatus(type, status);
    }

    @Override
    public List<DataSource> findAllById(List<String> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        mapper.deleteByIds(ids);
    }

    @Override
    public List<DataSource> saveAll(List<DataSource> entities) {
        for (DataSource entity : entities) {
            save(entity);
        }
        return entities;
    }
}
```

### 5. 创建MyBatis XML映射文件

创建`DataSourceMapper.xml`文件，实现SQL映射。

```xml
<!-- DataSourceMapper.xml -->
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.datascope.infrastructure.mybatis.mapper.DataSourceMapper">
    
    <resultMap id="dataSourceResultMap" type="com.datascope.domain.datasource.entity.DataSource">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="type" column="type"/>
        <result property="host" column="host"/>
        <result property="port" column="port"/>
        <result property="database" column="database_name"/>
        <result property="username" column="username"/>
        <result property="password" column="password_encrypted"/>
        <result property="salt" column="salt"/>
        <result property="status" column="status"/>
        <result property="lastSyncAt" column="last_sync_at"/>
        <result property="lastSyncStatus" column="last_sync_status"/>
        <result property="lastSyncMessage" column="last_sync_message"/>
        <result property="remark" column="remark"/>
        <result property="nonce" column="nonce"/>
        <result property="createdAt" column="created_at"/>
        <result property="createdBy" column="created_by"/>
        <result property="modifiedAt" column="modified_at"/>
        <result property="modifiedBy" column="modified_by"/>
    </resultMap>
    
    <sql id="baseColumns">
        id, name, type, host, port, database_name, username, password_encrypted, salt,
        status, last_sync_at, last_sync_status, last_sync_message, remark,
        nonce, created_at, created_by, modified_at, modified_by
    </sql>
    
    <insert id="insert" parameterType="com.datascope.domain.datasource.entity.DataSource">
        INSERT INTO tbl_data_source (
            id, name, type, host, port, database_name, username, password_encrypted, salt,
            status, last_sync_at, last_sync_status, last_sync_message, remark,
            nonce, created_at, created_by, modified_at, modified_by
        ) VALUES (
            #{id}, #{name}, #{type}, #{host}, #{port}, #{database}, #{username}, #{password}, #{salt},
            #{status}, #{lastSyncAt}, #{lastSyncStatus}, #{lastSyncMessage}, #{remark},
            #{nonce}, #{createdAt}, #{createdBy}, #{modifiedAt}, #{modifiedBy}
        )
    </insert>
    
    <update id="update" parameterType="com.datascope.domain.datasource.entity.DataSource">
        UPDATE tbl_data_source
        SET name = #{name},
            type = #{type},
            host = #{host},
            port = #{port},
            database_name = #{database},
            username = #{username},
            password_encrypted = #{password},
            salt = #{salt},
            status = #{status},
            last_sync_at = #{lastSyncAt},
            last_sync_status = #{lastSyncStatus},
            last_sync_message = #{lastSyncMessage},
            remark = #{remark},
            nonce = #{nonce},
            modified_at = #{modifiedAt},
            modified_by = #{modifiedBy}
        WHERE id = #{id}
    </update>
    
    <select id="selectById" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE id = #{id}
    </select>
    
    <select id="selectByIds" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>
    
    <select id="selectAll" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
    </select>
    
    <select id="selectByName" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE name = #{name}
    </select>
    
    <select id="existsByName" resultType="boolean">
        SELECT COUNT(1) > 0
        FROM tbl_data_source
        WHERE name = #{name}
    </select>
    
    <select id="selectByType" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE type = #{type}
    </select>
    
    <select id="selectByStatus" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE status = #{status}
    </select>
    
    <select id="selectByLastSyncStatus" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE last_sync_status = #{syncStatus}
    </select>
    
    <select id="selectByNameLike" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE name LIKE CONCAT('%', #{nameLike}, '%')
    </select>
    
    <select id="selectByHost" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE host = #{host}
    </select>
    
    <select id="selectByDatabase" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE database_name = #{database}
    </select>
    
    <select id="selectByTypeAndStatus" resultMap="dataSourceResultMap">
        SELECT <include refid="baseColumns"/>
        FROM tbl_data_source
        WHERE type = #{type} AND status = #{status}
    </select>
    
    <delete id="deleteById">
        DELETE FROM tbl_data_source
        WHERE id = #{id}
    </delete>
    
    <delete id="deleteByIds">
        DELETE FROM tbl_data_source
        WHERE id IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>
    
    <delete id="deleteAll">
        DELETE FROM tbl_data_source
    </delete>
    
    <select id="existsById" resultType="boolean">
        SELECT COUNT(1) > 0
        FROM tbl_data_source
        WHERE id = #{id}
    </select>
    
    <select id="count" resultType="long">
        SELECT COUNT(1)
        FROM tbl_data_source
    </select>
    
    <select id="countByType" resultType="long">
        SELECT COUNT(1)
        FROM tbl_data_source
        WHERE type = #{type}
    </select>
    
    <select id="countByStatus" resultType="long">
        SELECT COUNT(1)
        FROM tbl_data_source
        WHERE status = #{status}
    </select>
</mapper>
```

## 测试计划

1. 单元测试
  - 测试密码加密和解密功能
  - 测试数据源服务的CRUD操作
  - 测试数据源连接测试功能

2. 集成测试
  - 测试数据源连接池管理
  - 测试数据源仓储与数据库的交互

## 下一步工作

完成数据源管理功能后，下一步将实施元数据提取功能，包括：

1. 设计元数据模型（表、列、索引等）
2. 实现元数据提取逻辑
3. 实现元数据存储和查询
4. 实现元数据同步机制
