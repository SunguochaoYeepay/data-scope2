# Contributing to DataScope

Thank you for your interest in contributing to DataScope! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

This project adheres to the Contributor Covenant code of conduct. By participating, you are expected to uphold this code.

## Development Process

### 1. Fork and Clone
```bash
# Fork the repository on GitHub
git clone https://github.com/your-username/data-scope.git
cd data-scope
git remote add upstream https://github.com/original/data-scope.git
```

### 2. Create a Branch
```bash
git checkout develop
git pull upstream develop
git checkout -b feature/your-feature-name
```

### 3. Development Setup
```bash
# Install dependencies
mvn clean install

# Run the application
cd data-scope-main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Coding Standards

### Java Code Style
- Follow Google Java Style Guide
- Use meaningful variable and method names
- Write clear comments and documentation
- Keep methods focused and concise
- Use appropriate design patterns

### Example
```java
/**
 * Manages data source connections and metadata.
 */
@Service
@Slf4j
public class DataSourceService {
    private final DataSourceRepository repository;
    private final MetadataExtractor metadataExtractor;

    public DataSourceService(
            DataSourceRepository repository,
            MetadataExtractor metadataExtractor) {
        this.repository = repository;
        this.metadataExtractor = metadataExtractor;
    }

    /**
     * Creates a new data source with metadata extraction.
     *
     * @param command The creation command
     * @return Created data source
     * @throws DataSourceException if creation fails
     */
    @Transactional
    public DataSourceDTO create(CreateDataSourceCommand command) {
        log.debug("Creating data source: {}", command);
        // Implementation
    }
}
```

### Commit Messages
Follow the Conventional Commits specification:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

Types:
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Code style changes
- refactor: Code refactoring
- test: Adding tests
- chore: Build process or tools

Example:
```
feat(datasource): add metadata extraction capability

- Implement automatic schema detection
- Add table relationship inference
- Include column type mapping

Closes #123
```

## Testing Guidelines

### Unit Tests
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
        CreateDataSourceCommand command = new CreateDataSourceCommand("test");
        
        // When
        DataSourceDTO result = service.create(command);
        
        // Then
        assertThat(result).isNotNull();
        verify(repository).save(any());
    }
}
```

### Integration Tests
```java
@SpringBootTest
class DataSourceIntegrationTest {
    @Autowired
    private DataSourceService service;
    
    @Test
    void shouldCreateAndRetrieveDataSource() {
        // Given
        CreateDataSourceCommand command = new CreateDataSourceCommand("test");
        
        // When
        DataSourceDTO created = service.create(command);
        DataSourceDTO retrieved = service.findById(created.getId());
        
        // Then
        assertThat(retrieved)
            .isNotNull()
            .extracting(DataSourceDTO::getName)
            .isEqualTo("test");
    }
}
```

## Pull Request Process

1. Update Documentation
- Update README.md if needed
- Add/update API documentation
- Include relevant examples

2. Run Tests
```bash
# Run all tests
mvn clean verify

# Run specific test
mvn test -Dtest=DataSourceServiceTest
```

3. Create Pull Request
- Use the PR template
- Link related issues
- Add screenshots if UI changes
- Describe testing performed

### PR Template
```markdown
## Description
[Describe the changes]

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## How Has This Been Tested?
[Describe test cases]

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Code follows style guidelines
- [ ] All tests passing
```

## Review Process

### Code Review Guidelines
- Check code style compliance
- Verify test coverage
- Review documentation updates
- Validate performance impact
- Check security implications

### Review Checklist
- [ ] Code follows project standards
- [ ] Tests are comprehensive
- [ ] Documentation is complete
- [ ] No security vulnerabilities
- [ ] Performance is acceptable

## Development Tools

### Recommended IDE Setup
- IntelliJ IDEA or Eclipse
- Lombok plugin
- CheckStyle plugin
- SonarLint plugin

### Code Analysis
```xml
<plugin>
    <groupId>org.sonarsource.scanner.maven</groupId>
    <artifactId>sonar-maven-plugin</artifactId>
    <version>${sonar.version}</version>
</plugin>
```

## Documentation

### API Documentation
- Use OpenAPI annotations
- Include example requests/responses
- Document error responses
- Add rate limit information

### Code Documentation
- Add Javadoc for public APIs
- Include usage examples
- Document exceptions
- Explain complex algorithms

## Issue Reporting

### Bug Reports
Include:
- Steps to reproduce
- Expected behavior
- Actual behavior
- Environment details
- Logs/screenshots

### Feature Requests
Include:
- Use case description
- Expected benefits
- Proposed solution
- Alternative approaches

## Community

### Getting Help
- GitHub Issues
- Discussion Forums
- Stack Overflow
- Project Wiki

### Communication Channels
- Mailing List
- Slack Channel
- Discord Server
- Regular Meetings

## License

By contributing to DataScope, you agree that your contributions will be licensed under the project's MIT license.