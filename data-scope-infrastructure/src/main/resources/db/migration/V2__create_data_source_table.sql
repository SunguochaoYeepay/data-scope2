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

-- Add comments
COMMENT
ON TABLE data_source IS '数据源表';
COMMENT
ON COLUMN data_source.id IS '主键ID';
COMMENT
ON COLUMN data_source.name IS '数据源名称';
COMMENT
ON COLUMN data_source.type IS '数据源类型';
COMMENT
ON COLUMN data_source.host IS '主机地址';
COMMENT
ON COLUMN data_source.port IS '端口号';
COMMENT
ON COLUMN data_source.database_name IS '数据库名称';
COMMENT
ON COLUMN data_source.username IS '用户名';
COMMENT
ON COLUMN data_source.password IS '密码';
COMMENT
ON COLUMN data_source.salt IS '密码盐值';
COMMENT
ON COLUMN data_source.status IS '状态';
COMMENT
ON COLUMN data_source.last_sync_status IS '最后同步状态';
COMMENT
ON COLUMN data_source.created_by IS '创建人';
COMMENT
ON COLUMN data_source.created_time IS '创建时间';
COMMENT
ON COLUMN data_source.updated_by IS '更新人';
COMMENT
ON COLUMN data_source.updated_time IS '更新时间';
