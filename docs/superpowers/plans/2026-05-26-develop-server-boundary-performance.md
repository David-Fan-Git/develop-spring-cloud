# Develop Server Boundary and Performance Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Improve `develop-server` cohesion, runtime defaults, and maintainability without changing existing business behavior or moving hardcoded secrets.

**Architecture:** Keep `develop-server` as the boot container and avoid moving sensitive values in this phase. Make local/dev runtime diagnostics opt-in, document temporary compatibility boundaries clearly, and simplify container Java code while preserving HTTP responses and module fallback behavior.

**Tech Stack:** Java 17, Spring Boot, Spring Cloud Alibaba, Maven, YAML configuration, JUnit/Maven compile verification.

---

## Scope Boundaries

This plan intentionally does **not** process hardcoded `key` / `secret` / `password` values. Those remain unchanged until the wider configuration migration is ready.

This plan starts from the previously reviewed item 2:

1. Reduce business-module configuration coupling by clarifying configuration ownership and compatibility boundaries.
2. Improve local/dev default performance by making heavy diagnostics less aggressive.
3. Reduce RPC auto-configuration coupling risk through clear semantic comments without deleting exclusions that may still be required.
4. Simplify `DevelopServerApplication` comments.
5. Refactor `DefaultController` duplicated response construction while preserving all route mappings and messages.
6. Keep `/test` route behavior unchanged unless the user separately authorizes a behavior change.

## File Structure

**Modify:**
- `develop-server/src/main/resources/application.yaml`
  - Add concise boundary comments around cross-module compatibility configuration.
  - Do not change secret-like values.
- `develop-server/src/main/resources/application-local.yaml`
  - Make Druid web/stat diagnostics opt-in by default.
  - Narrow default mapper debug logging to currently enabled default modules where safe.
  - Improve comments for local single-process RPC exclusions.
- `develop-server/src/main/resources/application-dev.yaml`
  - Make Druid web/stat diagnostics opt-in by default.
  - Keep existing connection and secret values unchanged.
- `develop-server/src/main/java/com/develop/mvp/pk/server/DevelopServerApplication.java`
  - Remove repeated startup-help comments and keep one useful class-level note.
- `develop-server/src/main/java/com/develop/mvp/pk/server/controller/DefaultController.java`
  - Extract duplicated disabled-module response construction into one private method.
  - Keep route mappings, response code, and response messages unchanged.

**Verify:**
- `mvn compile -pl develop-server -am`
- `git diff -- develop-server/src/main/resources/application.yaml develop-server/src/main/resources/application-local.yaml develop-server/src/main/resources/application-dev.yaml develop-server/src/main/java/com/develop/mvp/pk/server/DevelopServerApplication.java develop-server/src/main/java/com/develop/mvp/pk/server/controller/DefaultController.java`

---

### Task 1: Make local Druid diagnostics opt-in

**Files:**
- Modify: `develop-server/src/main/resources/application-local.yaml:30-48`
- Modify: `develop-server/src/main/resources/application-dev.yaml:13-31`

- [ ] **Step 1: Confirm current Druid diagnostic defaults**

Run:
```bash
grep -n "web-stat-filter:\|stat-view-servlet:\|filter:\|stat:" develop-server/src/main/resources/application-local.yaml develop-server/src/main/resources/application-dev.yaml
```

Expected: both local and dev profiles show Druid `web-stat-filter`, `stat-view-servlet`, and `filter.stat` blocks.

- [ ] **Step 2: Change local profile Druid diagnostics to opt-in**

In `develop-server/src/main/resources/application-local.yaml`, replace the Druid block at lines 30-48 with:

```yaml
    druid: # Druid 诊断能力默认关闭，避免本地常态启动为每次请求/SQL 增加统计开销；排查连接池或慢 SQL 时再临时打开
      web-stat-filter:
        enabled: false
      stat-view-servlet:
        enabled: false
        allow: # 设置白名单，不填则允许所有访问；开启控制台时建议仅允许可信来源
        url-pattern: /druid/*
        login-username: # 控制台管理用户名和密码
        login-password:
      filter:
        stat:
          enabled: false
          log-slow-sql: true # 开启 stat 过滤器后记录慢 SQL
          slow-sql-millis: 100
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true
```

- [ ] **Step 3: Change dev profile Druid diagnostics to opt-in**

In `develop-server/src/main/resources/application-dev.yaml`, replace the Druid block at lines 13-31 with:

```yaml
    druid: # Druid 诊断能力默认关闭，避免 dev 环境常态请求/SQL 都进入统计链路；排障时按需打开
      web-stat-filter:
        enabled: false
      stat-view-servlet:
        enabled: false
        allow: # 设置白名单，不填则允许所有访问；开启控制台时建议仅允许可信来源
        url-pattern: /druid/*
        login-username: # 控制台管理用户名和密码
        login-password:
      filter:
        stat:
          enabled: false
          log-slow-sql: true # 开启 stat 过滤器后记录慢 SQL
          slow-sql-millis: 100
          merge-sql: true
        wall:
          config:
            multi-statement-allow: true
```

