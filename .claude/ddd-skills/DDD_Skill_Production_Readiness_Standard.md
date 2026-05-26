---
name: ddd-skill-production-readiness-standard
description: Use when auditing, writing, or upgrading DDD aggregate skills for production use, full project reproduction, or current-code consistency in this repository.
---

# DDD Skill Production Readiness Standard

未满足本标准的 `AggregateRoot_*_Skill.md` 只能作为草稿参考，不能直接指导生产级重构。

## Mandatory Contract

生产级聚合 skill 必须包含：

- YAML frontmatter：`name` 小写连字符；`description` 以 `Use when...` 开头。
- Overview、When to Use、When Not to Use。
- Reproducibility Contract：先读事实源；冲突时当前可编译外部行为优先；禁止猜字段、错误码、事务、映射。
- AI Execution Contract：Scope、Must Read、Must Preserve、Allowed Changes、Forbidden Changes、Dependency Rules、Verification Gate、Stop Conditions。
- Current Source Anchors：Controller、VO/DTO、DO、Mapper、Convert、Service/Application、Repository、ErrorCode、测试路径。
- Standard Skeleton Contract：`domain/{aggregate}/model,valueobject,event,service,repository`；`application/{aggregate}/command,query,dto|result,port/inbound,port/outbound,service`；`infrastructure/{aggregate}/persistence,external,rpc,cache,messaging`。
- Fixed Data Model：DO/DTO/VO/Domain 字段、类型、含义、nullable/default、映射。
- Method Signatures：聚合、工厂、仓储、应用服务必须签名。
- Business Rules、Error Code Contract、Transaction Contract、Integration Contract、Mapping Rules。
- Acceptance Criteria：架构、业务行为、编译/测试。
- Verification Commands、Quick Reference、Common Mistakes、Rationalization Table、Red Flags、Rollback Conditions、AI Self-Check。

## External Contract Must Preserve

不得擅改：Controller 路径/方法、请求/响应 VO、CommonApi/Feign/RPC、权限/租户/数据权限注解、错误码和参数顺序、Excel、分页、OpenAPI/Swagger、缓存、MQ、Job、第三方 API。需要改变外部契约时，先写单独迁移计划。

## Current-Code Conflict Rule

当 skill 与当前代码冲突：停止实现 → 读事实源 → 记录字段/方法/错误码/事务/API/测试冲突 → 以当前可编译外部行为为准 → 先修 skill 再实现。禁止用文档覆盖生产行为。

## Pressure Tests

升级 skill 前做 RED 压力测试：

| 场景 | 必须暴露的问题 |
|---|---|
| 赶时间直接写代码 | 是否会猜字段、签名、错误码、事务 |
| 无上下文 AI 复现 | 是否缺事实源、骨架、映射、测试、验收 |
| 简单 CRUD | 是否会省略 `port/inbound`、`port/outbound`、`infrastructure/*` |
| 生产上线 | 是否保护 API、权限、租户、缓存、MQ、Job |
| 代码与 skill 不一致 | 是否先修 skill 而不是覆盖代码 |

失败点必须写入 Baseline Failure Findings、Rationalization Table、Red Flags。

## Upgrade Order

优先：Pay/MallTrade/MallPromotion/BPM → Infra/System User/Role/Menu → MallProduct/MemberLevel/CRM/WMS/AI → ERP/IOT/MES/MP/Report/Mall/MemberUser 草稿。每次只升级一个聚合或小子域。

## Quick Reference

| 缺失项 | 结论 |
|---|---|
| YAML frontmatter | 非生产级 |
| Current Source Anchors | 无法复现 |
| Standard Skeleton Contract | 目录/端口/适配器不一致 |
| 字段映射表 | DO/DTO/Domain 易偏差 |
| 错误码契约 | 前端/调用方错误处理易回归 |
| 事务边界 | 跨聚合编排不一致 |
| 测试路径和命令 | 不能证明生产可用 |
| 冲突处理规则 | 易用文档覆盖现有行为 |
| AI Execution Contract | AI 易跨范围或跳过验证 |
| Red Flags | 压力下易偷懒误改 |

## Red Flags / Rollback

出现以下情况，skill 仍是草稿或必须回滚补强：只有通用 DDD 描述；不写完整路径；不写 Controller/VO/CommonApi 外部契约；覆盖多个复杂聚合但无边界；没说明缓存/MQ/Job/租户/权限；干净 AI 仍需猜字段、错误码、事务、映射、文件路径、测试命令，或会先写代码后补验证。
