# Changelog

All notable changes to DataScope will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure setup
- Basic documentation framework
- Maven module configuration
- Docker deployment support
- Monitoring setup with Prometheus and Grafana
- Visual SQL query builder implementation
  - Add TableSelector component for selecting tables and managing joins
  - Add FieldSelector component for selecting and configuring result fields
  - Add ConditionBuilder component for building WHERE conditions
  - Add support for nested condition groups with AND/OR operators
  - Add ExpressionEditor component for creating complex SQL expressions
    - Support for CASE WHEN statements
    - Support for SQL functions (aggregation, string, date, math)
    - Support for mathematical operations
    - Support for custom expressions
  - Update FieldSelector to support expression fields
  - Update QueryBuilder to handle expression fields in generated SQL
  - Add QueryManager component for query saving, loading and history management
  - Add predefined query templates for common query patterns
  - Implement query configuration persistence using local storage
  - Add search and filter functionality for saved queries
  - Integrate query builder with existing query system
  - Add uuid package for generating unique identifiers
  - Add query cancellation feature with improved UI feedback
    - Enhanced cancel button with visual indicators
    - Proper status update across components after cancellation
    - User-friendly error messages for cancelled queries
    - Improved UI for cancelling queries during execution with runtime timer

### Changed
- None

### Deprecated
- None

### Removed
- None

### Fixed
- 修复了查询执行按钮样式和交互问题，提供统一的视觉反馈
- 优化了执行按钮的禁用状态显示，提供更清晰的灰色外观和禁用指针，避免用户误解
- 统一了 SQL 编辑器、自然语言查询和查询构建器的执行按钮视觉反馈样式
- 移除了查询结果区域的冗余错误消息，仅保留右上角的错误消息，改善用户体验
- 提高了加载指示器的层级(z-index)，确保它始终显示在最上层，不会被其他组件覆盖
- 为加载指示器添加了取消按钮，允许用户随时取消正在执行的查询
- 统一了自然语言查询和 SQL 查询的加载体验，提供一致的用户界面
- 移除了重复的加载指示器，解决多个加载状态同时显示的问题
- 完全移除了 QueryEditor 中的自定义加载指示器，只使用全局加载服务提供的指示器，避免重复显示

### Security
- Initial security configuration
- Basic authentication setup
- Password encryption implementation

## [1.0.0] - 2025-03-23

### Added
- Project initialization
- Core module structure
  - Application layer
  - Domain layer
  - Facade layer
  - Infrastructure layer
- Basic documentation
  - README
  - Contributing guidelines
  - Security policy
  - API documentation
  - Architecture design
- Development setup
  - Maven configuration
  - Docker support
  - CI/CD pipeline
- Monitoring
  - Prometheus integration
  - Grafana dashboards
  - Health checks
- Security
  - Authentication framework
  - Authorization setup
  - Data encryption
  - Security guidelines

### Technical Details
- Java 17 support
- Spring Boot 3.2.0
- MyBatis integration
- Redis caching
- MySQL/DB2 support
- Docker containerization
- Prometheus monitoring
- Grafana dashboards

### Documentation
- Architecture documentation
- API documentation
- Security guidelines
- Performance guidelines
- Error handling documentation
- Monitoring setup
- Contributing guidelines

### Development Tools
- Maven build system
- Docker compose setup
- Prometheus configuration
- Grafana dashboards
- Code style configuration

## Types of Changes

### Added
For new features.

### Changed
For changes in existing functionality.

### Deprecated
For soon-to-be removed features.

### Removed
For now removed features.

### Fixed
For any bug fixes.

### Security
In case of vulnerabilities.

## Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

### Types
- feat: New feature
- fix: Bug fix
- docs: Documentation
- style: Formatting
- refactor: Code restructuring
- test: Adding tests
- chore: Maintenance

### Scope
- app: Application layer
- domain: Domain layer
- facade: API layer
- infra: Infrastructure layer
- docs: Documentation
- build: Build system
- ci: CI/CD
- docker: Docker configuration
- monitor: Monitoring setup

### Examples

```
feat(query): add natural language processing support

Implement OpenRouter API integration for converting
natural language to SQL queries.

Closes #123
```

```
fix(datasource): resolve connection pool timeout

Update HikariCP configuration to handle connection
timeouts properly.

Fixes #456
```

```
docs(api): update API documentation

Add detailed examples and improve formatting of
API documentation.

Related to #789
```

## Release Process

1. Version Update
   - Update version in pom.xml
   - Update CHANGELOG.md
   - Create release branch

2. Testing
   - Run all tests
   - Perform integration testing
   - Check documentation

3. Release
   - Create release tag
   - Build release artifacts
   - Update documentation
   - Deploy to production

4. Post-Release
   - Announce release
   - Update version to next snapshot
   - Close milestone
   - Update roadmap

## Versioning

We use [SemVer](http://semver.org/) for versioning:

- MAJOR version for incompatible API changes
- MINOR version for new functionality in a backwards compatible manner
- PATCH version for backwards compatible bug fixes

## Issue References

Issues should be referenced in commit messages using the following format:
- Fixes #123
- Closes #456
- Related to #789

## Contact

For major changes, please open an issue first to discuss what you would like to change.

- Email: support@example.com
- GitHub Issues: https://github.com/example/data-scope/issues
- Slack: #data-scope-dev