---
name: aggregate-root-module-draft-skill
description: Use when triaging placeholder DDD aggregate skills for modules that do not yet have production-ready current-code anchors.
---

# DDD Skill Draft: AggregateRoot_Module_Draft_Skill

## Status

这是 ERP、IOT、MES、MP、Report 等模块旧占位 skill 的统一草稿，不是生产级重构指南。

使用它之前必须先阅读并满足 `DDD_Skill_Production_Readiness_Standard.md`。未补齐事实源路径、字段映射、错误码、事务边界、外部契约和验证命令前，禁止按本草稿直接改生产代码。

## Applies To

当前用于收敛以下重复占位文件：

- `AggregateRoot_Erp_Skill.md`
- `AggregateRoot_Iot_Skill.md`
- `AggregateRoot_Mes_Skill.md`
- `AggregateRoot_Mp_Skill.md`
- `AggregateRoot_Report_Skill.md`

这些模块需要按实际业务边界分别升级为独立、可复现、可验证的生产级 DDD skill，而不是共享同一份通用模板。

## Shared Draft Checklist

### 聚合根

- 每个真实业务实体或一致性边界对应一个聚合根。
- 聚合根封装业务规则、状态流转和生命周期。
- 聚合根不得依赖 Spring、MyBatis、Mapper、Controller VO 或外部 API client。

### 值对象

- 使用不可变类型表达标识、名称、状态、金额、数量等领域概念。
- 值对象构造时自校验，不暴露 setter。

### 仓储接口

- 每个聚合根只依赖 domain 层仓储接口。
- 仓储接口定义在 domain 层，具体实现放在 infrastructure 层。

### 领域服务

- 跨聚合规则或不属于单个聚合根的领域决策放入领域服务。
- 外部系统调用、事务编排、DTO 转换不放入领域服务。

## Upgrade Requirements

升级任一模块专属 skill 时，必须补齐：

1. 当前代码事实源路径：Controller、VO/DTO、DO、Mapper、Service、Convert、ErrorCode、测试。
2. 固定数据模型：DO/DTO/VO/Domain 字段、类型、nullable/default、映射关系。
3. 方法签名：聚合、工厂、仓储、应用服务的目标签名。
4. 业务规则：编号、来源文件、所属层、验证方式。
5. 错误码契约：场景、错误码、参数顺序、抛出层级。
6. 事务与集成契约：缓存、MQ、Job、租户、权限、第三方 API 是否必须保持。
7. 验收标准和 Maven compile/test 命令。
8. Red Flags、Rollback Conditions、AI Self-Check。

## Acceptance Criteria For Upgrading A Module Skill

- 模块专属 skill 不再引用本草稿作为唯一依据。
- 模块专属 skill 能让无上下文 AI 根据当前代码事实复现重构边界。
- Controller 路径、API/CommonApi/Feign 契约、权限、租户、错误码、DTO 字段不被隐式改变。
- 编译和必要测试命令明确可执行。
