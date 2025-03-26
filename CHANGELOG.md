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
- 系统集成模块
  - 添加集成类型定义和接口支持
  - 创建表单配置编辑器组件
  - 创建表格配置编辑器组件
  - 创建查询选择器组件
  - 创建集成点配置编辑器组件
  - 实现集成列表、编辑和预览页面
  - 添加集成状态管理和API服务
  - 支持导出数据为CSV和Excel格式
  - 支持集成点与外部系统交互
  - 添加表单验证和错误处理机制
- 数据源管理功能，支持关系型数据库连接
- 查询编辑器，支持SQL查询和可视化查询构建
- 系统集成模块的基础实现
- 表单配置编辑器的基本字段配置功能
- 表格配置编辑器的基本列与操作配置功能
- 集成点编辑器的基本HTTP端点配置
- Markdown文档查看器

### Changed
- 优化项目结构
  - 将前端项目(webview-ui)移至根目录，便于独立开发和管理
  - 更新Maven配置，添加frontend-dev profile用于开发环境
  - 优化Vite配置，改进开发服务器代理设置
  - 统一前后端开发工作流程

### Deprecated
- None

### Removed
- None

### Fixed
- 修复了Vue组件中的CDATA标签问题，解决组件编译错误
  - 修复了QuerySelectorEnhanced.vue、QueryParamsConfig.vue和QueryPreview.vue文件中的标签问题
  - 确保所有组件能正确编译和显示，解决了"Cannot read properties of undefined (reading 'ns')"错误
- 修复了查询执行按钮样式和交互问题，提供统一的视觉反馈
- 优化了执行按钮的禁用状态显示，提供更清晰的灰色外观和禁用指针，避免用户误解
- 统一了 SQL 编辑器、自然语言查询和查询构建器的执行按钮视觉反馈样式
- 移除了查询结果区域的冗余错误消息，仅保留右上角的错误消息，改善用户体验
- 提高了加载指示器的层级(z-index)，确保它始终显示在最上层，不会被其他组件覆盖
- 为加载指示器添加了取消按钮，允许用户随时取消正在执行的查询
- 统一了自然语言查询和 SQL 查询的加载体验，提供一致的用户界面
- 移除了重复的加载指示器，解决多个加载状态同时显示的问题
- 完全移除了 QueryEditor 中的自定义加载指示器，只使用全局加载服务提供的指示器，避免重复显示
- 修复导航链接在不同环境（端口、基础路径）下的路由问题
  - 增强导航组件以正确处理应用程序基础路径（BASE_URL）
  - 更新路由配置以使用应用程序基础路径创建Web历史记录
  - 在Vite配置中明确定义应用基础路径
- 修复Axios类型导入错误
  - 解决Axios 1.x版本中`AxiosRequestConfig`和`AxiosResponse`类型导入问题
  - 添加兼容类型声明以支持最新版本的Axios
  - 更新请求拦截器以适应新版本Axios中headers的类型变化
- 修复表格示例组件的JSX语法错误
  - 将TableExample.vue中的JSX渲染函数替换为正的Vue插槽模板
  - 修复了导致前端编译失败的语法错误
- 修复查询页面无法显示数据问题
  - 增加query store中缺失的fetchQueryHistory和getFavorites方法
  - 添加正确的状态定义和方法实现
  - 更新Vite代理配置，将API目标指向正确的端口（8081）
- 修复查询编辑器无法加载查询的问题
  - 添加了查询存储中缺失的方法，如`getQuery`、`executeQuery`等
  - 引入了新的状态变量`currentQueryResult`
  - 增强了与查询执行、保存和导出相关的功能
- 修复了集成页面空白问题，原因是Vue模板中直接使用了`import.meta.env.DEV`
- 恢复query.ts文件到之前的正常状态，修复了查询功能的错误
- 增强表格配置编辑器(TableConfigEditor)功能
  - 添加列过滤条件配置支持，允许为每列设置多个过滤操作符和默认值
  - 实现批量操作功能配置，支持对表格数据进行批量处理的操作定义
  - 增加数据聚合和分组展示设置，支持复杂的数据汇总视图配置
  - 优化了高级筛选器保存功能，允许设置默认筛选器和条件组合
  - 更新了集成相关的类型定义，确保所有新特性有正确的类型支持
- 改进集成编辑界面的用户体验
  - 在简化版集成编辑页面添加了明显的表格配置编辑器入口按钮
  - 实现了模态对话框形式的表格配置编辑器，提供完整的配置功能
  - 优化了表格配置的保存流程，确保配置更改能立即生效
  - 增强了集成编辑页面的响应式布局，在不同设备上都能良好显示

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