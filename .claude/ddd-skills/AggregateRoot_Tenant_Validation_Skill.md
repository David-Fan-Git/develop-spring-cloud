# DDD Skill: AggregateRoot_Tenant_Validation_Skill

## 1. 技能名称

`AggregateRoot_Tenant_Validation_Skill` — 租户（Tenant）聚合根的领域建模与重构技能

## 2. 适用场景

本技能针对 **SaaS 租户** 的完整生命周期管理，覆盖以下业务操作：

- 创建租户（含自动创建管理员角色+用户——编排逻辑，非聚合根职责）
- 更新租户（含套餐变更时的角色权限同步）
- 删除租户（单个/批量）
- 租户有效性校验（存在性 + 启用状态 + 过期时间）
- 租户名称/域名唯一性校验
- 租户查询（按ID、名称、域名、套餐、状态、分页）

## 3. DDD 构造块

### 3.1 聚合根：Tenant

```
com.develop.mvp.pk.module.system.domain.tenant.Tenant
```

**角色**：SaaS 租户的领域聚合根，封装租户的完整生命周期和业务规则。

**聚合边界**：
- Tenant（根实体）
- 不包含：TenantPackage、User、Role、Menu、Permission — 这些是外部聚合，仅通过 ID 引用

### 3.2 值对象（Value Objects）

| 值对象 | 类名 | 封装字段 | 不可变 | 自校验 |
|--------|------|---------|--------|--------|
| 租户ID | `TenantId` | `Long value` | ✅ | 非空 |
| 租户名称 | `TenantName` | `String value` | ✅ | 非空、非空白 |
| 租户状态 | `TenantStatus` | `Integer code` | ✅ | 只能是 ENABLE/DISABLE |
| 租户套餐引用 | `TenantPackageRef` | Long packageId, String packageName | ✅ | 非空 |
| 过期时间 | `TenantExpireTime` | `LocalDateTime value` | ✅ | 非空 |

### 3.3 仓储接口（Repository，领域层）

```
com.develop.mvp.pk.module.system.domain.tenant.repository.TenantRepository
```

```java
public interface TenantRepository {
    Tenant save(Tenant tenant);
    void delete(TenantId id);
    Tenant findById(TenantId id);
    Optional<Tenant> findByName(TenantName name);
    Optional<Tenant> findByWebsite(String website);
    List<Tenant> findByPackageId(TenantPackageRef packageRef);
    List<Tenant> findByStatus(TenantStatus status);
    PageResult<Tenant> findPage(TenantPageQuery query);
    long countByPackageId(TenantPackageRef packageRef);
    List<Tenant> findAll();
    boolean existsByName(TenantName name);
    boolean existsByWebsite(String website);
}
```

### 3.4 领域服务（Domain Service）

| 领域服务 | 职责 | 原因 |
|---------|------|------|
| `TenantUniquenessChecker` | 检查租户名称/域名在全局唯一 | 需要跨聚合查询，属于领域服务 |

### 3.5 领域事件（Domain Events）

| 事件 | 触发时机 | 携带数据 | 消费者 |
|------|---------|---------|--------|
| `TenantCreatedEvent` | 租户创建成功后 | tenantId, name | 操作日志、初始化数据（可选） |
| `TenantDisabledEvent` | 租户状态变为禁用 | tenantId | 禁用该租户下所有用户 |
| `TenantDeletedEvent` | 租户删除成功后 | tenantId, name | 清理租户关联数据 |

### 3.6 工厂（Factory）

`TenantFactory`：负责创建复杂的 Tenant 聚合，特别是：
- 创建时：组装 TenantName、TenantStatus（默认ENABLED）、TenantExpireTime
- 重建时：从持久化数据恢复 Tenant 聚合

## 4. 职责边界

### 4.1 Tenant 聚合根必须负责的规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R01 | 创建时，默认状态为 ENABLE | `createTenant()` L100 — TenantDO 无默认状态，由调用方传入 |
| R02 | 租户名称在全局不可重复 | `validTenantNameDuplicate()` L163-175 |
| R03 | 租户域名在全局不可重复 | `validTenantWebsiteDuplicate()` L177-189 |
| R04 | 系统租户（packageId=0）不可修改/删除 | `validateUpdateTenant()` L234-243 |
| R05 | 租户状态只能是 ENABLE(0) 或 DISABLE(1) | TenantStatus 值对象 |
| R06 | 过期时间必须晚于创建时间 | `validTenant()` L93-94 — expireTime 过期校验 |
| R07 | 租户必须校验存在性+启用+未过期才能使用 | `validTenant()` L85-95 |
| R08 | 禁用租户时，状态变更为 DISABLE | 状态变更 |
| R09 | 租户套餐变更时，需同步更新租户的套餐引用 | `updateTenant()` L158 |

### 4.2 严禁外泄的职责（不可放在 Tenant 聚合内）

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接操作数据库/调用 Mapper | 破坏持久化无关性 | Repository 实现 |
| 创建管理员角色和用户（createTenant中的逻辑） | 跨聚合编排，User/Role是独立聚合 | ApplicationService |
| 更新角色菜单权限 | Permission/Menu 是独立聚合 | ApplicationService |
| 校验租户套餐是否存在且启用 | TenantPackage 是独立聚合 | 应用层传入校验结果 |
| 处理 UI 层的 VO 转换 | 表示层关注点 | Controller/Convert |

## 5. 依赖与协作

### 5.1 领域层依赖（向内）

