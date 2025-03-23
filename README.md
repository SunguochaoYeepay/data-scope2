# DataScope

DataScope is a comprehensive data management and query system that enables seamless integration of multiple database systems, intelligent data discovery, and low-code application development capabilities.

## Features

- **Data Source Management**
  - Support for MySQL and DB2 databases
  - Automated metadata extraction and synchronization
  - Secure credential management with encryption
  - Connection health monitoring

- **Smart Data Discovery**
  - Intuitive schema and table browsing
  - Natural language query support
  - AI-powered relationship inference
  - Query history and favorites

- **Low-Code Integration**
  - JSON-based configuration
  - Flexible display templates
  - Data masking capabilities
  - API version management

## Technology Stack

- Java 17+
- Spring Boot 3.1
- MyBatis
- MySQL 5.7+
- Redis
- Maven

## Project Structure

```
data-scope/
├── data-scope-app/          # Application layer
├── data-scope-domain/       # Domain layer
├── data-scope-facade/       # Interface layer
├── data-scope-infrastructure/# Infrastructure layer
└── data-scope-main/        # Main application
```

## Getting Started

### Prerequisites

- JDK 17 or higher
- Maven 3.8+
- MySQL 5.7+
- Redis 6+

### Configuration

1. Clone the repository:
```bash
git clone https://github.com/your-org/data-scope.git
cd data-scope
```

2. Configure database connection in `application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/data_scope
    username: your_username
    password: your_password
```

3. Configure Redis connection:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

### Building

```bash
mvn clean install
```

### Running

```bash
cd data-scope-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Access the application at: http://localhost:8080/api

Swagger UI: http://localhost:8080/api/swagger-ui/index.html

## Development

### Code Style

- Follow standard Java code conventions
- Use Lombok for reducing boilerplate
- Follow DDD layered architecture
- Write unit tests for business logic

### Database Guidelines

- Table names use `tbl_` prefix
- Use UUID for primary keys
- Include audit fields (created_at, modified_at, etc.)
- Use meaningful index names

### API Guidelines

- RESTful API design
- Proper HTTP methods and status codes
- Comprehensive API documentation
- Version management

## Security

- Passwords are salted and encrypted
- API rate limiting
- Data masking for sensitive information
- Query timeout protection

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.