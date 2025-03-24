CREATE TABLE user_display_config
(
    id             VARCHAR(36)  NOT NULL,
    user_id        VARCHAR(36)  NOT NULL,
    data_source_id VARCHAR(36)  NOT NULL,
    table_name     VARCHAR(255) NOT NULL,
    column_name    VARCHAR(255) NOT NULL,
    display_name   VARCHAR(255),
    width          INTEGER,
    align          VARCHAR(10),
    column_fixed   VARCHAR(10),
    visible        BOOLEAN DEFAULT TRUE,
    order_num      INTEGER,
    sortable       BOOLEAN DEFAULT TRUE,
    searchable     BOOLEAN DEFAULT TRUE,
    required       BOOLEAN DEFAULT FALSE,
    mask_type      VARCHAR(20),
    mask_config    TEXT,
    usage_count    INTEGER DEFAULT 0,
    last_used_at   TIMESTAMP,
    created_by     VARCHAR(36)  NOT NULL,
    created_time   TIMESTAMP    NOT NULL,
    updated_by     VARCHAR(36)  NOT NULL,
    updated_time   TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes
CREATE INDEX idx_user_display_config_user_id ON user_display_config (user_id);
CREATE INDEX idx_user_display_config_data_source ON user_display_config (user_id, data_source_id);
CREATE INDEX idx_user_display_config_table ON user_display_config (user_id, data_source_id, table_name);
CREATE INDEX idx_user_display_config_column ON user_display_config (user_id, data_source_id, table_name, column_name);