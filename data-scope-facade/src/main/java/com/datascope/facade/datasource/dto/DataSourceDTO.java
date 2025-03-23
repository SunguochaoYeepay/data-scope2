package com.datascope.facade.datasource.dto;

import com.datascope.domain.datasource.entity.DataSource;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 数据源DTO
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataSourceDTO {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空")
    @Size(max = 50, message = "数据源名称长度不能超过50个字符")
    private String name;

    /**
     * 数据源类型
     */
    @NotNull(message = "数据源类型不能为空")
    private DataSource.DataSourceType type;

    /**
     * 主机地址
     */
    @NotBlank(message = "主机地址不能为空")
    @Size(max = 100, message = "主机地址长度不能超过100个字符")
    private String host;

    /**
     * 端口号
     */
    @NotNull(message = "端口号不能为空")
    private Integer port;

    /**
     * 数据库名称
     */
    @NotBlank(message = "数据库名称不能为空")
    @Size(max = 50, message = "数据库名称长度不能超过50个字符")
    private String database;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 100, message = "密码长度不能超过100个字符")
    private String password;

    /**
     * 数据源状态
     */
    private DataSource.DataSourceStatus status;

    /**
     * 最后同步时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSyncAt;

    /**
     * 最后同步状态
     */
    private DataSource.SyncStatus lastSyncStatus;

    /**
     * 最后同步消息
     */
    private String lastSyncMessage;

    /**
     * 备注
     */
    @Size(max = 200, message = "备注长度不能超过200个字符")
    private String remark;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 最后修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;

    /**
     * 最后修改人
     */
    private String modifiedBy;
}