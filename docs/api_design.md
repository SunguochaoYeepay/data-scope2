# DataScope - API设计

## 概述

本文档概述了DataScope系统的API设计，包括RESTful端点、请求/响应格式和低代码集成协议。API遵循REST原则，并使用JSON进行数据交换。

## API版本控制

所有API端点都使用URL路径进行版本控制（例如，`/api/v1/...`）。这允许在引入更改时保持向后兼容性。当对端点进行重大更改时，应创建新版本而不是修改现有版本。

## 通用响应格式

所有API响应遵循一致的格式：

```json
{
  "success": true,
  "data": { ... },
  "error": null,
  "meta": {
    "timestamp": "2025-03-23T02:50:00Z",
    "requestId": "req-123456",
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "totalItems": 100,
      "totalPages": 5
    }
  }
}
```

对于错误响应：

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "人类可读的错误消息",
    "details": { ... }
  },
  "meta": {
    "timestamp": "2025-03-23T02:50:00Z",
    "requestId": "req-123456"
  }
}
```

## API端点

### 数据源管理

- `GET /api/v1/datasources` - 列出所有数据源
- `GET /api/v1/datasources/{id}` - 获取数据源详情
- `POST /api/v1/datasources` - 创建新数据源
- `PUT /api/v1/datasources/{id}` - 更新数据源
- `DELETE /api/v1/datasources/{id}` - 删除数据源
- `POST /api/v1/datasources/test-connection` - 测试数据源连接
- `POST /api/v1/datasources/{id}/sync` - 触发元数据同步
- `GET /api/v1/datasources/sync-jobs/{jobId}` - 获取同步作业状态

### 元数据浏览

- `GET /api/v1/metadata/datasources/{dataSourceId}/schemas` - 列出模式
- `GET /api/v1/metadata/schemas/{schemaId}/tables` - 列出模式中的表
- `GET /api/v1/metadata/tables/{tableId}/columns` - 列出表中的列
- `GET /api/v1/metadata/search` - 搜索元数据

### 查询管理

- `POST /api/v1/queries/execute` - 执行SQL查询
- `POST /api/v1/queries/natural-language` - 执行自然语言查询
- `POST /api/v1/queries/natural-language/refine` - 优化自然语言查询
- `GET /api/v1/queries` - 列出保存的查询
- `GET /api/v1/queries/{id}` - 获取查询详情
- `POST /api/v1/queries` - 保存新查询
- `PUT /api/v1/queries/{id}` - 更新查询
- `GET /api/v1/queries/history` - 获取查询历史
- `GET /api/v1/queries/{id}/download` - 将查询结果下载为CSV

### 关系管理

- `GET /api/v1/relationships` - 列出关系
- `POST /api/v1/relationships` - 创建关系
- `PUT /api/v1/relationships/{id}` - 更新关系
- `DELETE /api/v1/relationships/{id}` - 删除关系
- `GET /api/v1/relationships/inferred` - 列出推断的关系
- `POST /api/v1/relationships/inferred/{id}/approve` - 批准推断的关系

### 低代码集成

- `GET /api/v1/lowcode/apis` - 列出生成的API
- `POST /api/v1/lowcode/apis` - 生成新API
- `GET /api/v1/lowcode/apis/{id}` - 获取API详情
- `PUT /api/v1/lowcode/apis/{id}` - 更新API
- `DELETE /api/v1/lowcode/apis/{id}` - 删除API
- `GET /api/v1/lowcode/ui-configs` - 列出UI配置
- `POST /api/v1/lowcode/ui-configs` - 创建UI配置
- `PUT /api/v1/lowcode/ui-configs/{id}` - 更新UI配置

## 请求/响应示例

### 执行SQL查询

请求：
```json
POST /api/v1/queries/execute
{
  "dataSourceId": "550e8400-e29b-41d4-a716-446655440000",
  "sql": "SELECT customer_id, first_name, last_name FROM customers WHERE city = :city LIMIT 100",
  "parameters": {
    "city": "New York"
  },
  "timeout": 30
}
```

响应：
```json
{
  "success": true,
  "data": {
    "columns": [
      {
        "name": "customer_id",
        "type": "string"
      },
      {
        "name": "first_name",
        "type": "string"
      },
      {
        "name": "last_name",
        "type": "string"
      }
    ],
    "rows": [
      {
        "customer_id": "cust-001",
        "first_name": "John",
        "last_name": "Doe"
      },
      {
        "customer_id": "cust-002",
        "first_name": "Jane",
        "last_name": "Smith"
      }
    ],
    "totalRows": 2,
    "executionTimeMs": 45,
    "hasMore": false
  },
  "error": null,
  "meta": {
    "timestamp": "2025-03-23T02:50:00Z",
    "requestId": "req-123456"
  }
}
```

### 执行自然语言查询

请求：
```json
POST /api/v1/queries/natural-language
{
  "dataSourceId": "550e8400-e29b-41d4-a716-446655440000",
  "naturalLanguage": "显示所有来自纽约的客户",
  "maxResults": 100,
  "timeout": 30
}
```

响应：
```json
{
  "success": true,
  "data": {
    "generatedSql": "SELECT * FROM customers WHERE city = 'New York' LIMIT 100",
    "confidence": 0.92,
    "columns": [
      {
        "name": "customer_id",
        "type": "string"
      },
      {
        "name": "first_name",
        "type": "string"
      },
      {
        "name": "last_name",
        "type": "string"
      },
      {
        "name": "city",
        "type": "string"
      }
    ],
    "rows": [
      {
        "customer_id": "cust-001",
        "first_name": "John",
        "last_name": "Doe",
        "city": "New York"
      },
      {
        "customer_id": "cust-002",
        "first_name": "Jane",
        "last_name": "Smith",
        "city": "New York"
      }
    ],
    "totalRows": 2,
    "executionTimeMs": 145,
    "hasMore": false
  },
  "error": null,
  "meta": {
    "timestamp": "2025-03-23T02:50:00Z",
    "requestId": "req-123456"
  }
}
```

## 低代码集成协议

系统使用基于JSON的协议与低代码平台集成：

```json
{
  "apiEndpoint": "/api/v1/generated/query123",
  "version": "1.0",
  "queryConfig": {
    "id": "query-550e8400-e29b-41d4-a716-446655440000",
    "name": "客户订单",
    "description": "检索带有详细信息的客户订单"
  },
  "parameters": [
    {
      "name": "customerId",
      "label": "客户ID",
      "type": "string",
      "required": true,
      "defaultValue": null,
      "componentType": "text-input"
    },
    {
      "name": "startDate",
      "label": "开始日期",
      "type": "date",
      "required": false,
      "defaultValue": "2023-01-01",
      "componentType": "date-picker"
    }
  ],
  "results": {
    "columns": [
      {
        "name": "order_id",
        "label": "订单ID",
        "type": "string",
        "sortable": true,
        "filterable": true,
        "visible": true,
        "componentType": "text",
        "width": "100px"
      },
      {
        "name": "order_date",
        "label": "订单日期",
        "type": "date",
        "sortable": true,
        "filterable": true,
        "visible": true,
        "componentType": "date",
        "format": "YYYY-MM-DD",
        "width": "120px"
      },
      {
        "name": "credit_card",
        "label": "信用卡",
        "type": "string",
        "sortable": false,
        "filterable": false,
        "visible": true,
        "componentType": "text",
        "masking": {
          "type": "partial",
          "pattern": "****-****-****-$$$$"
        },
        "width": "150px"
      }
    ],
    "actions": [
      {
        "name": "view",
        "label": "查看",
        "icon": "eye",
        "endpoint": "/api/v1/orders/{order_id}"
      },
      {
        "name": "edit",
        "label": "编辑",
        "icon": "pencil",
        "endpoint": "/api/v1/orders/{order_id}/edit"
      }
    ],
    "pagination": {
      "enabled": true,
      "pageSize": 20,
      "pageSizeOptions": [10, 20, 50, 100]
    },
    "defaultSort": {
      "column": "order_date",
      "direction": "desc"
    }
  }
}
```

## 安全考虑

- 通过外部系统进行身份验证
- API级别的授权
- 速率限制以防止滥用
- 查询超时（默认30秒）
- 下载限制（最多50,000行）
- 敏感数据掩码
- 加密凭据存储

## 性能考虑

- 数据库访问的连接池
- 查询结果缓存
- 大型结果集的分页
- 长时间运行操作的异步处理
- 增量元数据同步
