CREATE TABLE tbl_query_execution
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

-- Create indexes
CREATE INDEX idx_query_execution_data_source ON tbl_query_execution (data_source_id);
CREATE INDEX idx_query_execution_user ON tbl_query_execution (user_id);
CREATE INDEX idx_query_execution_status ON tbl_query_execution (status);
CREATE INDEX idx_query_execution_time ON tbl_query_execution (start_time, end_time);
