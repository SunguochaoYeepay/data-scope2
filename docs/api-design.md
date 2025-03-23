# DataScope API Design

## API Standards

### General Principles
- RESTful API design
- JSON request/response format
- Versioning in URL path (/api/v1/...)
- Consistent error handling
- Authentication via JWT
- Rate limiting headers
- HTTPS only

### Response Format
```json
{
    "code": 0,           // 0 for success, other for error
    "message": "string", // Success or error message
    "data": {},          // Response data
    "timestamp": "string" // ISO-8601 format
}
```

### Error Codes
- 1000-1999: Authentication/Authorization errors
- 2000-2999: Input validation errors
- 3000-3999: Business logic errors
- 4000-4999: System errors
- 5000-5999: External service errors

## API Endpoints

### Data Source Management

#### List Data Sources
```
GET /api/v1/datasources
Query Parameters:
- page: int (default: 1)
- size: int (default: 10)
- status: string (optional)
- type: string (optional)
```

#### Create Data Source
```
POST /api/v1/datasources
Request Body:
{
    "name": "string",
    "type": "string",
    "host": "string",
    "port": int,
    "databaseName": "string",
    "username": "string",
    "password": "string",
    "description": "string"
}
```

#### Update Data Source
```
PUT /api/v1/datasources/{id}
Request Body:
{
    "name": "string",
    "host": "string",
    "port": int,
    "username": "string",
    "password": "string",
    "description": "string"
}
```

#### Test Connection
```
POST /api/v1/datasources/{id}/test
Response:
{
    "success": boolean,
    "message": "string"
}
```

### Metadata Management

#### Get Schema List
```
GET /api/v1/datasources/{id}/schemas
Query Parameters:
- page: int
- size: int
```

#### Get Tables
```
GET /api/v1/schemas/{id}/tables
Query Parameters:
- page: int
- size: int
- search: string
```

#### Get Columns
```
GET /api/v1/tables/{id}/columns
Query Parameters:
- page: int
- size: int
```

#### Sync Metadata
```
POST /api/v1/datasources/{id}/sync
Query Parameters:
- type: string (FULL/INCREMENTAL)
```

### Query Management

#### Execute Query
```
POST /api/v1/queries/execute
Request Body:
{
    "dataSourceId": "string",
    "sql": "string",
    "parameters": {},
    "timeout": int,
    "maxRows": int
}
```

#### Natural Language Query
```
POST /api/v1/queries/nl
Request Body:
{
    "dataSourceId": "string",
    "question": "string",
    "context": {}
}
```

#### Save Query
```
POST /api/v1/queries
Request Body:
{
    "name": "string",
    "description": "string",
    "dataSourceId": "string",
    "sql": "string",
    "displayConfig": {}
}
```

#### Get Query History
```
GET /api/v1/queries/history
Query Parameters:
- page: int
- size: int
- userId: string
- status: string
```

### Display Configuration

#### Save Display Config
```
POST /api/v1/display-configs
Request Body:
{
    "queryId": "string",
    "displayType": "string",
    "config": {
        "conditions": [{
            "field": "string",
            "label": "string",
            "type": "string",
            "required": boolean,
            "defaultValue": "string"
        }],
        "columns": [{
            "field": "string",
            "label": "string",
            "type": "string",
            "mask": "string",
            "format": "string"
        }],
        "operations": [{
            "type": "string",
            "label": "string",
            "action": "string"
        }]
    }
}
```

#### Get Display Config
```
GET /api/v1/display-configs/{id}
```

### User Preferences

#### Save Preferences
```
POST /api/v1/preferences
Request Body:
{
    "type": "string",
    "key": "string",
    "value": "string"
}
```

#### Get Preferences
```
GET /api/v1/preferences
Query Parameters:
- type: string
```

## Low Code Integration

### Generate Query API
```
POST /api/v1/low-code/apis
Request Body:
{
    "queryId": "string",
    "apiPath": "string",
    "method": "string",
    "parameters": [{
        "name": "string",
        "type": "string",
        "required": boolean,
        "defaultValue": "string"
    }]
}
```

### Get API Configuration
```
GET /api/v1/low-code/apis/{id}/config
Response:
{
    "apiConfig": {
        "path": "string",
        "method": "string",
        "parameters": []
    },
    "displayConfig": {
        "conditions": [],
        "columns": [],
        "operations": []
    }
}
```

## Rate Limiting

### Headers
```
X-RateLimit-Limit: Maximum requests per window
X-RateLimit-Remaining: Remaining requests in current window
X-RateLimit-Reset: Time when the rate limit resets
```

### Limits
- API calls: 1000 requests per minute per user
- Query execution: 10 concurrent queries per user
- Data download: 50000 rows per request