# DataScope - 数据库设计

## 概述

本文档概述了DataScope系统的数据库设计，遵循指定的数据库设计规则：

- 所有表名和字段名使用小写字母，以下划线作为分隔符
- 表名使用 `tbl_` 前缀
- 主键使用UUID格式
- 历史表记录创建时间和创建者
- 实体表包括乐观锁和跟踪字段
- 唯一索引使用 `u_idx_` 前缀
- 其他索引使用 `idx_` 前缀

## 核心表

### 数据源管理

#### tbl_data_source
存储已配置数据源的信息。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| name | varchar(100) | 数据源名称 | NOT NULL |
| description | text | 数据源描述 | |
| type | varchar(20) | 数据源类型 (MYSQL, DB2) | NOT NULL |
| host | varchar(255) | 数据库主机 | NOT NULL |
| port | int | 数据库端口 | NOT NULL |
| database_name | varchar(100) | 数据库名称 | NOT NULL |
| username | varchar(100) | 数据库用户名 | NOT NULL |
| password_encrypted | varchar(255) | 加密密码 | NOT NULL |
| password_salt | varchar(36) | 用于密码加密的盐 | NOT NULL |
| connection_params | text | 额外连接参数（JSON格式） | |
| status | varchar(20) | 连接状态 (ACTIVE, INACTIVE, ERROR) | NOT NULL |
| last_sync_time | datetime | 最后元数据同步时间 | |
| sync_frequency | varchar(50) | 同步调度的Cron表达式 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_data_source_name` (name) - 数据源名称的唯一索引

#### tbl_metadata_schema
存储每个数据源的模式信息。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| data_source_id | varchar(36) | 数据源引用 | FK, NOT NULL |
| name | varchar(100) | 模式名称 | NOT NULL |
| description | text | 模式描述 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_schema_ds_name` (data_source_id, name) - 数据源和模式名称的唯一索引
- `idx_schema_data_source` (data_source_id) - 用于按数据源快速查找的索引

#### tbl_metadata_table
存储每个模式的表元数据。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| schema_id | varchar(36) | 模式引用 | FK, NOT NULL |
| name | varchar(100) | 表名 | NOT NULL |
| description | text | 表描述 | |
| estimated_row_count | bigint | 估计行数 | |
| last_analyzed | datetime | 最后分析表统计信息的时间 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_table_schema_name` (schema_id, name) - 模式和表名的唯一索引
- `idx_table_schema` (schema_id) - 用于按模式快速查找的索引

#### tbl_metadata_column
存储每个表的列元数据。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| table_id | varchar(36) | 表引用 | FK, NOT NULL |
| name | varchar(100) | 列名 | NOT NULL |
| description | text | 列描述 | |
| data_type | varchar(50) | 数据库数据类型 | NOT NULL |
| length | int | 适用类型的长度/精度 | |
| scale | int | 数值类型的小数位数 | |
| nullable | boolean | 列是否允许NULL值 | NOT NULL |
| is_primary_key | boolean | 列是否为主键的一部分 | NOT NULL, DEFAULT false |
| is_foreign_key | boolean | 列是否为外键 | NOT NULL, DEFAULT false |
| is_indexed | boolean | 列是否被索引 | NOT NULL, DEFAULT false |
| default_value | text | 默认值表达式 | |
| ordinal_position | int | 列在表中的位置 | NOT NULL |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_column_table_name` (table_id, name) - 表和列名的唯一索引
- `idx_column_table` (table_id) - 用于按表快速查找的索引
- `idx_column_primary_key` (is_primary_key) - 用于查找主键列的索引
- `idx_column_foreign_key` (is_foreign_key) - 用于查找外键列的索引

