---
name: module-structure-standard
description: Use when auditing, creating, or refactoring module structure, API contracts, DDD layers, or runtime units in this repository.
---

# Module Structure Standard

统一目标：每个 Maven 运行单元、API 契约、业务聚合都有稳定职责边界、依赖方向和验收方式。

## Use / Do Not Use

必须使用：模块 Maven 结构、API/CommonApi/DTO/Feign/RPC/local/remote、DDD 层、`controller/job/mq/framework/service/dal` 去留、`iot/mall` 收口、聚合 skill/结构计划。

不要作为主依据：单点 bug、配置/SQL/文案/测试断言、只读排查、纯依赖升级；除非影响结构、契约或依赖方向。

## Reproducibility Contract

1. 先识别对象：运行单元、API 契约、DDD 分层或特殊模块。
2. 改聚合前先读生产就绪标准和聚合 skill；skill 不达标先升级。
3. 每次只处理一个模块、运行单元、契约集合或聚合根。
4. 当前可编译外部行为优先：HTTP、权限、租户、数据权限、错误码、DTO、分页、Excel、MQ、Job、缓存、RPC。
5. 标准/skill/spec 与代码冲突时，停止实现，读事实源，先修文档再迁移。
6. 不为“看起来统一”移动代码；移动必须服务职责、依赖、复用、扩展或维护。
7. 完成前执行匹配范围的 Maven compile/test；只改文档时执行结构/占位验证。

## Unified Target

- Maven 按 runtime unit 组织：`server`、必要时 `gateway`。
- API 用一套 `CommonApi` 契约 + `local/remote` 双适配。
- `controller/job/mq/framework` 是入口或技术配置。
- 核心业务进入 `domain/application/infrastructure/convert`。
- `service/dal` 是迁移源，不是核心业务最终承载层。
- `iot/mall` 不是长期例外。
- 结构必须高内聚低耦合、单一职责、必要功能注释、可扩展可维护。
- 设计模式只用于明确变化点或依赖隔离；禁止为套模式制造抽象。

## Standard Module Shape

```text
develop-module-{name}/
  develop-module-{name}-api/
  develop-module-{name}-server/
  [develop-module-{name}-gateway/]
```

API 模块只承载跨模块稳定契约；server 是业务运行单元；gateway 只在有独立网关/协议接入/设备通信职责时存在；父模块只做 Maven 聚合和依赖治理。

## API Module Standard

```text
api/{business}/
  XxxCommonApi.java
  dto/
  enums/
  local/
  remote/
```

`XxxCommonApi` 是本地和远程共同契约；`dto/enums` 暴露跨模块对象；`local/remote` 隔离调用方式。API 模块禁止应用编排、事务、领域决策、仓储实现、Mapper、DO、持久化逻辑。

## Runtime Unit Internal Standard

```text
domain/{aggregate}/model|valueobject|event|service|repository
application/{aggregate}/command|query|dto|result|port/inbound|port/outbound|service
infrastructure/{aggregate}/persistence|external|rpc|cache|messaging
convert/
controller/
job/
mq/
framework/
```

骨架规则：迁移或创建聚合时必须创建标准目录和接口骨架；Java 空目录用职责明确的接口或 `package-info.java` 固定，禁止 `Temp/Placeholder/Dummy`。

职责边界：
- `domain`：聚合、值对象、事件、领域服务、仓储接口；禁止 Spring/MyBatis/Feign/VO/Mapper/DO/infra。
- `application`：用例编排、事务、权限/租户/数据权限策略；依赖 domain 仓储接口和 outbound 端口，不直接依赖 Mapper/DO。
- `port/inbound`：Controller/Job/MQ 调用的用例契约。
- `port/outbound`：跨模块、通知、文件、第三方、远程服务等外部能力端口。
- `infrastructure`：持久化、外部、RPC、缓存、消息适配实现。
- `convert`：对象映射，不写业务判断。
- `controller/job/mq`：入口层，只调用 application。
- `framework`：Spring/技术装配。
- `service/dal`：旧代码迁移源。

## Special Modules

- `develop-module-iot-server` 是业务管理运行单元；`iot-gateway` 是协议/设备通信运行单元；`iot-core` 必须明确归属到 API/domain/infrastructure/shared，不能长期游离。
- `develop-module-mall` 的 product/promotion/trade/statistics 是独立上下文；每个上下文按同一 API + server + DDD 标准执行；`mall-server` 不能长期模糊聚合。

## Acceptance Criteria

- 模块可解释为标准 runtime unit；每个 runtime 内部结构一致。
- API 有一套 `CommonApi` + `local/remote`，且不含应用/领域/持久化逻辑。
- 核心业务在 `domain/application/infrastructure/convert`；入口层不承载核心规则。
- 每个迁移聚合具备 domain repository、application inbound/outbound/service、infrastructure persistence/external/rpc/cache/messaging。
- 依赖方向不反转；无跨层穿透；高内聚低耦合；命名能定位入口、用例、规则、持久化和适配。
- 外部 API、权限、租户、数据权限、错误码、缓存/MQ/Job/Excel/OpenAPI 行为不被无计划改变。

## Verification Commands

只改本文档：

```bash
grep -E "^## (Use / Do Not Use|Reproducibility Contract|Unified Target|Acceptance Criteria|Verification Commands|Red Flags|Quick Reference)" .claude/ddd-skills/Module_Structure_Standard.md
git diff -- .claude/ddd-skills/Module_Structure_Standard.md
git diff --check
```

改 API/结构时追加：

```bash
mvn compile -pl develop-module-{name}/develop-module-{name}-api -am
mvn compile -pl develop-module-{name}/develop-module-{name}-server -am
mvn test -pl develop-module-{name}/develop-module-{name}-server
mvn compile -pl develop-server -am
mvn clean package -pl develop-server -am -Dmaven.test.skip=true
```

## Red Flags

停止或缩小范围：批量移动多个模块且无收益/验收；API 含事务/Mapper/DO/领域规则；本地/远程契约分裂；入口层直接 Mapper/DO；domain 依赖 Spring/MyBatis/Feign/VO/Mapper/infra；application 沉淀领域不变量；`service/dal` 成为新业务最终位置；`iot/mall` 被长期例外化；以“当前为空/只有一个实现/避免空抽象”省略骨架；无变化点却制造抽象；无法给出验证命令。

## Quick Reference

| 要做什么 | 正确位置 | 禁止位置 |
|---|---|---|
| 稳定跨模块契约 | `api/{business}/XxxCommonApi.java` | `server/service` 私有接口 |
| 跨模块 DTO/枚举 | `api/{business}/dto|enums` | `controller/vo`、`dal/dataobject` |
| 本地/远程适配 | `api/{business}/local|remote` | 两套不同契约 |
| Web/Job/MQ 入口 | `controller/job/mq` 调用 application | 入口内写领域规则 |
| 业务不变量 | `domain/{aggregate}` | `controller/convert/dal` |
| 用例和事务 | `application/{aggregate}/service` | `domain` 或 `controller` |
| 入站/出站端口 | `application/{aggregate}/port/inbound|outbound` | Controller 私有方法或 infra 反向定义 |
| 仓储接口/实现 | `domain/.../repository` / `infrastructure/.../persistence` | domain 依赖 Mapper/DO |
| 对象转换 | `convert` | 应用服务散落映射 |
| 技术配置 | `framework` | `domain` |
| 旧代码 | `service/dal` 迁移源 | 新核心业务最终落位 |
