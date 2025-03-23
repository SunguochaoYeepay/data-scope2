-- 创建数据源表
CREATE TABLE IF NOT EXISTS tbl_data_source (
    -- 主键
    id VARCHAR(36) NOT NULL COMMENT '主键ID',
    
    -- 基本信息
    name VARCHAR(50) NOT NULL COMMENT '数据源名称',
    type VARCHAR(20) NOT NULL COMMENT '数据源类型',
    host VARCHAR(100) NOT NULL COMMENT '主机地址',
    port INT NOT NULL COMMENT '端口号',
    database_name VARCHAR(50) NOT NULL COMMENT '数据库名称',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(36) NOT NULL COMMENT '密码盐值',
    
    -- 状态信息
    status VARCHAR(20) NOT NULL COMMENT '数据源状态',
    last_sync_at DATETIME COMMENT '最后同步时间',
    last_sync_status VARCHAR(20) COMMENT '最后同步状态',
    last_sync_message TEXT COMMENT '最后同步消息',
    
    -- 其他信息
    remark VARCHAR(200) COMMENT '备注',
    
    -- 审计字段
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁',
    created_at DATETIME NOT NULL COMMENT '创建时间',
    created_by VARCHAR(36) NOT NULL COMMENT '创建人',
    modified_at DATETIME NOT NULL COMMENT '最后修改时间',
    modified_by VARCHAR(36) NOT NULL COMMENT '最后修改人',
    
    -- 约束
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_name (name),
    KEY idx_type (type),
    KEY idx_status (status),
    KEY idx_last_sync_status (last_sync_status),
    KEY idx_created_at (created_at),
    KEY idx_modified_at (modified_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据源表';