CREATE TABLE user_display_config
(
    id             VARCHAR(36)  NOT NULL,
    user_id        VARCHAR(36)  NOT NULL,
    data_source_id VARCHAR(36)  NOT NULL,
    table_name     VARCHAR(100) NOT NULL,
    column_name    VARCHAR(100) NOT NULL,
    display_name   VARCHAR(100),
    width          INTEGER,
    align          VARCHAR(10),
    column_fixed   VARCHAR(10),
    visible        BOOLEAN DEFAULT true,
    order_num      INTEGER,
    sortable       BOOLEAN DEFAULT true,
    searchable     BOOLEAN DEFAULT true,
    required       BOOLEAN DEFAULT false,
    mask_type      VARCHAR(20),
    mask_config    VARCHAR(255),
    usage_count    INTEGER DEFAULT 0,
    last_used_at   TIMESTAMP,
    created_by     VARCHAR(36)  NOT NULL,
    created_time   TIMESTAMP    NOT NULL,
    updated_by     VARCHAR(36)  NOT NULL,
    updated_time   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes
CREATE INDEX idx_udc_user_id ON user_display_config (user_id);
CREATE INDEX idx_udc_data_source ON user_display_config (data_source_id);
CREATE INDEX idx_udc_table ON user_display_config (table_name);
CREATE INDEX idx_udc_column ON user_display_config (column_name);
CREATE INDEX idx_udc_usage ON user_display_config (usage_count);

-- Add unique constraint
ALTER TABLE user_display_config
    ADD CONSTRAINT uk_udc_user_ds_table_column
        UNIQUE (user_id, data_source_id, table_name, column_name);

-- Add comments
COMMENT
ON TABLE user_display_config IS '用户显示配置表';
COMMENT
ON COLUMN user_display_config.id IS '主键ID';
COMMENT
ON COLUMN user_display_config.user_id IS '用户ID';
COMMENT
ON COLUMN user_display_config.data_source_id IS '数据源ID';
COMMENT
ON COLUMN user_display_config.table_name IS '表名';
COMMENT
ON COLUMN user_display_config.column_name IS '列名';
COMMENT
ON COLUMN user_display_config.display_name IS '显示名称';
COMMENT
ON COLUMN user_display_config.width IS '列宽';
COMMENT
ON COLUMN user_display_config.align IS '对齐方式';
COMMENT
ON COLUMN user_display_config.column_fixed IS '列固定位置';
COMMENT
ON COLUMN user_display_config.visible IS '是否可见';
COMMENT
ON COLUMN user_display_config.order_num IS '排序号';
COMMENT
ON COLUMN user_display_config.sortable IS '是否可排序';
COMMENT
ON COLUMN user_display_config.searchable IS '是否可搜索';
COMMENT
ON COLUMN user_display_config.required IS '是否必填';
COMMENT
ON COLUMN user_display_config.mask_type IS '掩码类型';
COMMENT
ON COLUMN user_display_config.mask_config IS '掩码配置';
COMMENT
ON COLUMN user_display_config.usage_count IS '使用次数';
COMMENT
ON COLUMN user_display_config.last_used_at IS '最后使用时间';
COMMENT
ON COLUMN user_display_config.created_by IS '创建人';
COMMENT
ON COLUMN user_display_config.created_time IS '创建时间';
COMMENT
ON COLUMN user_display_config.updated_by IS '更新人';
COMMENT
ON COLUMN user_display_config.updated_time IS '更新时间';