#### tbl_metadata_index
存储表的索引信息。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| table_id | varchar(36) | 表引用 | FK, NOT NULL |
| name | varchar(100) | 索引名称 | NOT NULL |
| is_unique | boolean | 索引是否强制唯一性 | NOT NULL, DEFAULT false |
| index_type | varchar(50) | 索引类型 (BTREE, HASH等) | NOT NULL |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_index_table_name` (table_id, name) - 表和索引名称的唯一索引
- `idx_index_table` (table_id) - 用于按表快速查找的索引

#### tbl_metadata_index_column
将列映射到索引。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| index_id | varchar(36) | 索引引用 | FK, NOT NULL |
| column_id | varchar(36) | 列引用 | FK, NOT NULL |
| ordinal_position | int | 列在索引中的位置 | NOT NULL |
| sort_direction | varchar(4) | 排序方向 (ASC, DESC) | NOT NULL, DEFAULT 'ASC' |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_index_column` (index_id, column_id) - 索引和列的唯一索引
- `idx_index_column_index` (index_id) - 用于按索引快速查找的索引
- `idx_index_column_column` (column_id) - 用于按列快速查找的索引

### 关系管理

#### tbl_relationship
存储表之间的关系。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| name | varchar(100) | 关系名称 | NOT NULL |
| source_table_id | varchar(36) | 源表ID | FK, NOT NULL |
| target_table_id | varchar(36) | 目标表ID | FK, NOT NULL |
| relationship_type | varchar(20) | 类型 (ONE_TO_ONE, ONE_TO_MANY等) | NOT NULL |
| is_inferred | boolean | 关系是否被推断 | NOT NULL, DEFAULT false |
| confidence_score | decimal(5,2) | 推断关系的置信度分数 | |
| is_approved | boolean | 推断关系是否被批准 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_relationship_name` (name) - 关系名称的唯一索引
- `idx_relationship_source` (source_table_id) - 用于按源表快速查找的索引
- `idx_relationship_target` (target_table_id) - 用于按目标表快速查找的索引
- `idx_relationship_inferred` (is_inferred) - 用于查找推断关系的索引

#### tbl_relationship_column
映射关系中涉及的列。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| relationship_id | varchar(36) | 关系引用 | FK, NOT NULL |
| source_column_id | varchar(36) | 源列ID | FK, NOT NULL |
| target_column_id | varchar(36) | 目标列ID | FK, NOT NULL |
| ordinal_position | int | 在多列关系中的位置 | NOT NULL |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_rel_column_mapping` (relationship_id, source_column_id, target_column_id) - 唯一映射
- `idx_rel_column_relationship` (relationship_id) - 用于按关系快速查找的索引
- `idx_rel_column_source` (source_column_id) - 用于按源列快速查找的索引
- `idx_rel_column_target` (target_column_id) - 用于按目标列快速查找的索引

### 查询管理

