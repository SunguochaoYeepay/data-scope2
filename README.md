# DataScope

DataScope is a comprehensive data management and query system that enables seamless integration of various database systems and provides intelligent data discovery capabilities through a low-code platform integration.

## Features

### Data Source Management
- Support for MySQL and DB2 databases
- Automated metadata extraction and synchronization
- Secure credential management
- Connection pooling and monitoring
- Health checks and diagnostics

### Metadata Management
- Comprehensive schema discovery
- Automated relationship inference
- Table and column documentation
- Index and constraint tracking
- Change history tracking

### Query Management
- SQL query editor with syntax highlighting
- Natural language query interface
- Visual query builder
- Query versioning
- Execution history

### Low Code Integration
- JSON-based configuration protocol
- Flexible UI component generation
- Query parameter management
- Result display customization
- Data export capabilities

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.8 or higher
- MySQL 8.2 or higher
- Redis 6.0 or higher

### Installation

1. Clone the repository
```bash
git clone https://github.com/yourusername/data-scope.git
cd data-scope
```

2. Build the project
```bash
./mvnw clean install
```

3. Configure application properties
```bash
cp data-scope-app/src/main/resources/application.example.yml data-scope-app/src/main/resources/application.yml
```

4. Start the application
```bash
./mvnw spring-boot:run -pl data-scope-app
```

### Docker Deployment

1. Build Docker image
```bash
docker build -t data-scope .
```

2. Run with Docker Compose
```bash
docker-compose up -d
```

## Project Structure

```
data-scope/
├── data-scope-app/        # Application layer
├── data-scope-domain/     # Domain layer
├── data-scope-facade/     # API layer
├── data-scope-infrastructure/  # Infrastructure layer
├── docs/                  # Documentation
└── ui/                    # UI templates
```

## Documentation

- [Architecture Design](docs/architecture.md)
- [API Documentation](docs/api_versioning.md)
- [Database Schema](docs/database_schema.md)
- [User Stories](docs/user_stories.md)
- [Low Code Protocol](docs/low_code_protocol.md)

## Development

### Build
```bash
./mvnw clean install
```

### Test
```bash
./mvnw test
```

### Code Style
```bash
./mvnw checkstyle:check
```

## Configuration

### Application Properties
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datascope
    username: root
    password: secret

  redis:
    host: localhost
    port: 6379

server:
  port: 8080
```

### Logging
```yaml
logging:
  level:
    root: INFO
    com.datascope.datascope: DEBUG
```

## API Examples

### Add Data Source
```bash
curl -X POST http://localhost:8080/api/v1/datasources \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Production DB",
    "type": "MYSQL",
    "host": "localhost",
    "port": 3306,
    "database": "production",
    "username": "user",
    "password": "password"
  }'
```

### Execute Query
```bash
curl -X POST http://localhost:8080/api/v1/queries/execute \
  -H "Content-Type: application/json" \
  -d '{
    "queryId": "123",
    "parameters": {
      "startDate": "2025-01-01",
      "region": "North"
    }
  }'
```

## Monitoring

### Metrics
- Application metrics available at `/actuator/prometheus`
- Health check at `/actuator/health`
- Database connection status
- Query execution statistics

### Grafana Dashboards
- System metrics dashboard
- Query performance dashboard
- Data source monitoring
- User activity tracking

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Security

For security issues, please read [SECURITY.md](SECURITY.md) and report vulnerabilities as described there.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- MyBatis team for the ORM framework
- OpenAI for natural language processing capabilities
- All contributors who have helped with code and documentation
