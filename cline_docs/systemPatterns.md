## System Patterns

### 整体架构

```mermaid
graph TB
    User((User))
    
    subgraph "DataScope System"
        subgraph "Frontend Container"
            WebUI["Web Interface<br>(HTML/JavaScript)"]
            
            subgraph "Frontend Components"
                DataSourceUI["Data Source UI<br>(HTML/JS)"]
                QueryUI["Query UI<br>(HTML/JS)"]
                MetadataUI["Metadata UI<br>(HTML/JS)"]
                SettingsUI["Settings UI<br>(HTML/JS)"]
            end
        end
        
        subgraph "Application Container"
            AppServer["Application Server<br>(Spring Boot)"]
            
            subgraph "Application Components"
                DataSourceController["Data Source Controller<br>(Spring MVC)"]
                QueryController["Query Controller<br>(Spring MVC)"]
                UserDisplayController["User Display Controller<br>(Spring MVC)"]
                GlobalExceptionHandler["Exception Handler<br>(Spring)"]
            end
        end
        
        subgraph "Domain Container"
            DomainLayer["Domain Layer<br>(Java)"]
            
            subgraph "Domain Components"
                DataSourceService["Data Source Service<br>(Java)"]
                QueryService["Query Service<br>(Java)"]
                QueryExecutionService["Query Execution Service<br>(Java)"]
                UserDisplayService["User Display Service<br>(Java)"]
                DataMasker["Data Masker<br>(Java)"]
            end
        end
        
        subgraph "Infrastructure Container"
            InfraLayer["Infrastructure Layer<br>(Java)"]
            
            subgraph "Infrastructure Components"
                DataSourceRepo["Data Source Repository<br>(MyBatis)"]
                QueryRepo["Query Repository<br>(MyBatis)"]
                UserDisplayRepo["User Display Repository<br>(MyBatis)"]
                PasswordEncryptor["Password Encryptor<br>(Java)"]
            end
        end
        
        subgraph "Storage Container"
            MySQL[("MySQL Database<br>(MySQL 8.2)")]
            Redis[("Cache<br>(Redis 7.2)")]
        end
        
        subgraph "Monitoring Container"
            Prometheus["Metrics Collector<br>(Prometheus)"]
            Grafana["Monitoring Dashboard<br>(Grafana)"]
        end
    end

    %% Relationships
    User -->|"Uses"| WebUI
    
    %% Frontend relationships
    WebUI -->|"Makes API calls"| AppServer
    DataSourceUI -->|"Manages"| DataSourceController
    QueryUI -->|"Executes queries via"| QueryController
    MetadataUI -->|"Configures via"| UserDisplayController
    
    %% Application relationships
    DataSourceController -->|"Uses"| DataSourceService
    QueryController -->|"Uses"| QueryService
    UserDisplayController -->|"Uses"| UserDisplayService
    
    %% Domain relationships
    DataSourceService -->|"Persists via"| DataSourceRepo
    QueryService -->|"Executes via"| QueryExecutionService
    QueryExecutionService -->|"Uses"| DataMasker
    UserDisplayService -->|"Stores via"| UserDisplayRepo
    
    %% Infrastructure relationships
    DataSourceRepo -->|"Stores in"| MySQL
    QueryRepo -->|"Stores in"| MySQL
    UserDisplayRepo -->|"Stores in"| MySQL
    QueryExecutionService -->|"Caches in"| Redis
    
    %% Monitoring relationships
    AppServer -->|"Reports metrics"| Prometheus
    Prometheus -->|"Visualizes"| Grafana
```

### How the system is built?

The DataScope system is built using a modular, layered architecture with the following modules:

* **data-scope-app:** The application module containing the controllers, DTOs, and API definitions.
* **data-scope-domain:** The domain module containing the core business logic and entities.
* **data-scope-facade:** The facade module providing a simplified interface to the domain layer.
* **data-scope-infrastructure:** The infrastructure module containing the data access layer and external integrations.
* **data-scope-main:** The main module responsible for bootstrapping the application.

The system uses a microservices-inspired architecture, with clear separation of concerns between the modules.

### Key technical decisions?

* **Technology Stack:** Java, Maven, Spring Boot, MyBatis, MySQL, Redis
* **API Protocol:** JSON-based interaction protocol for low-code platform integration
* **Security:** Password encryption with salt and AES
* **Extensibility:** Pluggable architecture for supporting different data source types
* **Scalability:** Versioned APIs to ensure backward compatibility
* **Data Masking:** Comprehensive data masking strategy with multiple masking types and customization options
* **Query Execution:** Stateful query execution with tracking and management capabilities

### Architecture patterns?

* **DDD (Domain-Driven Design):** The domain module follows DDD principles to model the business domain.
* **Repository Pattern:** The infrastructure module uses the repository pattern to abstract data access.
* **Facade Pattern:** The facade module provides a simplified interface to the domain layer.
* **Strategy Pattern:** Used in the data masking implementation to support different masking strategies.
* **Builder Pattern:** Used in model classes like QueryResult and SqlMetadata for flexible object creation.
* **State Pattern:** Used in the QueryExecution model to track and manage query execution states.
* **Microservices-inspired Architecture:** The modular architecture allows for independent deployment and scaling of
  individual modules.