#### tbl_query
存储保存的查询。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| name | varchar(100) | 查询名称 | NOT NULL |
| description | text | 查询描述 | |
| data_source_id | varchar(36) | 数据源引用 | FK, NOT NULL |
| current_version_id | varchar(36) | 当前活动版本 | FK |
| is_favorite | boolean | 查询是否被标记为收藏 | NOT NULL, DEFAULT false |
| folder | varchar(100) | 组织文件夹 | |
| tags | varchar(255) | 逗号分隔的标签 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_query_name_user` (name, created_by) - 每个用户的查询名称唯一索引
- `idx_query_data_source` (data_source_id) - 用于按数据源快速查找的索引
- `idx_query_current_version` (current_version_id) - 当前版本的索引
- `idx_query_favorite` (is_favorite) - 用于查找收藏查询的索引
- `idx_query_folder` (folder) - 用于按文件夹组织的索引
- `idx_query_creator` (created_by) - 用于查找用户查询的索引

#### tbl_query_version
存储查询的版本。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| query_id | varchar(36) | 查询引用 | FK, NOT NULL |
| version_number | int | 顺序版本号 | NOT NULL |
| sql_text | text | SQL查询文本 | NOT NULL |
| natural_language_description | text | 自然语言描述 | |
| parameters_json | text | 查询参数（JSON格式） | |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |

**索引:**
- `u_idx_query_version` (query_id, version_number) - 查询和版本的唯一索引
- `idx_query_version_query` (query_id) - 用于按查询快速查找的索引

#### tbl_query_history
跟踪查询执行历史。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| query_id | varchar(36) | 查询引用（临时查询为NULL） | FK |
| query_version_id | varchar(36) | 查询版本引用（临时查询为NULL） | FK |
| sql_text | text | SQL查询文本 | NOT NULL |
| parameters_json | text | 查询参数（JSON格式） | |
| execution_time_ms | int | 执行时间（毫秒） | |
| row_count | int | 返回的行数 | |
| status | varchar(20) | 执行状态 (SUCCESS, ERROR, TIMEOUT) | NOT NULL |
| error_message | text | 失败时的错误消息 | |
| created_at | datetime | 执行时间戳 | NOT NULL |
| created_by | varchar(100) | 执行查询的用户 | NOT NULL |

**索引:**
- `idx_history_query` (query_id) - 用于按查询快速查找的索引
- `idx_history_version` (query_version_id) - 用于按版本快速查找的索引
- `idx_history_user` (created_by) - 用于查找用户历史的索引
- `idx_history_timestamp` (created_at) - 用于基于时间过滤的索引
- `idx_history_status` (status) - 用于按状态过滤的索引

### 低代码集成

#### tbl_api_endpoint
存储API端点配置。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| name | varchar(100) | API端点名称 | NOT NULL |
| description | text | API端点描述 | |
| path | varchar(255) | API端点路径 | NOT NULL |
| query_id | varchar(36) | 查询引用 | FK, NOT NULL |
| query_version_id | varchar(36) | 特定查询版本的引用 | FK |
| parameters_json | text | 参数映射（JSON格式） | |
| rate_limit | int | 速率限制（每分钟请求数） | |
| timeout_seconds | int | 超时时间（秒） | NOT NULL, DEFAULT 30 |
| version | varchar(20) | API版本 | NOT NULL, DEFAULT '1.0' |
| is_active | boolean | 端点是否活动 | NOT NULL, DEFAULT true |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_api_path_version` (path, version) - 路径和版本的唯一索引
- `idx_api_query` (query_id) - 用于按查询快速查找的索引
- `idx_api_version` (query_version_id) - 用于按查询版本快速查找的索引
- `idx_api_active` (is_active) - 用于查找活动端点的索引

#### tbl_ui_configuration
存储UI显示配置。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| name | varchar(100) | 配置名称 | NOT NULL |
| description | text | 配置描述 | |
| query_id | varchar(36) | 查询引用 | FK, NOT NULL |
| display_type | varchar(50) | 显示类型 (FORM, TABLE, CHART等) | NOT NULL |
| configuration_json | text | 配置（JSON格式） | NOT NULL |
| is_default | boolean | 是否为默认配置 | NOT NULL, DEFAULT false |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_ui_config_name_query` (name, query_id) - 每个查询的配置名称唯一索引
- `idx_ui_config_query` (query_id) - 用于按查询快速查找的索引
- `idx_ui_config_default` (is_default) - 用于查找默认配置的索引
- `idx_ui_config_type` (display_type) - 用于按显示类型过滤的索引

#### tbl_user_preference
存储用户特定的偏好设置。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| user_id | varchar(100) | 用户标识符 | NOT NULL |
| preference_type | varchar(50) | 偏好类型 | NOT NULL |
| context | varchar(255) | 上下文（例如，查询ID，数据源ID） | |
| preference_json | text | 偏好数据（JSON格式） | NOT NULL |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_preference_user_type_context` (user_id, preference_type, context) - 唯一偏好
- `idx_preference_user` (user_id) - 用于按用户快速查找的索引
- `idx_preference_type` (preference_type) - 用于按偏好类型过滤的索引

### 系统管理

