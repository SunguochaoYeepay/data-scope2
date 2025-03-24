-- Schema for H2 database (Test Environment)
-- Generated from Flyway migration scripts

-- User Display Configuration Table
CREATE TABLE IF NOT EXISTS tbl_user_display_config (
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
                                                       last_used_time TIMESTAMP,
                                                       created_by     VARCHAR(36)  NOT NULL,
                                                       created_time   TIMESTAMP    NOT NULL,
                                                       updated_by     VARCHAR(36)  NOT NULL,
                                                       updated_time   TIMESTAMP    NOT NULL,
                                                       PRIMARY KEY (id)
);

-- Create indexes for tbl_user_display_config
CREATE INDEX IF NOT EXISTS idx_user_display_config_user_id ON tbl_user_display_config (user_id);
CREATE INDEX IF NOT EXISTS idx_user_display_config_data_source ON tbl_user_display_config (user_id, data_source_id);
CREATE INDEX IF NOT EXISTS idx_user_display_config_table ON tbl_user_display_config (user_id, data_source_id, table_name);
CREATE INDEX IF NOT EXISTS idx_user_display_config_column ON tbl_user_display_config (user_id, data_source_id, table_name, column_name);

-- Data Source Table
CREATE TABLE IF NOT EXISTS tbl_data_source
(
    id                 VARCHAR(36)  NOT NULL,
    name               VARCHAR(100) NOT NULL,
    type               VARCHAR(20)  NOT NULL,
    host               VARCHAR(255) NOT NULL,
    port               INTEGER      NOT NULL,
    database_name      VARCHAR(100) NOT NULL,
    username           VARCHAR(100) NOT NULL,
    password_encrypted VARCHAR(255) NOT NULL,
    salt               VARCHAR(32)  NOT NULL,
    status             VARCHAR(20)  NOT NULL,
    last_sync_time TIMESTAMP,
    last_sync_status   VARCHAR(20)  NOT NULL,
    last_sync_message  TEXT,
    remark             TEXT,
    nonce              INTEGER DEFAULT 1,
    created_time   TIMESTAMP NOT NULL,
    created_by         VARCHAR(36)  NOT NULL,
    modified_time  TIMESTAMP NOT NULL,
    modified_by        VARCHAR(36)  NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes for tbl_data_source
CREATE INDEX IF NOT EXISTS idx_data_source_name ON tbl_data_source (name);
CREATE INDEX IF NOT EXISTS idx_data_source_type ON tbl_data_source (type);
CREATE INDEX IF NOT EXISTS idx_data_source_status ON tbl_data_source (status);
CREATE INDEX IF NOT EXISTS idx_data_source_host ON tbl_data_source (host);
CREATE INDEX IF NOT EXISTS idx_data_source_database ON tbl_data_source (database_name);

-- Add unique constraint for tbl_data_source
ALTER TABLE tbl_data_source
    ADD CONSTRAINT IF NOT EXISTS uk_data_source_name UNIQUE (name);

-- Query Execution Table
CREATE TABLE IF NOT EXISTS tbl_query_execution
(
    id             VARCHAR(36) NOT NULL,
    data_source_id VARCHAR(36) NOT NULL,
    sql            TEXT        NOT NULL,
    status         VARCHAR(20) NOT NULL,
    start_time     TIMESTAMP   NOT NULL,
    end_time       TIMESTAMP,
    result_count   INTEGER,
    error_message  TEXT,
    user_id        VARCHAR(36) NOT NULL,
    created_by     VARCHAR(36) NOT NULL,
    created_time   TIMESTAMP   NOT NULL,
    updated_by     VARCHAR(36) NOT NULL,
    updated_time   TIMESTAMP   NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes for tbl_query_execution
CREATE INDEX IF NOT EXISTS idx_query_execution_data_source ON tbl_query_execution (data_source_id);
CREATE INDEX IF NOT EXISTS idx_query_execution_user ON tbl_query_execution (user_id);
CREATE INDEX IF NOT EXISTS idx_query_execution_status ON tbl_query_execution (status);
CREATE INDEX IF NOT EXISTS idx_query_execution_time ON tbl_query_execution (start_time, end_time);