- [ ] **Step 4: Verify Druid defaults changed only in local/dev**

Run:
```bash
grep -n "web-stat-filter:\|stat-view-servlet:\|enabled: false\|log-slow-sql" develop-server/src/main/resources/application-local.yaml develop-server/src/main/resources/application-dev.yaml
```

Expected: local/dev Druid web/stat diagnostics are `enabled: false`; slow SQL settings remain present for opt-in debugging.

---

### Task 2: Narrow local default mapper debug logging

**Files:**
- Modify: `develop-server/src/main/resources/application-local.yaml:160-185`

- [ ] **Step 1: Confirm current logging scope**

Run:
```bash
grep -n "com.develop.mvp.pk.module.*.dal" develop-server/src/main/resources/application-local.yaml
```

Expected: many optional modules are configured as `debug`, including modules not enabled by default in `develop-server/pom.xml`.

- [ ] **Step 2: Replace logging block with current-default module focus**

In `develop-server/src/main/resources/application-local.yaml`, replace the `logging.level` module mapper section at lines 164-185 with:

```yaml
  level:
    # 默认仅打开 develop-server 当前启用模块的 Mapper 日志，避免未启用模块的大范围 DEBUG 增加 I/O 与排障噪音
    com.develop.mvp.pk.module.infra.dal.mysql: debug
    com.develop.mvp.pk.module.infra.dal.mysql.logger.ApiErrorLogMapper: INFO # 避免和 GlobalExceptionHandler 重复打印
    com.develop.mvp.pk.module.infra.dal.mysql.job.JobLogMapper: INFO
    com.develop.mvp.pk.module.infra.dal.mysql.file.FileConfigMapper: INFO
    com.develop.mvp.pk.module.system.dal.mysql: debug
    com.develop.mvp.pk.module.system.dal.mysql.sms.SmsChannelMapper: INFO
    # 可选业务模块启用后，再按需临时打开对应 Mapper DEBUG，避免容器默认感知所有模块内部包
    # com.develop.mvp.pk.module.bpm.dal.mysql: debug
    # com.develop.mvp.pk.module.pay.dal.mysql: debug
    # com.develop.mvp.pk.module.tool.dal.mysql: debug
    # com.develop.mvp.pk.module.member.dal.mysql: debug
    # com.develop.mvp.pk.module.trade.dal.mysql: debug
    # com.develop.mvp.pk.module.promotion.dal.mysql: debug
    # com.develop.mvp.pk.module.statistics.dal.mysql: debug
    # com.develop.mvp.pk.module.crm.dal.mysql: debug
    # com.develop.mvp.pk.module.erp.dal.mysql: debug
    # com.develop.mvp.pk.module.iot.dal.mysql: debug
    # com.develop.mvp.pk.module.iot.dal.tdengine: DEBUG
    # com.develop.mvp.pk.module.ai.dal.mysql: debug
    org.springframework.context.support.PostProcessorRegistrationDelegate: ERROR # Spring Boot 3.x 部分历史 WARN 噪音，保留压制
```

- [ ] **Step 3: Verify optional modules are no longer active DEBUG defaults**

Run:
```bash
grep -n "module\.\(bpm\|pay\|member\|trade\|promotion\|statistics\|crm\|erp\|iot\|ai\).*: debug\|module\.iot\.dal\.tdengine: DEBUG" develop-server/src/main/resources/application-local.yaml
```

Expected: optional module debug entries appear only as commented examples, while `infra` and `system` remain active.

---

### Task 3: Document server configuration ownership boundaries

**Files:**
- Modify: `develop-server/src/main/resources/application.yaml:176-287`
- Modify: `develop-server/src/main/resources/application.yaml:288-374`

- [ ] **Step 1: Add AI compatibility boundary comment**

Above the existing line:

```yaml
--- #################### AI 相关配置 ####################
```

insert:

```yaml
# 兼容边界：develop-server 当前仍集中承载可选模块配置，保证旧模块按原方式启用。
# 新增或迁移后的核心业务配置应优先下沉到对应 module 的 ConfigurationProperties 或配置中心 DataId，避免容器继续感知业务细节。
```

- [ ] **Step 2: Add David/develop custom configuration boundary comment**

Above the existing line:

```yaml
--- #################### David相关配置 ####################
```

insert:

```yaml
# 兼容边界：以下 develop.* 包含通用运行时配置与部分历史业务模块配置。
# 本轮不迁移密钥和业务参数，只补齐边界说明；后续完成模块配置治理后再按模块拆分。
```

- [ ] **Step 3: Verify comments are concise and do not alter values**

