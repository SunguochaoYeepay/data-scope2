## 技术上下文

### 使用的技术

* Java
* Maven
* SpringBoot
* MyBatis
* MySQL
* Redis
* OpenRouter（用于 LLM 集成）
* HTML
* Tailwind CSS
* FontAwesome（或其他开源 UI 组件库）

### 开发环境设置

项目使用 Maven 进行依赖管理和构建。开发环境应包括：

* JDK 17 或更高版本
* Maven 3.6 或更高版本
* MySQL 服务器
* Redis 服务器
* 合适的 IDE（例如 IntelliJ IDEA、Eclipse）

### 技术约束

* 系统不应从数据源同步数据，只同步元数据。
* 密码应使用盐和 AES 加密。
* 访问控制由单独的系统处理。
* 系统应易于扩展，以支持更多的数据源类型。
* 系统应提供统一的 API 用于查询数据。
* SQL 和 API 应进行版本控制。
* API 接口应考虑低代码平台集成。
* 每个 API 接口应有 30 秒的默认超时时间。
* 用户查询频率应受到限制。
* 数据下载应限制为 50000 行。
* 应根据配置的规则对敏感信息应用数据掩码。
* 查询执行应被跟踪和管理，具有适当的状态处理。
* 自然语言查询应使用 OpenRouter 的 LLM 接口转换为 SQL。
