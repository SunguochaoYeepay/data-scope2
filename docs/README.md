# DataScope

DataScope是一个全面的数据管理和查询系统，允许用户集成不同的数据库系统，探索元数据，使用SQL或自然语言执行查询，并创建低代码集成。

## 功能特性

### 数据源管理
- 连接到MySQL和DB2数据库
- 使用加密进行安全凭证存储
- 自动元数据提取和同步
- 连接健康监控

### 智能数据发现
- 浏览模式、表和列
- 执行带参数支持的SQL查询
- 使用LLM进行自然语言查询处理
- 表之间的自动关系推断
- 手动关系定义

### 低代码集成
- 从查询生成API端点
- 为参数和结果配置UI组件
- 自定义数据显示和格式化
- 掩码敏感数据
- API和查询的版本控制

## 架构

DataScope遵循领域驱动设计(DDD)原则，并组织为以下模块：

- **App**：编排领域操作的应用服务
- **Domain**：核心业务逻辑和领域实体
- **Facade**：API控制器和DTO
- **Infrastructure**：仓储实现和外部服务
- **Main**：应用程序引导和配置

## 技术栈

- **后端**：Java 17, Spring Boot 3.x
- **数据库**：MySQL（用于系统数据），支持MySQL和DB2作为数据源
- **ORM**：MyBatis
- **缓存**：Redis
- **构建工具**：Maven
- **API文档**：Swagger/OpenAPI
- **测试**：JUnit, Mockito, Testcontainers

## 入门指南

### 前提条件

- JDK 17或更高版本
- Maven 3.8+
- MySQL 8.0+
- Redis 6.0+
- Docker（可选，用于容器化开发）

### 开发环境设置

1. **克隆仓库**

```bash
git clone https://github.com/your-organization/data-scope.git
cd data-scope
```

2. **配置数据库**

为DataScope创建MySQL数据库：

```sql
CREATE DATABASE datascope CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'datascope'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON datascope.* TO 'datascope'@'localhost';
FLUSH PRIVILEGES;
```

3. **配置应用属性**

创建`main/src/main/resources/application-dev.yml`文件，包含本地配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/datascope?useSSL=false&serverTimezone=UTC
    username: datascope
    password: your_password
  redis:
    host: localhost
    port: 6379

datascope:
  security:
    encryption-key: your_encryption_key
  llm:
    provider: openrouter
    api-key: your_openrouter_api_key
```

4. **构建项目**

```bash
mvn clean install
```

5. **运行应用**

```bash
cd main
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

应用将在http://localhost:8080可用

### Docker开发环境

或者，您可以使用Docker Compose设置开发环境：

```bash
docker-compose -f docker-compose.dev.yml up -d
```

这将启动MySQL、Redis和其他所需服务。

## 项目结构

```
data-scope/
├── app/                  # 应用服务
├── domain/               # 领域模型和业务逻辑
├── facade/               # API控制器和DTO
├── infrastructure/       # 仓储实现和外部服务
├── main/                 # 应用程序引导和配置
└── docs/                 # 文档
```

## 文档

详细文档可在`docs`目录中找到：

- [用户故事](docs/user_stories.md)
- [系统架构](docs/architecture.md)
- [数据库设计](docs/database_design.md)
- [API设计](docs/api_design.md)
- [UI原型](docs/ui_prototypes.md)
- [实施计划](docs/implementation_plan.md)
- [技术需求](docs/technical_requirements.md)
- [测试策略](docs/testing_strategy.md)
- [安全考虑](docs/security_considerations.md)

## API文档

当应用运行时，API文档可在以下位置获取：

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 开发指南

### 编码标准

- 遵循Java编码标准
- 为类、方法和变量使用有意义的名称
- 为所有公共类和方法添加Javadoc注释
- 为所有业务逻辑编写单元测试

### Git工作流

- 使用功能分支进行开发
- 创建拉取请求进行代码审查
- 合并时压缩提交
- 编写有意义的提交消息

### 测试

- 为所有业务逻辑编写单元测试
- 为仓储实现使用集成测试
- 在提交拉取请求前运行所有测试
- 保持最低80%的代码覆盖率

## 贡献

1. Fork仓库
2. 创建功能分支（`git checkout -b feature/amazing-feature`）
3. 提交更改（`git commit -m 'Add some amazing feature'`）
4. 推送到分支（`git push origin feature/amazing-feature`）
5. 打开拉取请求

## 许可证

本项目根据[MIT许可证](LICENSE)授权。

## 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis](https://mybatis.org/mybatis-3/)
- [Redis](https://redis.io/)
- [OpenRouter](https://openrouter.ai/)
- [Tailwind CSS](https://tailwindcss.com/)
- [FontAwesome](https://fontawesome.com/)