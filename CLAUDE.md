# CLAUDE.md

## Project Snapshot

Smart Cloud / develop-cloud is a Java 17 Spring Cloud Alibaba backend platform derived from RuoYi-Vue-Pro. Versions live in `develop-dependencies/pom.xml`; the root `pom.xml` controls the default Maven reactor. Use local `mvn` with Java 17; there is no Maven wrapper.

Default reactor/runtime is lightweight: `develop-server` aggregates enabled module servers, usually `system` + `infra`; optional business modules are present but may be commented out.

## Commands

```bash
mvn clean package -Dmaven.test.skip=true
mvn compile
mvn compile -pl develop-module-system/develop-module-system-server -am
mvn compile -pl develop-server -am
mvn clean package -pl develop-server -am -Dmaven.test.skip=true
mvn clean package -pl develop-gateway -am -Dmaven.test.skip=true
mvn test -pl develop-module-system/develop-module-system-server
mvn test -pl develop-module-system/develop-module-system-server -Dtest=AdminUserServiceImplTest
mvn test -pl develop-module-system/develop-module-system-server -Dtest=AdminUserServiceImplTest#testCreateUser_success
mvn spring-boot:run -pl develop-server -am
mvn spring-boot:run -pl develop-gateway -am
```

No repo-wide lint command; validate Java with Maven compile/tests. Test modules use JUnit 5, Surefire 3.x, `src/test/resources/application-unit-test.yaml`, and `develop-spring-boot-starter-test` helpers.

## Key Paths

- `develop-dependencies/`: BOM/dependency versions.
- `develop-framework/`: shared Spring Boot starters.
- `develop-server/`: monolithic boot container.
- `develop-gateway/`: Spring Cloud Gateway runtime.
- `develop-module-{name}/`: business modules, usually `api/` + `server/`.
- `develop-ui/`: frontend checkouts; check each subproject before assuming package manager.
- `sql/`: multi-database DDL.
- Main config: `develop-server/src/main/resources/application*.yaml`, gateway config under `develop-gateway/src/main/resources/`.

Package base: `com.develop.mvp.pk`.

## DDD Hard Rules

The repository is migrating from old three-layer code to DDD. Final homes for core business logic:

```text
domain/{aggregate}/          pure Java aggregate, value objects, events, domain services, repository interfaces
application/{aggregate}/     use-case orchestration, transactions, ports, DTO/results
infrastructure/{aggregate}/  persistence/external/rpc/cache/messaging adapters
convert/                     DO/domain/DTO/VO mapping
```

Rules:
- `service/` and `dal/` are migration sources, not final homes for new core business logic.
- Domain must not depend on Spring, MyBatis, Feign, Controller VO, Mapper, DO, or infrastructure implementations.
- Controllers, jobs, and MQ are entry layers; they call application use cases and must not hold domain decisions.
- Infrastructure implements adapters and repository implementations; it does not define business rules.
- Preserve external behavior: HTTP API, DTO/VO fields, permissions, tenant/data permissions, errors, cache/MQ/Job/Excel/OpenAPI contracts.
- Refactoring must preserve behavior unless a separate migration plan explicitly changes a contract.

## DDD Skill Workflow

DDD skills live in `.claude/ddd-skills/`.

Before changing module structure, API contracts, DDD placement, or aggregates, read:
- `.claude/ddd-skills/DDD_Skill_Production_Readiness_Standard.md`
- `.claude/ddd-skills/Module_Structure_Standard.md`
- the target aggregate skill, if present

For a new aggregate:
1. Identify intent, responsibility, data boundary, dependencies.
2. Create/update `AggregateRoot_<Name>_Skill.md` with boundaries, invariants, dependencies, rollback conditions, acceptance criteria.
3. Verify skill against current code; current compilable external behavior wins conflicts.
4. Refactor only that aggregate/context.
5. Compile/test and check acceptance criteria.

During DDD refactoring, decide from existing standards and facts; do not ask the user to choose execution path.

## Module/API Standard

- Maven modules are organized by runtime unit: `server`, optional `gateway`, and API modules.
- API modules expose one stable `CommonApi` contract with `local/remote` adapters for local module calls and Feign/RPC.
- `iot` and `mall` are not permanent exceptions; converge them to the same runtime-unit, API-contract, and DDD-layer standards.
- Use design patterns only for clear variation points or dependency isolation; do not add abstractions only to use a pattern.

## Framework Starters

Common starters include web, security, mybatis, redis, mq, rpc, job, tenant, data-permission, excel, protection, monitor, websocket, and test under `develop-framework/`.

## Git Workflow

For development tasks: create an isolated branch/worktree first, implement and verify there, commit locally, then push to GitHub and create a PR for review/merge when authorized. Do not directly pollute the target branch.

## Safety

Prefer POM files over README prose for versions. Do not delete framework code that may be historical production fixes without verification. For frontend work, inspect the actual frontend project before choosing commands.
