-- 用户显示配置表
CREATE TABLE IF NOT EXISTS tbl_user_display_config (
    -- 主键
    id VARCHAR(36) NOT NULL COMMENT '配置ID',
    -- 基础信息
    user_id VARCHAR(36) NOT NULL COMMENT '用户ID',
    data_source_id VARCHAR(36) NOT NULL COMMENT '数据源ID',
    table_name VARCHAR(64) NOT NULL COMMENT '表名',
    column_name VARCHAR(64) NOT NULL COMMENT '列名',
    -- 显示属性
    display_name VARCHAR(64) COMMENT '显示名称',
    width INT COMMENT '列宽度',
    align VARCHAR(16) COMMENT '列对齐方式',
    fixed VARCHAR(16) COMMENT '列固定位置',
    visible BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否可见',
    display_order INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
    -- 查询条件
    is_query_condition BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为查询条件',
    is_required BOOLEAN NOT NULL DEFAULT FALSE COMMENT '查询条件是否必填',
    default_value VARCHAR(255) COMMENT '查询条件默认值',
    is_advanced_condition BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否为高级查询条件',
    -- 数据掩码
    mask_type VARCHAR(32) COMMENT '数据掩码类型',
    mask_config TEXT COMMENT '掩码配置(JSON)',
    -- 使用统计
    usage_count BIGINT NOT NULL DEFAULT 0 COMMENT '使用次数',
    last_used_at BIGINT COMMENT '最后使用时间',
    -- 基础字段
    nonce INT NOT NULL DEFAULT 1 COMMENT '乐观锁版本号',
    created_at BIGINT NOT NULL COMMENT '创建时间',
    created_by VARCHAR(36) NOT NULL COMMENT '创建人',
    modified_at BIGINT NOT NULL COMMENT '最后修改时间',
    modified_by VARCHAR(36) NOT NULL COMMENT '最后修改人',
    -- 主键
    PRIMARY KEY (id),
    -- 索引
    INDEX idx_user_datasource (user_id, data_source_id),
    INDEX idx_table_name (table_name),
    INDEX idx_usage (usage_count, last_used_at),
    -- 唯一索引
    UNIQUE INDEX u_idx_user_config (user_id, data_source_id, table_name, column_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户显示配置表';