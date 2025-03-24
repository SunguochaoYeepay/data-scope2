CREATE TABLE data_source
(
    id               VARCHAR(36)  NOT NULL,
    name             VARCHAR(100) NOT NULL,
    type             VARCHAR(20)  NOT NULL,
    host             VARCHAR(255) NOT NULL,
    port             INTEGER      NOT NULL,
    database_name    VARCHAR(100) NOT NULL,
    username         VARCHAR(100) NOT NULL,
    password         VARCHAR(255) NOT NULL,
    salt             VARCHAR(32)  NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    last_sync_status VARCHAR(20)  NOT NULL,
    created_by       VARCHAR(36)  NOT NULL,
    created_time     TIMESTAMP    NOT NULL,
    updated_by       VARCHAR(36)  NOT NULL,
    updated_time     TIMESTAMP    NOT NULL,
    PRIMARY KEY (id)
);

-- Create indexes
CREATE INDEX idx_data_source_name ON data_source (name);
CREATE INDEX idx_data_source_type ON data_source (type);
CREATE INDEX idx_data_source_status ON data_source (status);
CREATE INDEX idx_data_source_host ON data_source (host);
CREATE INDEX idx_data_source_database ON data_source (database_name);

-- Add unique constraint
ALTER TABLE data_source
    ADD CONSTRAINT uk_data_source_name UNIQUE (name);