#### tbl_system_config
存储系统范围的配置设置。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| config_key | varchar(100) | 配置键 | NOT NULL |
| config_value | text | 配置值 | NOT NULL |
| description | text | 配置描述 | |
| nonce | int | 乐观锁版本 | NOT NULL, DEFAULT 0 |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |
| modified_at | datetime | 最后修改时间戳 | NOT NULL |
| modified_by | varchar(100) | 最后修改者标识符 | NOT NULL |

**索引:**
- `u_idx_config_key` (config_key) - 配置键的唯一索引

#### tbl_sync_job
跟踪元数据同步作业。

| 列 | 类型 | 描述 | 约束 |
|--------|------|-------------|------------|
| id | varchar(36) | 主键 (UUID) | PK |
| data_source_id | varchar(36) | 数据源引用 | FK, NOT NULL |
| job_type | varchar(50) | 同步作业类型 | NOT NULL |
| status | varchar(20) | 作业状态 | NOT NULL |
| start_time | datetime | 作业开始时间 | NOT NULL |
| end_time | datetime | 作业结束时间 | |
| duration_seconds | int | 作业持续时间（秒） | |
| items_processed | int | 处理的项目数 | |
| items_added | int | 添加的项目数 | |
| items_updated | int | 更新的项目数 | |
| items_deleted | int | 删除的项目数 | |
| error_message | text | 失败时的错误消息 | |
| created_at | datetime | 创建时间戳 | NOT NULL |
| created_by | varchar(100) | 创建者标识符 | NOT NULL |

**索引:**
- `idx_sync_data_source` (data_source_id) - 用于按数据源快速查找的索引
- `idx_sync_status` (status) - 用于按状态过滤的索引
- `idx_sync_start_time` (start_time) - 用于基于时间过滤的索引

## 关系

下图说明了表之间的关系：

```
tbl_data_source
    ↑
    | 1:N
    |
tbl_metadata_schema
    ↑
    | 1:N
    |
tbl_metadata_table ←→ tbl_relationship
    ↑                    ↑
    | 1:N                | 1:N
    |                    |
tbl_metadata_column ←→ tbl_relationship_column
    ↑
    | 1:N
    |
tbl_metadata_index
    ↑
    | 1:N
    |
tbl_metadata_index_column

tbl_query
    ↑
    | 1:N
    |
tbl_query_version ←→ tbl_query_history
    ↑
    | 1:N
    |
tbl_api_endpoint ←→ tbl_ui_configuration
```

## 数据类型映射

下表显示了数据库列数据类型和UI组件类型之间的映射：

| 数据库类型 | UI组件类型 | 备注 |
|---------------|-------------------|-------|
| varchar, char, text | text-input | 用于短文本 |
| text (large) | textarea | 用于较长文本 |
| int, bigint, smallint | number-input | 带有适当的最小/最大值 |
| decimal, numeric | number-input | 带有精度/小数位设置 |
| boolean | checkbox, toggle | |
| date | date-picker | |
| time | time-picker | |
| datetime, timestamp | datetime-picker | |
| enum | select, radio-group | 带有预定义选项 |
| set | multi-select, checkbox-group | 用于多选 |
| blob, binary | file-upload | 用于二进制数据 |
| json | json-editor | 用于结构化数据 |
| geometry | map | 用于空间数据 |

## 敏感数据类型

以下数据类型被视为敏感数据，应提供掩码选项：

| 数据类型 | 默认掩码模式 | 示例 |
|-----------|-------------------------|---------|
| 信用卡 | ****-****-****-$$$$ | 仅显示最后4位 |
| 电子邮件 | $$$@***.com | 前3个字符 + @ + 域名首字符 |
| 电话号码 | ($$$) $$$-**** | 仅显示前3位 |
| 社会安全号/国民身份证 | ***-**-$$$$ | 仅显示最后4位 |
| 密码 | ******** | 完全掩码 |
| 地址 | $$$* ***** | 显示前3个字符 |
| 姓名 | $$$* ***** | 显示前3个字符 |
| 账号 | *****$$$$ | 仅显示最后4位 |