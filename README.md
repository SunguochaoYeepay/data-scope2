# DataScope - 数据查询和探索平台

DataScope是一个强大的数据查询和探索平台，旨在帮助研发人员、数据分析师和业务人员快速高效地查询和分析数据。

## 功能特性

### 数据源管理

- 支持MySQL、DB2等多种数据库系统
- 自动化元数据提取和同步
- 数据源健康监控
- 密码加密存储

### 智能数据发现

- 直观的数据浏览界面
- 自然语言查询支持
- 可视化SQL查询构建器
  - 表关系可视化选择
  - 字段和条件灵活配置
  - 支持嵌套条件组
  - 高级表达式编辑器
    - CASE WHEN条件判断
    - 聚合、字符串、日期等SQL函数支持
    - 数学运算表达式
    - 自定义SQL表达式
  - 排序和分组管理
  - 预览和复制生成的SQL
  - 临时查询管理
    - 查询历史记录
    - 本地存储与快速访问
- 查询执行管理
  - 可视化查询结果展示
  - 查询执行状态实时反馈
  - 查询取消功能
  - 友好的错误处理和提示
- 智能表关系推断
- SQL查询优化建议
- **统一查询体验**：提供 SQL 编辑器、自然语言查询和可视化查询构建器三种查询方式，满足不同用户需求
- **优化加载反馈**：全屏加载指示器提供清晰的查询执行状态，包含执行时间和取消功能
- **查询管理**：支持查询历史记录和保存功能，方便重复使用常用查询

### 系统集成

- 提供表单和表格两种集成方式
- 可视化配置编辑器
  - 表单配置：支持多种布局、字段类型和验证规则
  - 表格配置：支持列显示、排序、过滤和操作按钮
    - 高级列过滤条件配置，可为每列设置多种过滤条件
    - 批量操作功能，支持对多条记录进行批量处理
    - 数据聚合与分组展示，支持按字段分组和多种聚合函数
    - 高级筛选器保存功能，支持复杂条件组合和默认筛选器设置
    - 全面的数据导出配置，支持多种格式和行数限制
- 支持与外部系统集成
  - URL集成：自定义HTTP请求和响应处理
  - 表单提交：与系统内表单集成
- 数据导出功能
  - 支持CSV、Excel格式导出
  - 自定义导出列和格式配置
- 完整的集成生命周期管理
  - 创建、编辑、预览、激活和停用
  - 版本控制和历史记录
- 低代码开发支持
  - 标准化的API接口
  - 配置导入导出功能

### 低代码集成

- 标准化集成协议
- 灵活的界面配置
- AI辅助配置生成
- 多种展示形式支持

## 技术栈

### 后端技术

- Java 8+
- Spring Boot 2.7+
- MyBatis 3.5+
- Redis 6.0+

### 前端技术

- Vue 3
- Tailwind CSS
- FontAwesome
- TypeScript

### 数据存储

- MySQL 8.0+
- Redis缓存

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+

### 安装步骤

1. 克隆项目
```bash
git clone https://github.com/yourusername/data-scope.git
cd data-scope
```

2. 配置数据库

```sql
create database data_scope;
```

3. 修改配置
```bash
cp src/main/resources/application.example.yml src/main/resources/application.yml
# 编辑application.yml配置数据库连接信息
```

4. 编译打包
```bash
mvn clean package
```

5. 运行应用
```bash
java -jar data-scope-main/target/data-scope.jar
```

### 访问系统

- 后端访问地址：http://localhost:8081
- 前端开发服务器：http://localhost:3000
- 集成部署访问：http://localhost:8081
- 默认用户名：admin
- 默认密码：admin123

### 多环境访问支持

系统支持在不同端口和基础路径下运行，路由已优化以支持以下场景：

- 开发环境：前端单独运行于http://localhost:3000
- 集成部署：静态资源打包到后端访问于http://localhost:8081
- 代理环境：在任意基础路径下运行（如/datascope/）

## 项目结构

```
data-scope/
├── data-scope-app/          # 应用层
├── data-scope-domain/       # 领域层
├── data-scope-facade/       # 门面层
├── data-scope-infrastructure/# 基础设施层
├── data-scope-main/         # 启动模块
├── docs/                    # 项目文档
└── README.md               # 项目说明
```

## 文档说明

- [用户故事](docs/user_stories.md)
- [数据库设计](docs/database_design.md)
- [系统架构](docs/architecture.md)
- [API设计](docs/api_design.md)
- [低代码集成](docs/lowcode_integration.md)

## 开发指南

### 代码规范

- 遵循DDD架构设计
- 采用Java代码规范
- 使用统一的命名规则
- 编写完整的注释

### 提交规范

- feat: 新功能
- fix: 修复问题
- docs: 文档变更
- style: 代码格式
- refactor: 代码重构
- test: 测试相关
- chore: 其他修改

### 分支管理

- main: 主分支
- develop: 开发分支
- feature/*: 特性分支
- hotfix/*: 紧急修复分支

## 测试

### 单元测试
```bash
mvn test
```

### 集成测试
```bash
mvn verify
```

## 部署

### Docker部署
```bash
docker build -t data-scope .
docker run -p 8080:8080 data-scope
```

### 配置说明

- application.yml: 应用配置
- logback-spring.xml: 日志配置
- docker-compose.yml: 容器编排

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交修改
4. 推送到分支
5. 创建Pull Request

## 许可证

[MIT License](LICENSE)

## 联系方式

- 项目负责人：Your Name
- 邮箱：your.email@example.com
- 问题反馈：GitHub Issues