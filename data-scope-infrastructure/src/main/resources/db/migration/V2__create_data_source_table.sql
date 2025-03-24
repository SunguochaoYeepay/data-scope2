CREATE TABLE tbl_data_source
(
    id               VARCHAR(36)  NOT NULL,
    name             VARCHAR(100) NOT NULL,
    type             VARCHAR(20)  NOT NULL,
    host             VARCHAR(255) NOT NULL,
    port             INTEGER      NOT NULL,
    database_name    VARCHAR(100) NOT NULL,
    username         VARCHAR(100) NOT NULL,
    password_encrypted VARCHAR(255) NOT NULL,
    salt             VARCHAR(32)  NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    last_sync_time TIMESTAMP,
    last_sync_status VARCHAR(20)  NOT NULL,
    last_sync_message  TEXT,
    remark             TEXT,
    nonce              INTEGER DEFAULT 1,
    created_time   TIMESTAMP NOT NULL,
    created_by       VARCHAR(36)  NOT NULL,
    modified_time  TIMESTAMP NOT NULL,
    modified_by        VARCHAR(36)  NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes
CREATE INDEX idx_data_source_name ON tbl_data_source (name);
CREATE INDEX idx_data_source_type ON tbl_data_source (type);
CREATE INDEX idx_data_source_status ON tbl_data_source (status);
CREATE INDEX idx_data_source_host ON tbl_data_source (host);
CREATE INDEX idx_data_source_database ON tbl_data_source (database_name);

-- Add unique constraint
ALTER TABLE tbl_data_source
    ADD CONSTRAINT uk_data_source_name UNIQUE (name);
