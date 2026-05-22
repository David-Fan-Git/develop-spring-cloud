# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Summary

芋道 develop-cloud (Smart Cloud) — a Spring Cloud Alibaba microservices rapid-development platform. This is the **complete version** with all business modules. Derived from RuoYi-Vue-Pro.

**Tech stack:** Java 17, Spring Boot 3.5.x, Spring Cloud 2025.0.1, Spring Cloud Alibaba 2025.0.0.0, Maven, MyBatis Plus, Redis/Redisson, Lombok + MapStruct.

## Build & Run

```bash
# Full build (skip tests)
mvn clean package -Dmaven.test.skip=true

# Run tests for a single module
mvn test -pl develop-module-system/develop-module-system-server

# Run the server (after build)
cd develop-server && mvn spring-boot:run

# Run the gateway
cd develop-gateway && mvn spring-boot:run
```

Configuration files: `develop-server/src/main/resources/application.yaml` (main) and per-profile variants (`application-local.yaml`, `application-dev.yaml`).

The project uses `maven-surefire-plugin` 3.x with JUnit 5. There is a `develop-spring-boot-starter-test` in the framework that provides test base classes and utilities — extend those for new tests.

## Module Architecture

```
develop-dependencies/          # BOM — all dependency versions (single pom)
develop-framework/             # 17 shared starters (web, security, mybatis, redis, mq, rpc, job, excel, test…)
develop-gateway/               # Spring Cloud Gateway (standalone boot app)
develop-server/                # Monolithic boot app — aggregates develop-module-*-server as Maven dependencies
develop-module-{name}/         # Business modules, each split into api/ and server/ sub-modules
develop-ui/                    # 5 frontend projects (Vue3-element-plus, Vue3-vben, Vue2, uni-app admin, uni-app mall)
sql/                           # Database init scripts for MySQL, Oracle, PostgreSQL, SQL Server, DM, Kingbase, OpenGauss
```

The platform runs in one of two modes controlled by which module dependencies are uncommented in `develop-server/pom.xml`:
- **Minimal:** only `develop-module-system-server` + `develop-module-infra-server` (fast compile)
- **Full:** uncomment additional modules (member, bpm, pay, report, mall, crm, erp, etc.)

## Business Module Pattern

Every `develop-module-{name}/` follows this internal structure:

```
develop-module-{name}/
  develop-module-{name}-api/     # Interfaces, DTOs, API contracts (can be shared across modules)
  develop-module-{name}-server/  # Implementation: controllers, services, DAL
```

Some larger modules (e.g., `develop-module-mall`) contain multiple sub-domains, each with their own api/server pair (product, promotion, trade, statistics).

Java package base: `com.develop.mvp.pk`

Within a `-server` module, the typical layer layout under `com.develop.mvp.pk.module.{name}/`:
- `controller/` — REST endpoints
- `service/` — business logic interfaces and implementations
- `dal/` — MyBatis Plus mapper interfaces and entity classes
- `framework/` — module-local Spring configuration (auto-configurations, interceptors, etc.)

## Framework Starters (develop-framework)

Key starters and their responsibilities:

| Starter | Purpose |
|---|---|
| `develop-spring-boot-starter-web` | REST API defaults, global exception handling, Jackson config |
| `develop-spring-boot-starter-security` | Spring Security authn/authz + operation logging |
| `develop-spring-boot-starter-mybatis` | MyBatis Plus, dynamic datasource, easy-trans |
| `develop-spring-boot-starter-redis` | Redis caching, Redisson distributed locks |
| `develop-spring-boot-starter-mq` | Message queue abstraction (Redis/RabbitMQ/Kafka/RocketMQ) |
| `develop-spring-boot-starter-rpc` | Feign-based inter-service RPC |
| `develop-spring-boot-starter-job` | XXL-Job integration |
| `develop-spring-boot-starter-biz-tenant` | Multi-tenant (SaaS) support |
| `develop-spring-boot-starter-biz-data-permission` | Row-level data permission filtering |
| `develop-spring-boot-starter-excel` | Excel import/export |
| `develop-spring-boot-starter-test` | Test base classes (extend for unit/integration tests) |

## Key Infrastructure Services

- **Nacos** — service registration + configuration center
- **Spring Cloud Gateway** (`develop-gateway`) — API gateway with token auth filter, gray routing, CORS, access logging
- **Sentinel** — rate limiting and circuit breaking (`develop-spring-boot-starter-protection`)
- **XXL-Job** — distributed scheduled tasks (`develop-spring-boot-starter-job`)
- **Flowable** — workflow engine (used in `develop-module-bpm`)
- **Spring Security + Token + Redis** — authentication, supports multi-terminal and SSO

## Configuration Conventions

- `application.yaml` — main config with Spring profile placeholders
- `application-local.yaml` — local development (use this for daily work)
- `application-dev.yaml` — dev environment
- `lombok.config` — project-level Lombok settings (chain accessors, callSuper on toString/equalsHashCode)
- `${develop.info.base-package}` property is set to `com.develop.mvp.pk` and used in `@SpringBootApplication` scanBasePackages

## Database

Multiple database dialects supported via DDL scripts in `sql/`: MySQL, Oracle, PostgreSQL, SQL Server, MariaDB, DM (达梦), Kingbase (人大金仓), OpenGauss. The project uses MyBatis Plus dynamic-datasource for multi-database routing.
