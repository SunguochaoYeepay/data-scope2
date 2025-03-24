-- 用户显示配置表
CREATE TABLE IF NOT EXISTS user_display_config
(
    id
    VARCHAR
(
    36
) PRIMARY KEY,
    user_id VARCHAR
(
    36
) NOT NULL,
    data_source_id VARCHAR
(
    36
) NOT NULL,
    table_name VARCHAR
(
    100
) NOT NULL,
    column_name VARCHAR
(
    100
) NOT NULL,
    display_name VARCHAR
(
    100
),
    width INT,
    fixed_position VARCHAR
(
    10
), -- 使用 fixed_position 代替 fixed 关键字
    align_type VARCHAR
(
    10
), -- 使用 align_type 代替 align 关键字
    visible BOOLEAN DEFAULT TRUE,
    display_order INT, -- 使用 display_order 代替 order 关键字
    searchable BOOLEAN DEFAULT FALSE,
    required BOOLEAN DEFAULT FALSE,
    mask_type VARCHAR
(
    20
),
    mask_config TEXT,
    sortable BOOLEAN DEFAULT FALSE,
    sort_type VARCHAR
(
    10
),
    usage_count INT DEFAULT 0,
    last_used_at TIMESTAMP,
    created_by VARCHAR
(
    36
),
    created_time TIMESTAMP,
    updated_by VARCHAR
(
    36
),
    updated_time TIMESTAMP
    );

-- 查询执行记录表
CREATE TABLE IF NOT EXISTS query_execution
(
    id
    VARCHAR
(
    36
) PRIMARY KEY,
    data_source_id VARCHAR
(
    36
) NOT NULL,
    sql TEXT NOT NULL,
    status VARCHAR
(
    20
) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    result_count BIGINT,
    error_message TEXT,
    user_id VARCHAR
(
    36
),
    parameters TEXT -- 存储JSON格式的参数
    );

-- 索引
CREATE INDEX IF NOT EXISTS idx_user_display_config_user_id ON user_display_config(user_id);
CREATE INDEX IF NOT EXISTS idx_user_display_config_data_source ON user_display_config(data_source_id);
CREATE INDEX IF NOT EXISTS idx_query_execution_user_id ON query_execution(user_id);
CREATE INDEX IF NOT EXISTS idx_query_execution_data_source ON query_execution(data_source_id);
CREATE INDEX IF NOT EXISTS idx_query_execution_status ON query_execution(status);
