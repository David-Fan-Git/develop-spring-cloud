---
name: aggregate-root-ai-execution-contract-template
description: Use when creating or upgrading a DDD aggregate skill so a clean AI can execute it without guessing scope, facts, dependencies, or verification.
---

# Aggregate Root AI Execution Contract Template

## Overview

本模板用于创建或升级 `.claude/ddd-skills/AggregateRoot_<Name>_Skill.md`，目标是让无上下文 AI 能按事实源、边界、依赖规则和验证命令执行生产级 DDD 改造。

## AI Execution Contract

### Scope

- Module: `develop-module-{module}`
- Runtime unit: `develop-module-{module}-server`
- Aggregate: `{aggregate}`
- Use cases in scope:
  - `{useCaseName}`
- Out of scope:
  - 无关模块
  - 无关聚合
  - 破坏性 API 契约变更
  - 数据库结构变更，除非本 skill 明确列出 SQL 和迁移验证

### Must Read Before Coding

| Fact type | Path |
|---|---|
| Controller | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../controller/...` |
| Request/Response VO | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../controller/.../vo/...` |
| CommonApi/DTO | `develop-module-{module}/develop-module-{module}-api/src/main/java/.../api/{business}/...` |
| Service source | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../service/...` |
| DO/Mapper | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../dal/...` |
| Convert | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../convert/...` |
| ErrorCode | `develop-module-{module}/develop-module-{module}-server/src/main/java/.../enums/ErrorCodeConstants.java` |
| Existing tests | `develop-module-{module}/develop-module-{module}-server/src/test/java/...` |

### Must Preserve

- Controller path, HTTP method, request VO, response VO, pagination shape.
- `CommonApi` method names, parameter types, return types, and remote/local semantics.
- Permission annotations, tenant handling, data permission behavior, operation logs.
- Error code constants, exception type, and parameter order.
- Existing MQ, Job, cache, Excel import/export, OpenAPI-visible behavior.

### Allowed Changes

- `domain/{aggregate}/` for aggregate root, value objects, domain events, domain services, and domain repository interfaces.
- `application/{aggregate}/` for use case orchestration, transaction boundary, and application policies.
- `infrastructure/{aggregate}/` for repository implementation and technical adapters.
- `convert/` for DO ↔ Domain ↔ DTO/VO mapping.
- `controller/job/mq/framework` only when adapting entry points to call application use cases.
- Tests that prove preserved behavior and architecture boundaries.

### Forbidden Changes

- Do not move or rename public APIs without a separate migration plan.
- Do not add new core business logic to `service/dal`.
- Do not make `domain` depend on Spring, MyBatis, Feign, Controller VO, Mapper, DO, or infrastructure implementations.
- Do not make `application` depend on controller classes or concrete infrastructure implementations.
- Do not create empty abstractions, generic ports, or design patterns without a clear variation point.
- Do not batch unrelated aggregates or modules into one execution.

### Dependency Rules

| Layer | Allowed dependencies | Forbidden dependencies |
|---|---|---|
| API | DTO, enums, local/remote contract adapters | service, application, domain internals, dal, Mapper, DO |
| Controller/Job/MQ | application use cases, VO/DTO, framework annotations | Mapper, DO direct access, domain rule duplication |
| Application | domain, repository/port interfaces, transaction annotations | controller, infrastructure implementation, Mapper, DO |
| Domain | Java, Lombok, domain-local types | Spring, MyBatis, Feign, Controller VO, Mapper, DO, infrastructure |
| Infrastructure | domain, repository interfaces, dal/mapper, external clients | defining business rules that belong in domain |
| Convert | VO/DTO/DO/domain mapping only | business rules, permission checks, persistence access |

### Verification Gate

Run the smallest matching command set:

```bash
mvn compile -pl develop-module-{module}/develop-module-{module}-api -am
mvn compile -pl develop-module-{module}/develop-module-{module}-server -am
mvn test -pl develop-module-{module}/develop-module-{module}-server
```

If architecture tests exist for the module, run them explicitly:

```bash
mvn test -pl develop-module-{module}/develop-module-{module}-server -Dtest=*ArchitectureTest
```

### Stop Conditions

Stop and update the skill or ask for a migration decision when:

- A required fact source path does not exist.
- Current code contradicts this skill.
- A field, method signature, error code, transaction boundary, or external behavior would need to be guessed.
- A public HTTP API, `CommonApi`, DTO, MQ, Job, cache, or Excel behavior might change.
- Maven compile/test or ArchUnit fails.
- The requested scope touches multiple unrelated modules or aggregates.

## AI Self-Check

- [ ] I read every `Must Read Before Coding` path that exists.
- [ ] I preserved all `Must Preserve` behavior.
- [ ] I only changed files allowed by `Allowed Changes`.
- [ ] I avoided every `Forbidden Changes` item.
- [ ] I verified dependency direction with tests or explicit review.
- [ ] I ran the verification commands or documented why they could not run.
