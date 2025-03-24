CREATE TABLE IF NOT EXISTS user_display_config
(
    id
    VARCHAR
(
    255
) PRIMARY KEY,
    user_id VARCHAR
(
    255
),
    data_source_id VARCHAR
(
    255
),
    table_name VARCHAR
(
    255
),
    column_name VARCHAR
(
    255
),
    display_name VARCHAR
(
    255
),
    width INT,
    align VARCHAR
(
    255
),
    column_fixed VARCHAR
(
    255
),
    visible BOOLEAN,
    order_num INT,
    sortable BOOLEAN,
    searchable BOOLEAN,
    required BOOLEAN,
    mask_type VARCHAR
(
    255
),
    mask_config VARCHAR
(
    255
),
    usage_count INT,
    last_used_at TIMESTAMP,
    created_by VARCHAR
(
    255
),
    created_time TIMESTAMP,
    updated_by VARCHAR
(
    255
),
    updated_time TIMESTAMP
    );