Tenant 聚合根仅依赖：
- 自身值对象（TenantId, TenantName, TenantStatus, TenantPackageRef, TenantExpireTime）
- 领域服务接口（TenantUniquenessChecker）
- 仓储接口（TenantRepository）

### 5.2 跨聚合协作（仅通过 ID 引用）

| 外部聚合 | 引用方式 | 协作场景 |
|---------|---------|---------|
| TenantPackage（套餐） | `TenantPackageRef` (packageId) | 创建/更新时校验套餐存在性（应用层负责） |
| User（管理员用户） | `contactUserId: Long` | 创建时自动创建管理员（应用层编排） |
| Role（角色） | 无直接引用 | 创建时自动创建租户管理员角色（应用层编排） |

### 5.3 基础设施依赖（向外，通过接口倒置）

```
领域层定义接口                   基础设施层实现
─────────────                   ──────────────
TenantRepository         ←──    TenantRepositoryImpl (委托 TenantMapper)
TenantUniquenessChecker  ←──    TenantUniquenessCheckerImpl (委托 TenantMapper)
```

## 6. 不变式与约束（Invariants）

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I01 | `name` 在全局不可重复 | 跨聚合唯一性 | 创建/修改时 |
| I02 | `websites` 中的每个域名在全局不可重复 | 跨聚合唯一性 | 创建/修改时 |
| I03 | `status` 只能是 ENABLE 或 DISABLE | 聚合内部（值对象） | 状态变更时 |
| I04 | 系统租户（packageId=0）不可被删除或修改名称 | 聚合内部 | 删除/修改时 |
| I05 | `expireTime` 不能为空，过期租户不可被验证通过 | 聚合内部 | 有效性校验时 |
| I06 | `packageId` 必须引用一个存在且启用的套餐 | 跨聚合约束 | 创建/修改时（应用层校验） |
| I07 | 租户名称变更时，如套餐也变更，需同步更新套餐关联 | 聚合内部 | 修改时 |

## 7. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | Tenant 类不包含任何 MyBatis/Spring 注解 | 代码审查 |
| AC02 | Tenant 类不直接注入或调用 Mapper/Repository 实现类 | 代码审查 |
| AC03 | Tenant 构造方法确保所有不变式在创建时得到满足 | 代码审查 |
| AC04 | TenantId、TenantName、TenantStatus、TenantPackageRef、TenantExpireTime 为不可变值对象（final 字段，无 setter） | 代码审查 |
| AC05 | TenantRepository 接口定义在领域层包，不 import MyBatis 类 | 代码审查 |
| AC06 | TenantRepositoryImpl 在基础设施层，import MyBatis 类并负责 DO↔领域模型映射 | 代码审查 |
| AC07 | Tenant 聚合的公共方法名称体现业务语义（disable, updateProfile, markDeleted） | 代码审查 |
| AC08 | TenantServiceImpl 仅保留编排逻辑，所有业务规则迁移到 Tenant 聚合或值对象 | 代码审查 |
| AC09 | 创建租户时的角色+用户创建编排逻辑在 ApplicationService 中 | 代码审查 |
| AC10 | 唯一性校验通过 TenantUniquenessChecker 领域服务接口完成 | 代码审查 |
| AC11 | TenantController 编译通过，无编译错误 | 运行编译 |
| AC12 | 系统租户不能被删除或修改（R04, I04） | 单元测试 |

## 8. 目录结构规划（重构后）

```
develop-module-system/develop-module-system-server/src/main/java/com/develop/mvp/pk/module/system/
├── domain/tenant/
│   ├── Tenant.java                    # 聚合根
│   ├── TenantFactory.java             # 工厂
│   ├── valueobject/
│   │   ├── TenantId.java
│   │   ├── TenantName.java
│   │   ├── TenantStatus.java
│   │   ├── TenantPackageRef.java
│   │   └── TenantExpireTime.java
│   ├── event/
│   │   ├── TenantCreatedEvent.java
│   │   ├── TenantDisabledEvent.java
│   │   └── TenantDeletedEvent.java
│   ├── service/
│   │   └── TenantUniquenessChecker.java  # 领域服务接口
│   └── repository/
│       ├── TenantRepository.java         # 仓储接口
│       └── TenantPageQuery.java          # 分页查询
├── application/tenant/
│   └── TenantApplicationService.java     # 应用编排服务
└── infrastructure/tenant/
    ├── TenantRepositoryImpl.java         # MyBatis 仓储实现
    └── TenantUniquenessCheckerImpl.java  # 唯一性校验实现
```

## 9. 回滚条件

如果以下任一情况发生，回滚当前修改：

1. 编译失败
2. Tenant 聚合根内部注入了基础设施依赖
3. 值对象存在 setter 或可变字段
4. 创建租户的角色/用户创建逻辑泄漏到聚合根内
5. TenantController 的所有端点行为出现回归

## 10. 分步执行计划

**阶段 1**：创建值对象（TenantId, TenantName, TenantStatus, TenantPackageRef, TenantExpireTime）
**阶段 2**：创建领域事件（TenantCreatedEvent, TenantDisabledEvent, TenantDeletedEvent）
**阶段 3**：创建仓储接口（TenantRepository, TenantPageQuery）+ 领域服务接口（TenantUniquenessChecker）
**阶段 4**：创建 Tenant 聚合根 + TenantFactory
**阶段 5**：实现基础设施层（TenantRepositoryImpl, TenantUniquenessCheckerImpl）
**阶段 6**：创建应用层（TenantApplicationService）, 迁移编排逻辑
**阶段 7**：适配 TenantController，精简 TenantServiceImpl
**阶段 8**：编译验证
