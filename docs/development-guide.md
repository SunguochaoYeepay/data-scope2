# DataScope Development Guide

## Development Environment Setup

### Required Software
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Git
- IDE (IntelliJ IDEA or Eclipse)
- Docker (optional)

### IDE Configuration
1. Install Lombok plugin
2. Enable annotation processing
3. Set Java compiler to 17
4. Configure code style (provided in `.editorconfig`)

### Project Structure
```
data-scope/
├── app/          # Application layer
├── data-scope-domain/       # Domain layer
├── data-scope-facade/       # Interface layer
├── data-scope-infrastructure/  # Infrastructure layer
├── data-scope-main/         # Main application
├── docs/                    # Documentation
└── pom.xml                  # Parent POM
```

## Development Guidelines

### Code Style

#### Naming Conventions
- Classes: PascalCase
- Methods/Variables: camelCase
- Constants: UPPER_SNAKE_CASE
- Packages: lowercase
- Database: snake_case

#### Class Structure
```java
public class ClassName {
    // Constants
    private static final String CONSTANT_NAME = "value";

    // Static fields
    private static AtomicInteger counter;

    // Instance fields
    private final DependencyType dependency;
    private String field;

    // Constructors
    public ClassName(DependencyType dependency) {
        this.dependency = dependency;
    }

    // Public methods
    public void methodName() {
        // Implementation
    }

    // Private methods
    private void helperMethod() {
        // Implementation
    }
}
```

### DDD Implementation

#### Domain Layer
```java
// Entity
@Entity
public class DataSource extends BaseEntity {
    @Getter
    private final DataSourceId id;
    private String name;
    private DataSourceType type;

    public void updateName(String newName) {
        Assert.hasText(newName, "Name cannot be empty");
        this.name = newName;
    }
}

// Value Object
@Value
public class DataSourceId {
    String value;

    public static DataSourceId generate() {
        return new DataSourceId(UUID.randomUUID().toString());
    }
}

// Repository Interface
public interface DataSourceRepository {
    DataSource save(DataSource dataSource);
    Optional<DataSource> findById(DataSourceId id);
    List<DataSource> findAll();
}
```

#### Application Layer
```java
@Service
@Transactional
public class DataSourceApplicationService {
    private final DataSourceRepository repository;
    private final DomainEventPublisher eventPublisher;

    public DataSourceDTO create(CreateDataSourceCommand command) {
        DataSource dataSource = new DataSource(
            DataSourceId.generate(),
            command.getName(),
            command.getType()
        );
        
        repository.save(dataSource);
        eventPublisher.publish(new DataSourceCreatedEvent(dataSource));
        
        return DataSourceMapper.INSTANCE.toDTO(dataSource);
    }
}
```

#### Infrastructure Layer
```java
@Repository
public class MyBatisDataSourceRepository implements DataSourceRepository {
    private final DataSourceMapper mapper;

    @Override
    public DataSource save(DataSource dataSource) {
        DataSourceDO dataSourceDO = DataSourceConverter.toDO(dataSource);
        if (dataSourceDO.getId() == null) {
            mapper.insert(dataSourceDO);
        } else {
            mapper.update(dataSourceDO);
        }
        return DataSourceConverter.toDomain(dataSourceDO);
    }
}
```

#### 其他指引

- domain模块不依赖facade模块，因此需要在facade模块中重新定义DTO或者枚举，在app模块中实现facade和domain的转换
- domain 模块不能直接调用 infrastructure 模块中的类，如果需要这样调用时可以这么实现：在domain 模块中提供 Gateway
  接口，然后在infrastructure 模块实现。比如在domain 模块提供一个PasswordEncryptorGateway接口，在infrastructure 模块实现改接口。

### Testing Guidelines

#### Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class DataSourceServiceTest {
    @Mock
    private DataSourceRepository repository;
    
    @InjectMocks
    private DataSourceService service;
    
    @Test
    void shouldCreateDataSource() {
        // Given
        CreateDataSourceCommand command = new CreateDataSourceCommand("test", DataSourceType.MYSQL);
        
        // When
        DataSourceDTO result = service.create(command);
        
        // Then
        assertThat(result).isNotNull();
        verify(repository).save(any(DataSource.class));
    }
}
```

#### Integration Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class DataSourceControllerIT {
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void shouldCreateDataSource() throws Exception {
        // Given
        CreateDataSourceRequest request = new CreateDataSourceRequest("test", "MYSQL");
        
        // When/Then
        mockMvc.perform(post("/api/v1/datasources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("test"));
    }
}
```

### Error Handling
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        ErrorResponse error = new ErrorResponse(
            ex.getErrorCode(),
            ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
```

### Logging
```java
@Slf4j
public class ServiceClass {
    public void methodName() {
        log.debug("Debug message with {}", value);
        log.info("Info message");
        log.error("Error message", exception);
    }
}
```

## Development Workflow

### Git Workflow
1. Create feature branch from develop
```bash
git checkout develop
git pull
git checkout -b feature/feature-name
```

2. Make changes and commit
```bash
git add .
git commit -m "feat: description"
```

3. Push and create pull request
```bash
git push origin feature/feature-name
```

### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Code style changes
- refactor: Code refactoring
- test: Adding tests
- chore: Build process or tools

### Code Review Checklist
- [ ] Code follows style guide
- [ ] Unit tests are present
- [ ] Integration tests are present
- [ ] Documentation is updated
- [ ] No unnecessary dependencies
- [ ] Error handling is proper
- [ ] Logging is appropriate
- [ ] Performance considerations
- [ ] Security considerations

## Build and Deploy

### Local Development
```bash
# Run tests
mvn clean test

# Run application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Package application
mvn clean package
```

### Docker Build
```bash
# Build image
docker build -t datascope:latest .

# Run container
docker run -p 8080:8080 datascope:latest
```

### Database Migration
```bash
# Create new migration
mvn flyway:create -Dflyway.locations=db/migration -Dflyway.configFiles=flyway.conf

# Run migration
mvn flyway:migrate
```

## Performance Guidelines

### Query Optimization
- Use indexes appropriately
- Limit result sets
- Use pagination
- Optimize joins
- Cache frequently accessed data

### Caching Strategy
```java
@Cacheable(value = "dataSource", key = "#id")
public DataSourceDTO findById(String id) {
    return repository.findById(id)
        .map(mapper::toDTO)
        .orElseThrow(() -> new NotFoundException("DataSource not found"));
}
```

### Connection Pool Configuration
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1200000
```

## Security Guidelines

### Password Handling
```java
@Component
public class PasswordEncryptor {
    public String encrypt(String password, String salt) {
        // Implementation using AES
    }
    
    public boolean verify(String password, String encryptedPassword, String salt) {
        // Implementation
    }
}
```

### Input Validation
```java
@Validated
@RestController
public class DataSourceController {
    @PostMapping("/datasources")
    public ResponseEntity<DataSourceDTO> create(
        @Valid @RequestBody CreateDataSourceRequest request
    ) {
        // Implementation
    }
}
```

### API Security
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/v1/**").authenticated()
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