Run:
```bash
git diff -- develop-server/src/main/resources/application.yaml
```

Expected: diff contains only the new comments in `application.yaml`; no `api-key`, `secret`, `password`, `request-key`, or `response-key` values are changed.

---

### Task 4: Clarify local RPC auto-configuration exclusions

**Files:**
- Modify: `develop-server/src/main/resources/application-local.yaml:16-28`

- [ ] **Step 1: Replace RPC exclusion comment with explicit compatibility note**

In `develop-server/src/main/resources/application-local.yaml`, replace:

```yaml
      # 禁用 RPC 相关自动配置（本地单体启动无需 Feign 远程调用）
```

with:

```yaml
      # 本地单体启动使用模块内本地 Bean，不发起 Feign/RPC 远程调用。
      # 这里保留历史 AutoConfiguration 排除项以维持启动兼容；后续应收敛到 develop.rpc.* 语义开关，避免容器感知 framework 内部类名。
```

- [ ] **Step 2: Do not delete the existing exclusion list**

Keep these existing class names unchanged:

```yaml
      - com.develop.mvp.pk.framework.security.config.DevelopSecurityRpcAutoConfiguration
      - com.develop.mvp.pk.framework.operatelog.config.DevelopOperateLogRpcAutoConfiguration
      - com.develop.mvp.pk.framework.datapermission.config.DevelopDataPermissionRpcAutoConfiguration
      - com.develop.mvp.pk.framework.dict.config.DevelopDictRpcAutoConfiguration
      - com.develop.mvp.pk.framework.tenant.config.DevelopTenantRpcAutoConfiguration
      - com.develop.mvp.pk.framework.env.config.DevelopEnvRpcAutoConfiguration
      - com.develop.mvp.pk.framework.apilog.config.DevelopApiLogRpcAutoConfiguration
```

- [ ] **Step 3: Verify only the comment changed in the exclusion block**

Run:
```bash
git diff -- develop-server/src/main/resources/application-local.yaml
```

Expected: the RPC exclusion class names are unchanged; only surrounding comments and unrelated planned logging/Druid changes differ.

---

### Task 5: Simplify the boot application class

**Files:**
- Modify: `develop-server/src/main/java/com/develop/mvp/pk/server/DevelopServerApplication.java`

- [ ] **Step 1: Replace repeated class and method comments**

Replace the file content with:

```java
package com.develop.mvp.pk.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * develop-server 是默认单体启动容器，通过 Maven 依赖组合实际启用的业务模块。
 * 启动问题优先参考项目根目录 CLAUDE.md 中的 Maven 命令与当前 profile 配置。
 *
 * @author David
 */
@SuppressWarnings("SpringComponentScan") // IDEA 无法识别 ${develop.info.base-package} 占位符，运行时由 Spring 正常解析
@SpringBootApplication(scanBasePackages = {"${develop.info.base-package}.server", "${develop.info.base-package}.module"},
        excludeName = {
            // RPC 相关
//            "org.springframework.cloud.openfeign.FeignAutoConfiguration",
//            "com.develop.mvp.pk.module.system.framework.rpc.config.RpcConfiguration"
        })
public class DevelopServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevelopServerApplication.class, args);
//        new SpringApplicationBuilder(DevelopServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);
    }

}
```

- [ ] **Step 2: Verify no startup annotation changed**

Run:
```bash
git diff -- develop-server/src/main/java/com/develop/mvp/pk/server/DevelopServerApplication.java
```

Expected: repeated comments are removed; `@SpringBootApplication`, `scanBasePackages`, and `excludeName` remain semantically unchanged.

---

### Task 6: Refactor DefaultController duplicate response construction

**Files:**
- Modify: `develop-server/src/main/java/com/develop/mvp/pk/server/controller/DefaultController.java`

- [ ] **Step 1: Replace repeated `CommonResult.error` calls with a private helper**

Replace the file content with:

