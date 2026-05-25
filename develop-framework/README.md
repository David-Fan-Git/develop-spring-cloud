# develop-framework

## 模块定位

通用框架与 Spring Boot Starter 集合，为业务模块提供 Web、安全、MyBatis、Redis、MQ、RPC、任务、监控、租户、数据权限等基础能力。

## 基本信息

| 项目 | 内容 |
|---|---|
| 模块路径 | `develop-framework` |
| Maven Artifact | `develop-framework` |
| Packaging | `pom` |
| Java 源文件数量 | 370 |
| 模块说明 | 该包是技术组件，每个子包，代表一个组件。每个组件包括两部分： 1. core 包：是该组件的核心封装 2. config 包：是该组件基于 Spring 的配置 技术组件，也分成两类： 1. 框架组件：和我们熟悉的 MyBatis、Redis 等等的拓展 2. 业务组件：和业务相关的组件的封装，例如说数据字典、操作日志等等。 如果是业务组件，Maven 名字会包含 biz |

## 子模块结构

| 子模块 | 职责 |
|---|---|
| `develop-common` | 模块内部子工程。 |
| `develop-spring-boot-starter-env` | 模块内部子工程。 |
| `develop-spring-boot-starter-mybatis` | 模块内部子工程。 |
| `develop-spring-boot-starter-redis` | 模块内部子工程。 |
| `develop-spring-boot-starter-web` | 模块内部子工程。 |
| `develop-spring-boot-starter-security` | 模块内部子工程。 |
| `develop-spring-boot-starter-websocket` | 模块内部子工程。 |
| `develop-spring-boot-starter-monitor` | 模块内部子工程。 |
| `develop-spring-boot-starter-protection` | 模块内部子工程。 |
| `develop-spring-boot-starter-job` | 模块内部子工程。 |
| `develop-spring-boot-starter-mq` | 模块内部子工程。 |
| `develop-spring-boot-starter-rpc` | 模块内部子工程。 |
| `develop-spring-boot-starter-excel` | 模块内部子工程。 |
| `develop-spring-boot-starter-test` | 模块内部子工程。 |
| `develop-spring-boot-starter-biz-tenant` | 模块内部子工程。 |
| `develop-spring-boot-starter-biz-data-permission` | 模块内部子工程。 |
| `develop-spring-boot-starter-biz-ip` | 模块内部子工程。 |

## 主要目录职责

| 目录 | 说明 |
|---|---|
| `framework` | 模块内 Spring 配置、拦截器、扩展点。 |
| `enums` | 模块内枚举、错误码、状态值等。 |
| `job` | XXL-Job 定时任务处理器。 |
| `mq` | 消息生产者、消费者和消息体。 |
| `service` | 传统三层业务服务接口与实现，是 DDD 迁移的重要来源。 |
| `convert` | 对象转换层，通常使用 MapStruct 处理 VO / DTO / DO / Domain 转换。 |
| `websocket` | WebSocket 连接、会话和消息能力。 |

## 关键依赖

未发现需要在 README 中强调的直接业务 API 或 Starter 依赖。

## 架构职责

- 本模块聚合通用框架能力与 Spring Boot Starter，不直接承载业务用例。
- 各 Starter 面向业务模块输出可复用基础设施能力，例如 Web、安全、MyBatis、Redis、MQ、RPC、任务、监控与租户能力。
- 框架能力应保持领域无关，避免反向依赖具体业务模块。

## 构建与验证

```bash
# 编译该聚合模块及其子模块
mvn compile -pl develop-framework -am

# 打包该聚合模块及其子模块
mvn clean package -pl develop-framework -am -Dmaven.test.skip=true
```

## 维护建议

- 修改 Starter 能力时，优先保证配置项、自动配置条件和默认行为向调用方清晰可控。
- 框架模块不应引入具体业务模块依赖；需要扩展业务行为时优先通过接口、SPI 或配置完成。
- 至少运行当前 Starter 的 `mvn compile`，必要时补充依赖该 Starter 的业务模块编译验证。
