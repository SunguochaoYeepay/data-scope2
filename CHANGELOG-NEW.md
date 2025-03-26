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
- 查询选择器增强功能
  - 支持查询参数配置
  - 添加数据预览功能 
  - 参数验证和默认值设置
  - 分页预览查询结果

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
- 表格配置编辑器功能增强
  - 支持列过滤条件配置
  - 支持批量操作设置
  - 支持数据聚合与分组显示配置
  - 改进高级筛选器的保存功能
  - 使用选项卡式界面组织配置部分，改善用户体验
- 修复了数据源连接测试时的并发问题
- 解决了某些SQL查询在执行时的超时问题
- 修复了表单提交时的数据验证错误
- 修正了集成编辑界面中的样式问题
- 优化了集成编辑界面，添加了表格配置编辑器的入口

### Security
- 加强了API认证和授权机制
- 实现了敏感数据的加密存储
- 添加了SQL注入防护措施