```java
package com.develop.mvp.pk.server.controller;

import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.util.servlet.ServletUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.develop.mvp.pk.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * 默认 Controller，负责在可选模块未启用时返回明确提示，避免调用方只看到 404。
 *
 * @author David
 */
@RestController
@Slf4j
public class DefaultController {

    @RequestMapping("/admin-api/bpm/**")
    public CommonResult<Boolean> bpm404() {
        return disabledModule("[工作流模块 develop-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
    }

    @RequestMapping("/admin-api/mp/**")
    public CommonResult<Boolean> mp404() {
        return disabledModule("[微信公众号 develop-module-mp - 已禁用][参考 https://doc.iocoder.cn/mp/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/product/**", // 商品中心
            "/admin-api/trade/**", // 交易中心
            "/admin-api/promotion/**" }) // 营销中心
    public CommonResult<Boolean> mall404() {
        return disabledModule("[商城系统 develop-module-mall - 已禁用][参考 https://doc.iocoder.cn/mall/build/ 开启]");
    }

    @RequestMapping("/admin-api/erp/**")
    public CommonResult<Boolean> erp404() {
        return disabledModule("[ERP 模块 develop-module-erp - 已禁用][参考 https://doc.iocoder.cn/erp/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/wms/**" })
    public CommonResult<Boolean> wms404() {
        return disabledModule("[WMS 仓库管理系统 develop-module-wms - 已禁用][参考 https://doc.iocoder.cn/wms/build/ 开启]");
    }

    @RequestMapping("/admin-api/crm/**")
    public CommonResult<Boolean> crm404() {
        return disabledModule("[CRM 模块 develop-module-crm - 已禁用][参考 https://doc.iocoder.cn/crm/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/mes/**" })
    public CommonResult<Boolean> mes404() {
        return disabledModule("[MES 系统 develop-module-mes - 已禁用][参考 https://doc.iocoder.cn/mes/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/im/**" })
    public CommonResult<Boolean> im404() {
        return disabledModule("[IM 即时通讯 develop-module-im - 已禁用][参考 https://doc.iocoder.cn/im/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/report/**" })
    public CommonResult<Boolean> report404() {
        return disabledModule("[报表模块 develop-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/pay/**" })
    public CommonResult<Boolean> pay404() {
        return disabledModule("[支付模块 develop-module-pay - 已禁用][参考 https://doc.iocoder.cn/pay/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/ai/**" })
    public CommonResult<Boolean> ai404() {
        return disabledModule("[AI 大模型 develop-module-ai - 已禁用][参考 https://doc.iocoder.cn/ai/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/iot/**" })
    public CommonResult<Boolean> iot404() {
        return disabledModule("[IoT 物联网 develop-module-iot - 已禁用][参考 https://doc.iocoder.cn/iot/build/ 开启]");
    }

    /**
     * 本地联调辅助接口：打印请求参数、请求头和请求体；本轮保持原行为不变。
     */
    @RequestMapping(value = { "/test" })
    @PermitAll
    public CommonResult<Boolean> test(HttpServletRequest request) {
        log.info("Query: {}", ServletUtils.getParamMap(request));
        log.info("Header: {}", ServletUtils.getHeaderMap(request));
        log.info("Body: {}", ServletUtils.getBody(request));
        return CommonResult.success(true);
    }

    private CommonResult<Boolean> disabledModule(String message) {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(), message);
    }

}
```

- [ ] **Step 2: Verify all route mappings are preserved**

Run:
```bash
grep -n "@RequestMapping" develop-server/src/main/java/com/develop/mvp/pk/server/controller/DefaultController.java
```

Expected: mappings still include `/admin-api/bpm/**`, `/admin-api/mp/**`, `/admin-api/product/**`, `/admin-api/trade/**`, `/admin-api/promotion/**`, `/admin-api/erp/**`, `/admin-api/wms/**`, `/admin-api/crm/**`, `/admin-api/mes/**`, `/admin-api/im/**`, `/admin-api/report/**`, `/admin-api/pay/**`, `/admin-api/ai/**`, `/admin-api/iot/**`, and `/test`.

---

### Task 7: Compile and review final diff

**Files:**
- Verify all files modified in prior tasks.

- [ ] **Step 1: Compile develop-server reactor**

Run:
```bash
mvn compile -pl develop-server -am
```

Expected: Maven exits with code 0 and reports `BUILD SUCCESS`.

- [ ] **Step 2: Review changed files**

Run:
```bash
git diff -- develop-server/src/main/resources/application.yaml develop-server/src/main/resources/application-local.yaml develop-server/src/main/resources/application-dev.yaml develop-server/src/main/java/com/develop/mvp/pk/server/DevelopServerApplication.java develop-server/src/main/java/com/develop/mvp/pk/server/controller/DefaultController.java
```

Expected:
- No secret-like values are changed.
- Druid local/dev diagnostic defaults are less expensive.
- Optional module mapper DEBUG logging is no longer active by default.
- RPC exclusion class names are unchanged.
- Startup class is simpler.
- DefaultController route mappings and messages are preserved.

- [ ] **Step 3: Check working tree status**

Run:
```bash
git status --short
```

Expected: modified files include only the planned `develop-server` files plus any pre-existing `.claude/settings.local.json` change and this plan file if it remains uncommitted.

---

## Self-Review

- Spec coverage: covers all approved items from item 2 onward and explicitly excludes hardcoded secret migration.
- Placeholder scan: no `TBD`, unfinished requirement, or unspecified implementation step remains.
- Type consistency: Java helper method `disabledModule(String message)` returns the same `CommonResult<Boolean>` type used by existing endpoints.
- Behavior preservation: no route, response text, Maven dependency, or secret value is intentionally changed.
