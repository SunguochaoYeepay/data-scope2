# DataScope - System Architecture

## Overview

DataScope is a comprehensive data management and query system designed to integrate various database systems, provide intelligent data discovery capabilities, and facilitate low-code application development. This document outlines the architectural design of the system.

## System Architecture

The architecture follows Domain-Driven Design (DDD) principles, organized into the following modules:

![Architecture Diagram](https://via.placeholder.com/800x600?text=DataScope+Architecture+Diagram)

### Modules

1. **App Module**
   - Contains application services that orchestrate domain operations
   - Implements use cases by coordinating domain objects
   - Manages transactions and security concerns

2. **Domain Module**
   - Core business logic and domain entities
   - Domain services for complex operations
   - Value objects and domain events
   - Repository interfaces

3. **Facade Module**
   - API controllers and endpoints
   - Request/response DTOs
   - API documentation
   - Rate limiting and request validation

4. **Infrastructure Module**
   - Repository implementations
   - Database access and ORM configuration
   - External service integrations (LLM, etc.)
   - Caching, logging, and other cross-cutting concerns

5. **Main Module**
   - Application bootstrap and configuration
   - Dependency injection setup
   - Environment-specific configurations
   - Main application entry point

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **Database**: MySQL (for system data), support for MySQL and DB2 as data sources
- **ORM**: MyBatis
- **Caching**: Redis
- **Build Tool**: Maven
- **API Documentation**: Swagger/OpenAPI
- **Testing**: JUnit, Mockito
- **Frontend Prototype**: HTML + Tailwind CSS + FontAwesome

## Core Components

### Data Source Management

![Data Source Management](https://via.placeholder.com/800x400?text=Data+Source+Management)

#### Components:
- **DataSourceRegistry**: Central registry for all configured data sources
- **MetadataExtractor**: Extracts schema, table, and column metadata from data sources
- **MetadataSynchronizer**: Manages the synchronization process for metadata
- **ConnectionManager**: Handles database connections and connection pooling
- **CredentialManager**: Securely stores and retrieves database credentials

#### Key Features:
- Support for MySQL and DB2 databases
- Secure credential storage with salted AES encryption
- Incremental metadata synchronization
- Connection health monitoring
- Extensible design for adding new data source types

### Intelligent Data Discovery

![Intelligent Data Discovery](https://via.placeholder.com/800x400?text=Intelligent+Data+Discovery)

#### Components:
- **MetadataExplorer**: Interface for browsing available data structures
- **QueryBuilder**: SQL query construction and execution
- **NaturalLanguageProcessor**: Converts natural language to SQL using LLM
- **RelationshipManager**: Manages table relationships (manual and inferred)
- **RelationshipInferenceEngine**: Automatically infers relationships between tables
- **QueryHistoryManager**: Tracks and manages query history

#### Key Features:
- Hierarchical metadata navigation
- SQL query editor with syntax highlighting
- Natural language query processing via OpenRouter LLM integration
- Relationship inference from query patterns and naming conventions
- Query history and favorites
- Query versioning

### Low-Code Integration

![Low-Code Integration](https://via.placeholder.com/800x400?text=Low-Code+Integration)

#### Components:
- **APIGenerator**: Creates API endpoints from saved queries
- **UIConfigurationManager**: Manages display configurations for queries
- **DisplayTemplateEngine**: Renders query results according to templates
- **ParameterConfigurationManager**: Manages query parameter configurations
- **MaskingEngine**: Applies data masking rules to sensitive data

#### Key Features:
- JSON-based integration protocol for low-code platforms
- Configurable display templates for different data types
- Parameter form generation
- Sensitive data masking
- API versioning
- User preference tracking

## Database Design

### Core Tables

1. **tbl_data_source**
   - Primary table for data source configuration
   - Stores connection details and metadata

2. **tbl_metadata_schema**
   - Stores schema information for each data source

3. **tbl_metadata_table**
   - Stores table metadata
   - Links to schema and data source

4. **tbl_metadata_column**
   - Stores column metadata
   - Links to tables

5. **tbl_relationship**
   - Stores relationships between tables
   - Includes both manual and inferred relationships

6. **tbl_query**
   - Stores saved queries
   - Includes SQL, natural language description, and metadata

7. **tbl_query_version**
   - Stores versions of queries
   - Enables tracking changes over time

8. **tbl_query_history**
   - Tracks query execution history
   - Stores execution time, user, and status

9. **tbl_api_endpoint**
   - Stores API endpoint configurations
   - Links to queries

10. **tbl_ui_configuration**
    - Stores UI display configurations
    - Includes column visibility, formatting, etc.

11. **tbl_user_preference**
    - Stores user-specific preferences
    - Includes frequently used parameters, display settings, etc.

### Database Schema Diagram

![Database Schema](https://via.placeholder.com/800x600?text=DataScope+Database+Schema)

## API Design

### RESTful API Endpoints

#### Data Source Management
- `GET /api/v1/datasources` - List all data sources
- `GET /api/v1/datasources/{id}` - Get data source details
- `POST /api/v1/datasources` - Create new data source
- `PUT /api/v1/datasources/{id}` - Update data source
- `DELETE /api/v1/datasources/{id}` - Delete data source
- `POST /api/v1/datasources/{id}/sync` - Trigger metadata sync

#### Metadata Exploration
- `GET /api/v1/metadata/datasources/{id}/schemas` - List schemas
- `GET /api/v1/metadata/schemas/{id}/tables` - List tables in schema
- `GET /api/v1/metadata/tables/{id}/columns` - List columns in table
- `GET /api/v1/metadata/search` - Search metadata

#### Query Management
- `POST /api/v1/queries/execute` - Execute ad-hoc query
- `POST /api/v1/queries/natural-language` - Execute natural language query
- `GET /api/v1/queries` - List saved queries
- `GET /api/v1/queries/{id}` - Get query details
- `POST /api/v1/queries` - Save new query
- `PUT /api/v1/queries/{id}` - Update query
- `GET /api/v1/queries/history` - Get query history
- `GET /api/v1/queries/{id}/download` - Download query results as CSV

#### Relationship Management
- `GET /api/v1/relationships` - List relationships
- `POST /api/v1/relationships` - Create relationship
- `PUT /api/v1/relationships/{id}` - Update relationship
- `DELETE /api/v1/relationships/{id}` - Delete relationship
- `GET /api/v1/relationships/inferred` - List inferred relationships
- `POST /api/v1/relationships/inferred/{id}/approve` - Approve inferred relationship

#### Low-Code Integration
- `GET /api/v1/lowcode/apis` - List generated APIs
- `POST /api/v1/lowcode/apis` - Generate new API
- `GET /api/v1/lowcode/apis/{id}` - Get API details
- `PUT /api/v1/lowcode/apis/{id}` - Update API
- `DELETE /api/v1/lowcode/apis/{id}` - Delete API
- `GET /api/v1/lowcode/ui-configs` - List UI configurations
- `POST /api/v1/lowcode/ui-configs` - Create UI configuration
- `PUT /api/v1/lowcode/ui-configs/{id}` - Update UI configuration

### Low-Code Integration Protocol

The system will use a JSON-based protocol for integration with low-code platforms:

```json
{
  "apiEndpoint": "/api/v1/generated/query123",
  "version": "1.0",
  "queryConfig": {
    "id": "query123",
    "name": "Customer Orders",
    "description": "Retrieves customer orders with details"
  },
  "parameters": [
    {
      "name": "customerId",
      "label": "Customer ID",
      "type": "string",
      "required": true,
      "defaultValue": null,
      "componentType": "text-input"
    },
    {
      "name": "startDate",
      "label": "Start Date",
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
        "label": "Order ID",
        "type": "string",
        "sortable": true,
        "filterable": true,
        "visible": true,
        "componentType": "text",
        "width": "100px"
      },
      {
        "name": "order_date",
        "label": "Order Date",
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
        "label": "Credit Card",
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
        "label": "View",
        "icon": "eye",
        "endpoint": "/api/v1/orders/{order_id}"
      },
      {
        "name": "edit",
        "label": "Edit",
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

## Security Considerations

- **Authentication**: Integration with existing authentication systems
- **Authorization**: Role-based access control for data sources and queries
- **Data Protection**: 
  - Sensitive data masking
  - Encrypted credential storage (salted AES)
  - Audit logging for sensitive operations
- **API Security**:
  - Rate limiting
  - Request validation
  - API key management
- **Query Safety**:
  - SQL injection prevention
  - Query timeout (30 seconds)
  - Resource usage limits

## Performance Considerations

- **Caching Strategy**:
  - Redis for metadata caching
  - Query result caching with appropriate TTL
- **Query Optimization**:
  - Execution plan analysis
  - Index recommendation
  - Query parameter validation
- **Connection Pooling**:
  - Efficient connection management
  - Connection timeout handling
- **Rate Limiting**:
  - Per-user query frequency limits
  - Gradual throttling approach

## Extensibility

The system is designed to be extensible in several key areas:

1. **Data Source Types**:
   - Pluggable architecture for adding new database types
   - Abstraction layer for database-specific operations

2. **UI Components**:
   - Extensible mapping between data types and UI components
   - Custom component registration

3. **Natural Language Processing**:
   - Pluggable LLM providers
   - Customizable prompt templates

4. **Export Formats**:
   - Beyond CSV to support other formats in the future

## User Interface Prototypes

### Data Source Management Screen

![Data Source Management UI](https://via.placeholder.com/800x600?text=Data+Source+Management+UI)

### Metadata Explorer

![Metadata Explorer UI](https://via.placeholder.com/800x600?text=Metadata+Explorer+UI)

### Query Builder

![Query Builder UI](https://via.placeholder.com/800x600?text=Query+Builder+UI)

### Natural Language Query

![Natural Language Query UI](https://via.placeholder.com/800x600?text=Natural+Language+Query+UI)

### Low-Code Configuration

![Low-Code Configuration UI](https://via.placeholder.com/800x600?text=Low-Code+Configuration+UI)

## Implementation Plan

1. **Phase 1: Core Infrastructure**
   - Basic project setup
   - Database schema design
   - Data source connection management
   - Metadata extraction

2. **Phase 2: Query Capabilities**
   - SQL query execution
   - Query history
   - Basic result display
   - Query saving and versioning

3. **Phase 3: Intelligent Features**
   - Natural language processing integration
   - Relationship inference
   - Advanced metadata exploration

4. **Phase 4: Low-Code Integration**
   - API generation
   - UI configuration
   - Display template engine
   - Integration protocol implementation

5. **Phase 5: Advanced Features**
   - Data masking
   - Performance optimization
   - Advanced security features
   - User preference learning