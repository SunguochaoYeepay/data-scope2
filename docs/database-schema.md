# DataScope Database Schema Design

## Data Source Management

### tbl_data_source
Data source configuration table
```sql
CREATE TABLE tbl_data_source (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    name VARCHAR(100) NOT NULL COMMENT 'Data source name',
    type VARCHAR(20) NOT NULL COMMENT 'Data source type: MYSQL, DB2',
    host VARCHAR(255) NOT NULL COMMENT 'Host address',
    port INT NOT NULL COMMENT 'Port number',
    database_name VARCHAR(100) NOT NULL COMMENT 'Database name',
    username VARCHAR(100) NOT NULL COMMENT 'Username',
    password VARCHAR(255) NOT NULL COMMENT 'Encrypted password',
    password_salt VARCHAR(36) NOT NULL COMMENT 'Password salt',
    status VARCHAR(20) NOT NULL COMMENT 'Status: ACTIVE, INACTIVE',
    description TEXT COMMENT 'Description',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Data source configuration';
```

### tbl_metadata_sync_history
Metadata synchronization history
```sql
CREATE TABLE tbl_metadata_sync_history (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    data_source_id VARCHAR(36) NOT NULL COMMENT 'Data source ID',
    sync_type VARCHAR(20) NOT NULL COMMENT 'Sync type: FULL, INCREMENTAL',
    status VARCHAR(20) NOT NULL COMMENT 'Status: SUCCESS, FAILED',
    start_time DATETIME NOT NULL COMMENT 'Start time',
    end_time DATETIME COMMENT 'End time',
    error_message TEXT COMMENT 'Error message',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    PRIMARY KEY (id),
    KEY idx_data_source_id (data_source_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Metadata sync history';
```

## Schema Management

### tbl_schema
Schema information
```sql
CREATE TABLE tbl_schema (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    data_source_id VARCHAR(36) NOT NULL COMMENT 'Data source ID',
    name VARCHAR(100) NOT NULL COMMENT 'Schema name',
    description TEXT COMMENT 'Description',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_ds_name (data_source_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Schema information';
```

### tbl_table
Table information
```sql
CREATE TABLE tbl_table (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    schema_id VARCHAR(36) NOT NULL COMMENT 'Schema ID',
    name VARCHAR(100) NOT NULL COMMENT 'Table name',
    description TEXT COMMENT 'Description',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_schema_name (schema_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Table information';
```

### tbl_column
Column information
```sql
CREATE TABLE tbl_column (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    table_id VARCHAR(36) NOT NULL COMMENT 'Table ID',
    name VARCHAR(100) NOT NULL COMMENT 'Column name',
    data_type VARCHAR(50) NOT NULL COMMENT 'Data type',
    length INT COMMENT 'Length',
    precision INT COMMENT 'Precision',
    scale INT COMMENT 'Scale',
    nullable BOOLEAN NOT NULL COMMENT 'Nullable',
    is_primary_key BOOLEAN NOT NULL COMMENT 'Is primary key',
    description TEXT COMMENT 'Description',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_table_name (table_id, name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Column information';
```

## Query Management

### tbl_query
Query information
```sql
CREATE TABLE tbl_query (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    name VARCHAR(100) NOT NULL COMMENT 'Query name',
    description TEXT COMMENT 'Description',
    data_source_id VARCHAR(36) NOT NULL COMMENT 'Data source ID',
    sql_content TEXT NOT NULL COMMENT 'SQL content',
    version INT NOT NULL COMMENT 'Version number',
    status VARCHAR(20) NOT NULL COMMENT 'Status: DRAFT, PUBLISHED',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    KEY idx_creator (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Query information';
```

### tbl_query_history
Query execution history
```sql
CREATE TABLE tbl_query_history (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    query_id VARCHAR(36) COMMENT 'Query ID',
    data_source_id VARCHAR(36) NOT NULL COMMENT 'Data source ID',
    sql_content TEXT NOT NULL COMMENT 'SQL content',
    execution_time BIGINT NOT NULL COMMENT 'Execution time (ms)',
    row_count INT NOT NULL COMMENT 'Result row count',
    status VARCHAR(20) NOT NULL COMMENT 'Status: SUCCESS, FAILED',
    error_message TEXT COMMENT 'Error message',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    PRIMARY KEY (id),
    KEY idx_query_id (query_id),
    KEY idx_creator (created_by)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Query execution history';
```

### tbl_table_relationship
Table relationship information
```sql
CREATE TABLE tbl_table_relationship (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    source_table_id VARCHAR(36) NOT NULL COMMENT 'Source table ID',
    target_table_id VARCHAR(36) NOT NULL COMMENT 'Target table ID',
    source_column_id VARCHAR(36) NOT NULL COMMENT 'Source column ID',
    target_column_id VARCHAR(36) NOT NULL COMMENT 'Target column ID',
    relationship_type VARCHAR(20) NOT NULL COMMENT 'Type: MANUAL, AUTO_DETECTED',
    confidence DECIMAL(5,2) COMMENT 'Confidence score for auto-detected relationships',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_relationship (source_table_id, target_table_id, source_column_id, target_column_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Table relationship information';
```

## Display Configuration

### tbl_display_config
Display configuration
```sql
CREATE TABLE tbl_display_config (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    query_id VARCHAR(36) NOT NULL COMMENT 'Query ID',
    display_type VARCHAR(20) NOT NULL COMMENT 'Display type: FORM, TABLE, CHART',
    config_content JSON NOT NULL COMMENT 'Configuration content',
    is_default BOOLEAN NOT NULL COMMENT 'Is default configuration',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    KEY idx_query_id (query_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Display configuration';
```

### tbl_user_preference
User preference settings
```sql
CREATE TABLE tbl_user_preference (
    id VARCHAR(36) NOT NULL COMMENT 'Primary key',
    user_id VARCHAR(36) NOT NULL COMMENT 'User ID',
    preference_type VARCHAR(20) NOT NULL COMMENT 'Preference type',
    preference_key VARCHAR(100) NOT NULL COMMENT 'Preference key',
    preference_value TEXT NOT NULL COMMENT 'Preference value',
    nonce INT NOT NULL DEFAULT 0 COMMENT 'Optimistic lock version',
    created_at DATETIME NOT NULL COMMENT 'Create time',
    created_by VARCHAR(36) NOT NULL COMMENT 'Creator',
    modified_at DATETIME NOT NULL COMMENT 'Last modify time',
    modified_by VARCHAR(36) NOT NULL COMMENT 'Last modifier',
    PRIMARY KEY (id),
    UNIQUE KEY u_idx_user_pref (user_id, preference_type, preference_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='User preference settings';