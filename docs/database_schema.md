# Database Schema Design

## Overview
This document outlines the database schema design for the DataScope system, including tables, relationships, and indexes.

## Core Tables

### Data Sources (tbl_data_source)
```sql
CREATE TABLE tbl_data_source (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,  -- MYSQL, DB2
    host VARCHAR(255) NOT NULL,
    port INT NOT NULL,
    database_name VARCHAR(100) NOT NULL,
    schema_name VARCHAR(100),
    username VARCHAR(100) NOT NULL,
    password_encrypted VARCHAR(255) NOT NULL,
    description TEXT,
    connection_timeout INT DEFAULT 30,
    max_pool_size INT DEFAULT 10,
    auto_sync BOOLEAN DEFAULT true,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_data_source_name UNIQUE (name)
);
```

### Metadata Tables (tbl_table_metadata)
```sql
CREATE TABLE tbl_table_metadata (
    id VARCHAR(36) NOT NULL,
    data_source_id VARCHAR(36) NOT NULL,
    schema_name VARCHAR(100) NOT NULL,
    table_name VARCHAR(100) NOT NULL,
    table_type VARCHAR(50) NOT NULL,  -- TABLE, VIEW
    description TEXT,
    row_count BIGINT,
    size_bytes BIGINT,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_table_metadata UNIQUE (data_source_id, schema_name, table_name),
    CONSTRAINT fk_table_metadata_data_source FOREIGN KEY (data_source_id) 
        REFERENCES tbl_data_source(id)
);

CREATE TABLE tbl_column_metadata (
    id VARCHAR(36) NOT NULL,
    table_metadata_id VARCHAR(36) NOT NULL,
    column_name VARCHAR(100) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    column_type VARCHAR(100) NOT NULL,
    is_nullable BOOLEAN NOT NULL,
    is_primary_key BOOLEAN NOT NULL,
    is_foreign_key BOOLEAN NOT NULL,
    column_default TEXT,
    description TEXT,
    ordinal_position INT NOT NULL,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_column_metadata UNIQUE (table_metadata_id, column_name),
    CONSTRAINT fk_column_metadata_table FOREIGN KEY (table_metadata_id) 
        REFERENCES tbl_table_metadata(id)
);

CREATE TABLE tbl_index_metadata (
    id VARCHAR(36) NOT NULL,
    table_metadata_id VARCHAR(36) NOT NULL,
    index_name VARCHAR(100) NOT NULL,
    index_type VARCHAR(50) NOT NULL,  -- BTREE, HASH
    is_unique BOOLEAN NOT NULL,
    description TEXT,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_index_metadata UNIQUE (table_metadata_id, index_name),
    CONSTRAINT fk_index_metadata_table FOREIGN KEY (table_metadata_id) 
        REFERENCES tbl_table_metadata(id)
);

CREATE TABLE tbl_index_column (
    id VARCHAR(36) NOT NULL,
    index_metadata_id VARCHAR(36) NOT NULL,
    column_metadata_id VARCHAR(36) NOT NULL,
    ordinal_position INT NOT NULL,
    sort_order VARCHAR(4) NOT NULL,  -- ASC, DESC
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_index_column UNIQUE (index_metadata_id, column_metadata_id),
    CONSTRAINT fk_index_column_index FOREIGN KEY (index_metadata_id) 
        REFERENCES tbl_index_metadata(id),
    CONSTRAINT fk_index_column_column FOREIGN KEY (column_metadata_id) 
        REFERENCES tbl_column_metadata(id)
);
```

### Table Relations (tbl_table_relation)
```sql
CREATE TABLE tbl_table_relation (
    id VARCHAR(36) NOT NULL,
    source_table_id VARCHAR(36) NOT NULL,
    target_table_id VARCHAR(36) NOT NULL,
    relation_type VARCHAR(50) NOT NULL,  -- FK, INFERRED
    confidence DECIMAL(5,2),  -- For inferred relations
    description TEXT,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_table_relation UNIQUE (source_table_id, target_table_id, relation_type),
    CONSTRAINT fk_relation_source_table FOREIGN KEY (source_table_id) 
        REFERENCES tbl_table_metadata(id),
    CONSTRAINT fk_relation_target_table FOREIGN KEY (target_table_id) 
        REFERENCES tbl_table_metadata(id)
);

CREATE TABLE tbl_column_relation (
    id VARCHAR(36) NOT NULL,
    table_relation_id VARCHAR(36) NOT NULL,
    source_column_id VARCHAR(36) NOT NULL,
    target_column_id VARCHAR(36) NOT NULL,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_column_relation UNIQUE (table_relation_id, source_column_id, target_column_id),
    CONSTRAINT fk_column_relation_table_relation FOREIGN KEY (table_relation_id) 
        REFERENCES tbl_table_relation(id),
    CONSTRAINT fk_column_relation_source FOREIGN KEY (source_column_id) 
        REFERENCES tbl_column_metadata(id),
    CONSTRAINT fk_column_relation_target FOREIGN KEY (target_column_id) 
        REFERENCES tbl_column_metadata(id)
);
```

