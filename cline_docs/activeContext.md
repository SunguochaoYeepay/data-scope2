## Active Context

### What you're working on now?

完成了数据掩码功能的实现，包括：

- MaskType 枚举定义，支持多种掩码类型
- DataMasker 接口设计，支持基本和自定义掩码
- MaskOptions 配置类，用于自定义掩码配置
- DataMaskerImpl 实现类，实现所有掩码功能
- 完整的单元测试覆盖

### Recent changes?

* 实现了完整的数据掩码功能：
  * 基本掩码（NONE, FULL）
  * 定向掩码（LEFT, RIGHT, MIDDLE）
  * 格式特定掩码（EMAIL, PHONE, ID_CARD, BANK_CARD）
  * 自定义掩码模式
* 添加了全面的单元测试
* 更新了 progress.md 以反映最新进展

### Next steps?

1. 实现查询执行引擎的核心功能：
  * SQL 执行逻辑
  * 查询结果处理
  * 结果导出功能
2. 开发低代码平台集成接口
3. 实现 AI 辅助功能
4. 完善安全特性
