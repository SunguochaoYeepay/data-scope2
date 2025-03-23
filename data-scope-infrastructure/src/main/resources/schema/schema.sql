-- User display configuration table
CREATE TABLE IF NOT EXISTS tbl_user_display_config (
    -- Primary key
    id VARCHAR(36) NOT NULL COMMENT 'Primary key (UUID)',
    
    -- Business fields
    user_id VARCHAR(36) NOT NULL COMMENT 'User ID',
    data_source_id VARCHAR(36) NOT NULL COMMENT 'Data source ID',
    table_name VARCHAR(64) NOT NULL COMMENT 'Table name',
    column_name VARCHAR(64) NOT NULL COMMENT 'Column name',
    display_name VARCHAR(64) COMMENT 'Display name',
    width INT COMMENT 'Column width',
    align VARCHAR(16) COMMENT 'Column alignment (LEFT/CENTER/RIGHT)',
    fixed VARCHAR(16) COMMENT 'Column fixed position (NONE/LEFT/RIGHT)',
    visible BOOLEAN DEFAULT TRUE COMMENT 'Whether the column is visible',
    order_num INT DEFAULT 0 COMMENT 'Column order number',
    sortable BOOLEAN DEFAULT FALSE COMMENT 'Whether the column is sortable',
    searchable BOOLEAN DEFAULT FALSE COMMENT 'Whether the column is searchable',
    required BOOLEAN DEFAULT FALSE COMMENT 'Whether the column is required',
    mask_type VARCHAR(16) DEFAULT 'NONE' COMMENT 'Column mask type',
    mask_config VARCHAR(255) COMMENT 'Column mask configuration',
    usage_count BIGINT DEFAULT 0 COMMENT 'Usage count',
    last_used_at DATETIME COMMENT 'Last used time',
    
    -- Common fields
    nonce INT DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Creation time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modification time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    
    -- Primary key constraint
    PRIMARY KEY (id),
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_data_source_id (data_source_id),
    INDEX idx_table_name (table_name),
    INDEX idx_usage_count (usage_count),
    INDEX idx_last_used_at (last_used_at),
    UNIQUE INDEX u_idx_user_config (user_id, data_source_id, table_name, column_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User display configuration';