### Queries (tbl_query)
```sql
CREATE TABLE tbl_query (
    id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    data_source_id VARCHAR(36) NOT NULL,
    sql_text TEXT NOT NULL,
    version INT NOT NULL DEFAULT 1,
    is_active BOOLEAN DEFAULT true,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_query_data_source FOREIGN KEY (data_source_id) 
        REFERENCES tbl_data_source(id)
);

CREATE TABLE tbl_query_version (
    id VARCHAR(36) NOT NULL,
    query_id VARCHAR(36) NOT NULL,
    version INT NOT NULL,
    sql_text TEXT NOT NULL,
    change_notes TEXT,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_query_version UNIQUE (query_id, version),
    CONSTRAINT fk_query_version_query FOREIGN KEY (query_id) 
        REFERENCES tbl_query(id)
);

CREATE TABLE tbl_query_parameter (
    id VARCHAR(36) NOT NULL,
    query_id VARCHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    data_type VARCHAR(50) NOT NULL,
    is_required BOOLEAN DEFAULT false,
    default_value TEXT,
    description TEXT,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_query_parameter UNIQUE (query_id, name),
    CONSTRAINT fk_query_parameter_query FOREIGN KEY (query_id) 
        REFERENCES tbl_query(id)
);
```

### Query History (tbl_query_history)
```sql
CREATE TABLE tbl_query_history (
    id VARCHAR(36) NOT NULL,
    query_id VARCHAR(36),  -- NULL for ad-hoc queries
    sql_text TEXT NOT NULL,
    parameters TEXT,  -- JSON format
    execution_time BIGINT,  -- in milliseconds
    row_count INT,
    status VARCHAR(50) NOT NULL,  -- SUCCESS, FAILED, CANCELLED
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_query_history_query FOREIGN KEY (query_id) 
        REFERENCES tbl_query(id)
);
```

### User Preferences (tbl_user_preference)
```sql
CREATE TABLE tbl_user_preference (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    preference_key VARCHAR(100) NOT NULL,
    preference_value TEXT NOT NULL,
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_user_preference UNIQUE (user_id, preference_key)
);
```

### Favorites (tbl_favorite)
```sql
CREATE TABLE tbl_favorite (
    id VARCHAR(36) NOT NULL,
    user_id VARCHAR(100) NOT NULL,
    item_type VARCHAR(50) NOT NULL,  -- QUERY, TABLE, DATA_SOURCE
    item_id VARCHAR(36) NOT NULL,
    folder_name VARCHAR(100),
    nonce INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    modified_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT u_idx_favorite UNIQUE (user_id, item_type, item_id)
);
```

## Indexes

### Performance Indexes
```sql
CREATE INDEX idx_data_source_type ON tbl_data_source(type);
CREATE INDEX idx_table_metadata_data_source ON tbl_table_metadata(data_source_id);
CREATE INDEX idx_column_metadata_table ON tbl_column_metadata(table_metadata_id);
CREATE INDEX idx_index_metadata_table ON tbl_index_metadata(table_metadata_id);
CREATE INDEX idx_table_relation_source ON tbl_table_relation(source_table_id);
CREATE INDEX idx_table_relation_target ON tbl_table_relation(target_table_id);
CREATE INDEX idx_query_data_source ON tbl_query(data_source_id);
CREATE INDEX idx_query_history_query ON tbl_query_history(query_id);
CREATE INDEX idx_query_history_created ON tbl_query_history(created_at);
CREATE INDEX idx_favorite_user ON tbl_favorite(user_id);
```

## Data Types

### Common Enums
```sql
-- Data Source Types
MYSQL
DB2

-- Table Types
TABLE
VIEW

-- Index Types
BTREE
HASH

-- Relation Types
FK          -- Foreign Key
INFERRED    -- AI Inferred Relation

-- Query Status
SUCCESS
FAILED
CANCELLED

-- Favorite Item Types
QUERY
TABLE
DATA_SOURCE
```

## Notes

1. All tables use UUID as primary key for better scalability and data migration
2. Timestamps are stored in UTC
3. Soft delete is not implemented but can be added if needed
4. Version control is implemented for queries
5. User authentication and authorization are handled by external systems
6. All tables include audit fields (created_at, created_by, modified_at, modified_by)
7. Optimistic locking is implemented using the nonce field
8. Unique constraints are prefixed with u_idx_
9. Foreign key constraints are prefixed with fk_
10. Regular indexes are prefixed with idx_