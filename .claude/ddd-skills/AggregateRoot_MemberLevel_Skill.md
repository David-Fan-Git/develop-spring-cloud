# DDD Skill: AggregateRoot_MemberLevel_Skill

## 1. 技能名称

`AggregateRoot_MemberLevel_Skill` — 会员域（Member Domain）多个聚合根的领域建模与重构技能

## 2. 适用场景

本技能针对 **会员域** 的完整生命周期管理，覆盖以下 6 个聚合根的 CRUD + 领域逻辑：

| 聚合根 | 领域包 | 模块 | 核心职责 |
|--------|--------|------|----------|
| MemberLevel | `domain/level/` | 会员等级 | 等级定义（名称、级别、经验值、折扣）、等级升降级计算、等级变更追踪 |
| MemberGroup | `domain/group/` | 会员分组 | 用户分组管理、状态控制 |
| MemberTag | `domain/tag/` | 会员标签 | 标签管理、名称唯一性 |
| MemberPointRecord | `domain/point/` | 积分记录 | 积分变动记录、余额校验 |
| MemberSignInConfig | `domain/signin/` | 签到配置 | 签到天数规则配置（每日积分/经验） |
| MemberSignInRecord | `domain/signin/` | 签到记录 | 签到记录、连续签到天数、防重复签到 |

## 3. DDD 构造块

### 3.1 聚合根清单

#### MemberLevel — 会员等级

```
com.develop.mvp.pk.module.member.domain.level.MemberLevel
```

**角色**：会员等级的定义，封装等级名称、级别数值、升级所需经验值、折扣百分比，以及等级间的排序关系。

**聚合边界**：
- MemberLevel（根实体）
- 不包含：MemberUser（通过 levelId 引用）、MemberLevelRecord、MemberExperienceRecord（独立聚合/日志）

#### MemberGroup — 会员分组

```
com.develop.mvp.pk.module.member.domain.group.MemberGroup
```

**角色**：会员分组标签，用于对会员进行分组管理。

**聚合边界**：
- MemberGroup（根实体）
- 不包含：MemberUser（通过 groupId 引用）

#### MemberTag — 会员标签

```
com.develop.mvp.pk.module.member.domain.tag.MemberTag
```

**角色**：会员标签，用于对会员进行打标分类。

**聚合边界**：
- MemberTag（根实体）
- 不包含：MemberUser（通过 tagId 引用）

#### MemberPointRecord — 积分记录

```
com.develop.mvp.pk.module.member.domain.point.MemberPointRecord
```

**角色**：用户积分变动记录（只追加，不修改），每个业务行为导致积分变动时记录。

**聚合边界**：
- MemberPointRecord（根实体，只追加，不修改）
- 不包含：MemberUser（通过 userId 引用）

#### MemberSignInConfig — 签到配置

```
com.develop.mvp.pk.module.member.domain.signin.MemberSignInConfig
```

**角色**：签到天数奖励规则配置（第 N 天签到获得 x 积分 + y 经验）。

**聚合边界**：
- MemberSignInConfig（根实体）
- 不包含：MemberSignInRecord（独立聚合）

#### MemberSignInRecord — 签到记录

```
com.develop.mvp.pk.module.member.domain.signin.MemberSignInRecord
```

**角色**：会员签到记录，追踪每日签到、连续签到天数。

**聚合边界**：
- MemberSignInRecord（根实体）
- 不包含：MemberUser（通过 userId 引用）、MemberSignInConfig（通过 day 关联）

### 3.2 值对象（Value Objects）

| 聚合 | 值对象 | 类名 | 封装字段 | 不可变 | 自校验 |
|------|--------|------|---------|--------|--------|
| MemberLevel | 等级名称 | `LevelName` | `String value` | ✅ | 非空、全局唯一 |
| MemberLevel | 等级值 | `LevelValue` | `Integer value` | ✅ | 0-100、全局唯一 |
| MemberLevel | 折扣百分比 | `DiscountPercent` | `Integer value` | ✅ | 0-100 |
| MemberLevel | 经验范围 | `ExperienceRange` | `Integer min, max` | ✅ | min>=0, max>min, 不与其他等级重叠 |
| MemberGroup | 分组状态 | `GroupStatus` | `Integer code` | ✅ | ENABLE(0)/DISABLE(1) |
| MemberTag | 标签名称 | `TagName` | `String value` | ✅ | 非空、全局唯一 |
| MemberPointRecord | 积分变动值 | `PointDelta` | `Integer value` | ✅ | 变动值 |
| MemberPointRecord | 业务类型引用 | `PointBizRef` | `bizType, bizId` | ✅ | 非空 |
| MemberSignInConfig | 签到天数 | `SignInDay` | `Integer value` | ✅ | day>=1、全局唯一 |
| MemberSignInRecord | 连续天数 | `ContinuousDay` | `Integer value` | ✅ | day>=1 |

### 3.3 仓储接口（Repository Interfaces）

```java
// domain/level/repository/MemberLevelRepository.java
public interface MemberLevelRepository {
    MemberLevel save(MemberLevel level);
    void delete(Long id);
    MemberLevel findById(Long id);
    List<MemberLevel> findByIds(Collection<Long> ids);
    List<MemberLevel> findByNameLike(String name);
    List<MemberLevel> findByStatus(Integer status);
    List<MemberLevel> findAll();
}

// domain/group/repository/MemberGroupRepository.java
public interface MemberGroupRepository {
    MemberGroup save(MemberGroup group);
    void delete(Long id);
    MemberGroup findById(Long id);
    List<MemberGroup> findByIds(Collection<Long> ids);
    List<MemberGroup> findByStatus(Integer status);
    PageResult<MemberGroup> findPage(MemberGroupPageQuery query);
    List<MemberGroup> findAll();
}

// domain/tag/repository/MemberTagRepository.java
public interface MemberTagRepository {
    MemberTag save(MemberTag tag);
    void delete(Long id);
    MemberTag findById(Long id);
    List<MemberTag> findByIds(Collection<Long> ids);
    List<MemberTag> findAll();
    PageResult<MemberTag> findPage(MemberTagPageQuery query);
    List<MemberTag> findByNameLike(String name);
    Optional<MemberTag> findByName(String name);
}

// domain/point/repository/MemberPointRecordRepository.java
public interface MemberPointRecordRepository {
    void save(MemberPointRecord record);
    MemberPointRecord findById(Long id);
    PageResult<MemberPointRecord> findPage(MemberPointRecordPageQuery query);
    PageResult<MemberPointRecord> findByUserId(Long userId, PageParam pageParam);
}

// domain/signin/repository/MemberSignInConfigRepository.java
public interface MemberSignInConfigRepository {
    MemberSignInConfig save(MemberSignInConfig config);
    void delete(Long id);
    MemberSignInConfig findById(Long id);
    List<MemberSignInConfig> findAll();
    List<MemberSignInConfig> findByStatus(Integer status);
    Optional<MemberSignInConfig> findByDay(Integer day);
}

// domain/signin/repository/MemberSignInRecordRepository.java
public interface MemberSignInRecordRepository {
    void save(MemberSignInRecord record);
    MemberSignInRecord findById(Long id);
    PageResult<MemberSignInRecord> findPage(MemberSignInRecordPageQuery query);
    PageResult<MemberSignInRecord> findByUserId(Long userId, PageParam pageParam);
    Optional<MemberSignInRecord> findLastByUserId(Long userId);
    Long countByUserId(Long userId);
}
```

### 3.4 领域服务（Domain Service）

| 领域服务 | 职责 | 原因 | 对应原代码位置 |
|---------|------|------|-------------|
| `LevelUpgrader` | 根据经验值计算会员应升级到的等级 | 需要跨所有等级配置计算，属于领域服务 | `MemberLevelServiceImpl.java:270-292` `calculateNewLevel()` |
| `PointBalanceChecker` | 校验用户积分余额是否充足 | 需要联动 MemberUser 跨聚合查询 | `MemberPointRecordServiceImpl.java:73-80` |

### 3.5 领域事件（Domain Events）

| 聚合 | 事件 | 触发时机 | 携带数据 | 消费者 |
|------|------|---------|---------|--------|
| MemberLevel | `LevelChangedEvent` | 会员等级变更后 | userId, oldLevelId, newLevelId, experience | 通知、操作日志 |
| MemberLevel | `LevelDeletedEvent` | 等级删除后 | levelId, name | 操作日志 |
| MemberPointRecord | `PointChangedEvent` | 积分变动后 | userId, point, totalPoint, bizType, bizId | 通知、操作日志 |
| MemberSignInRecord | `SignInCompletedEvent` | 签到成功后 | userId, day, point, experience | 通知、积分/经验发放 |

## 4. 职责边界

### 4.1 各聚合根必须负责的规则

#### MemberLevel 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-L01 | 等级名称在全局不可重复 | `MemberLevelServiceImpl.java:98-107` `validateNameUnique()` |
| R-L02 | 等级值（level 字段）在全局不可重复 | `MemberLevelServiceImpl.java:110-119` `validateLevelUnique()` |
| R-L03 | 升级所需经验值必须大于前一个等级的经验值、小于后一个等级的经验值 | `MemberLevelServiceImpl.java:123-141` `validateExperienceOutRange()` |
| R-L04 | 等级下有用户时不可删除 | `MemberLevelServiceImpl.java:155-159` `validateLevelHasUser()` 调用 `memberUserService.getUserCountByLevelId()` |
| R-L05 | 计算新等级时：取经验值 >= 升级经验的所有等级中的最高级 | `MemberLevelServiceImpl.java:270-292` `calculateNewLevel()` 使用 `.max(Comparator.comparing(MemberLevelDO::getLevel))` |
| R-L06 | 等级未变化时（新旧 levelId 相同），不产生变更事件 | `MemberLevelServiceImpl.java:193` `if (ObjUtil.equal(user.getLevelId(), updateReqVO.getLevelId())) return;` |

#### MemberGroup 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-G01 | 分组下有用户时不可删除 | `MemberGroupServiceImpl.java:73-78` `validateGroupHasUser()` 调用 `memberUserService.getUserCountByGroupId(id)` |

#### MemberTag 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-T01 | 标签名称在全局不可重复 | `MemberTagServiceImpl.java:77-92` `validateTagNameUnique()` 通过 `selelctByName()` 校验 |
| R-T02 | 标签下有用户时不可删除 | `MemberTagServiceImpl.java:95-99` `validateTagHasUser()` 调用 `memberUserService.getUserCountByTagId(id)` |

#### MemberPointRecord 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-P01 | 积分变动值为 0 时跳过处理 | `MemberPointRecordServiceImpl.java:69-71` `if (point == 0) return;` |
| R-P02 | 积分变动后余额不可为负数 | `MemberPointRecordServiceImpl.java:75-79` `totalPoint = userPoint + point; if (totalPoint < 0)` 抛警告并 return |
| R-P03 | 并发情况下通过乐观锁/数据库更新防止积分为负 | `MemberPointRecordServiceImpl.java:83-85` `boolean success = memberUserService.updateUserPoint(userId, point)` 返回失败时抛 `USER_POINT_NOT_ENOUGH` |

#### MemberSignInConfig 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-SC01 | 签到天数（day）不可重复 | `MemberSignInConfigServiceImpl.java:75-84` `validateSignInConfigDayDuplicate()` |
| R-SC02 | 签到配置列表按 day 升序排列 | `MemberSignInConfigServiceImpl.java:95` `list.sort(Comparator.comparing(MemberSignInConfigDO::getDay))` |

#### MemberSignInRecord 聚合根规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R-SR01 | 同一用户一天只能签到一次 | `MemberSignInRecordServiceImpl.java:136-142` `validateSigned()` 检查上次签到是否为今天，是则抛 `SIGN_IN_RECORD_TODAY_EXISTS` |
| R-SR02 | 连续签到天数计算：上次签到是昨天则连续+1，否则重置为1 | `MemberSignInRecordServiceImpl.java:120` — 通过 `MemberSignInRecordConvert.INSTANCE.convert(userId, lastRecord, signInConfigs)` 实现 |
| R-SR03 | 签到成功后异步增加积分和经验 | `MemberSignInRecordServiceImpl.java:126-132` — 积分通过 `pointRecordService.createPointRecord()`，经验通过 `memberLevelService.addExperience()` |
| R-SR04 | 签到记录摘要：总天数、连续天数、今日是否已签到 | `MemberSignInRecordServiceImpl.java:57-86` `getSignInRecordSummary()` |

### 4.2 严禁外泄的职责

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接调用 Mapper/操作 DO | 破坏持久化无关性 | RepositoryImpl |
| 直接操作 MemberUser 的积分/等级字段 | MemberUser 是独立聚合 | 应用层通过 MemberUserRepository 更新 |
| 计算会员等级时查询数据库 | 应使用 LevelUpgrader 领域服务 | LevelUpgrader 领域服务 |
| Excel 导入导出 | 表示层关注点 | Controller/Convert |
| 发送通知消息（等级变更/签到提醒） | 基础设施关注点 | 领域事件订阅者 |
| 记录操作日志 | 基础设施关注点 | 领域事件订阅者 |

## 5. 依赖与协作

### 5.1 领域层依赖（向内）

每个聚合根仅依赖：
- 自身值对象
- 仓储接口
- 领域事件接口

### 5.2 跨聚合协作（仅通过 ID 引用）

| 源聚合 | 目标聚合 | 引用方式 | 协作场景 |
|-------|---------|---------|---------|
| MemberLevel | MemberUser | `levelId: Long` | 等级变更时更新 user.levelId |
| MemberGroup | MemberUser | `groupId: Long` | 删除分组时校验是否有用户引用 |
| MemberTag | MemberUser | `tagId: Long` | 删除标签时校验是否有用户引用 |
| MemberPointRecord | MemberUser | `userId: Long` | 积分变动后更新 user.point |
| MemberSignInRecord | MemberSignInConfig | `day: Integer` | 签到完成后根据 day 获取奖励配置 |
| MemberSignInRecord | MemberLevel | `experience: Integer` | 签到获得经验后触发等级升级计算 |

### 5.3 基础设施依赖（向外，通过接口倒置）

```
领域层定义接口                               基础设施层实现
─────────────                               ──────────────
MemberLevelRepository           ←──         MemberLevelRepositoryImpl (委托 MemberLevelMapper)
MemberGroupRepository           ←──         MemberGroupRepositoryImpl (委托 MemberGroupMapper)
MemberTagRepository             ←──         MemberTagRepositoryImpl (委托 MemberTagMapper)
MemberPointRecordRepository     ←──         MemberPointRecordRepositoryImpl (委托 MemberPointRecordMapper)
MemberSignInConfigRepository    ←──         MemberSignInConfigRepositoryImpl (委托 MemberSignInConfigMapper)
MemberSignInRecordRepository    ←──         MemberSignInRecordRepositoryImpl (委托 MemberSignInRecordMapper)
DomainEventPublisher            ←──         SpringDomainEventPublisher (委托 Spring ApplicationEventPublisher)
```

## 6. 不变式与约束（Invariants）

| 编号 | 不变式 | 聚合 | 类型 | 验证点 |
|------|--------|------|------|--------|
| I01 | 等级名称在全局不可重复 | MemberLevel | 跨聚合唯一性 | 创建/修改时 |
| I02 | 等级值（level）在全局不可重复 | MemberLevel | 跨聚合唯一性 | 创建/修改时 |
| I03 | 等级经验值范围不可重叠（前一个 < 当前 < 后一个） | MemberLevel | 聚合间约束 | 创建/修改时 |
| I04 | 有用户引用的等级不可删除 | MemberLevel | 聚合外部约束 | 删除时 |
| I05 | 标签名称在全局不可重复 | MemberTag | 跨聚合唯一性 | 创建/修改时 |
| I06 | 有用户引用的标签不可删除 | MemberTag | 聚合外部约束 | 删除时 |
| I07 | 有用户引用的分组不可删除 | MemberGroup | 聚合外部约束 | 删除时 |
| I08 | 签到天数（day）不可重复 | MemberSignInConfig | 跨聚合唯一性 | 创建/修改时 |
| I09 | 同一用户一天只能签到一次 | MemberSignInRecord | 聚合内部 | 签到前校验 |
| I10 | 积分变动后总余额不可为负数 | MemberPointRecord | 聚合外部约束 | 创建积分记录时 |
| I11 | 积分变动值为 0 时跳过处理 | MemberPointRecord | 应用层约束 | 创建积分记录时 |
| I12 | 等级经验值为 null 时不做升降级计算 | MemberLevel | 应用层约束 | 等级变更时 |

## 7. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | MemberLevel、MemberGroup、MemberTag、MemberPointRecord、MemberSignInConfig、MemberSignInRecord 均无 MyBatis/Spring 注解 | 代码审查 |
| AC02 | 所有值对象是 final class，字段是 final，无 setter | 代码审查 |
| AC03 | 所有仓储接口定义在 `domain/{aggregate}/repository/` 包，不 import MyBatis 类 | 代码审查 |
| AC04 | 所有 RepositoryImpl 在 `infrastructure/{aggregate}/` 包，负责 DO 与领域模型映射 | 代码审查 |
| AC05 | ApplicationService 在 `application/{aggregate}/` 包，使用 `@Transactional` 管理事务 | 代码审查 |
| AC06 | MemberLevel 聚合根提供业务方法如 `updateConfig()`, `enable()`, `disable()`，名称体现业务语义 | 代码审查 |
| AC07 | 等级经验值范围校验封装在 `ExperienceRange` 值对象或 `LevelUpgrader` 领域服务中 | 代码审查 |
| AC08 | MemberLevelServiceImpl 等旧 Service 仅保留编排逻辑，所有业务规则迁移到对应聚合根 | 代码审查 |
| AC09 | 签到配置按 day 升序排列的逻辑由仓储层或应用层保证，不在聚合根内 | 代码审查 |
| AC10 | 积分变动跨聚合协作（更新 MemberUser.point）通过 ApplicationService 编排 | 代码审查 |
| AC11 | 等级计算（calculateNewLevel）封装为 `LevelUpgrader` 领域服务 | 代码审查 |
| AC12 | 签到流水和积分/经验发放为同一个事务 | 代码审查 |
| AC13 | 编译通过，Controller 行为无回归 | 编译 + 集成测试 |
| AC14 | 等级升降级测试通过：经验增加时升级、经验扣减时降级 | 单元测试 |
| AC15 | 签到唯一性测试通过：同用户同一天不能两次签到 | 单元测试 |
| AC16 | 积分负余额测试通过：积分不足时操作失败 | 单元测试 |

## 8. 目录结构规划（重构后）

```
develop-module-member/develop-module-member-server/src/main/java/com/develop/mvp/pk/module/member/
├── domain/                                              # 领域层
│   ├── level/
│   │   ├── MemberLevel.java                             # 聚合根
│   │   ├── event/
│   │   │   ├── LevelChangedEvent.java
│   │   │   └── LevelDeletedEvent.java
│   │   ├── service/
│   │   │   └── LevelUpgrader.java                       # 领域服务
│   │   └── repository/
│   │       └── MemberLevelRepository.java
│   ├── group/
│   │   ├── MemberGroup.java                             # 聚合根
│   │   ├── valueobject/
│   │   │   └── GroupStatus.java
│   │   └── repository/
│   │       └── MemberGroupRepository.java
│   ├── tag/
│   │   ├── MemberTag.java                               # 聚合根
│   │   ├── valueobject/
│   │   │   └── TagName.java
│   │   └── repository/
│   │       └── MemberTagRepository.java
│   ├── point/
│   │   ├── MemberPointRecord.java                       # 聚合根
│   │   ├── valueobject/
│   │   │   ├── PointDelta.java
│   │   │   └── PointBizRef.java
│   │   ├── event/
│   │   │   └── PointChangedEvent.java
│   │   └── repository/
│   │       └── MemberPointRecordRepository.java
│   └── signin/
│       ├── MemberSignInConfig.java                      # 聚合根
│       ├── MemberSignInRecord.java                      # 聚合根
│       ├── valueobject/
│       │   └── SignInDay.java
│       ├── event/
│       │   └── SignInCompletedEvent.java
│       └── repository/
│           ├── MemberSignInConfigRepository.java
│           └── MemberSignInRecordRepository.java
├── application/                                         # 应用层
│   ├── level/
│   │   └── MemberLevelApplicationService.java
│   ├── group/
│   │   └── MemberGroupApplicationService.java
│   ├── tag/
│   │   └── MemberTagApplicationService.java
│   ├── point/
│   │   └── MemberPointApplicationService.java
│   └── signin/
│       ├── MemberSignInConfigApplicationService.java
│       └── MemberSignInRecordApplicationService.java
├── infrastructure/                                      # 基础设施层
│   ├── level/
│   │   └── MemberLevelRepositoryImpl.java
│   ├── group/
│   │   └── MemberGroupRepositoryImpl.java
│   ├── tag/
│   │   └── MemberTagRepositoryImpl.java
│   ├── point/
│   │   └── MemberPointRecordRepositoryImpl.java
│   └── signin/
│       ├── MemberSignInConfigRepositoryImpl.java
│       └── MemberSignInRecordRepositoryImpl.java
├── controller/                                          # 接口层（保留）
├── dal/                                                 # 数据访问层（保留）
└── convert/                                             # 转换层（保留）
```

## 9. 回滚条件

如果以下任一情况发生，应回滚当前修改并重新分析：

1. 编译失败
2. 聚合根内部注入了 Mapper/基础设施依赖
3. 值对象存在 setter 或可变字段
4. 等级升降级计算逻辑从聚合根泄漏回旧 Service
5. 积分余额并发控制失效（通过乐观锁 update 返回值校验）
6. 签到防重复校验失效（同用户同一天多次签到）
7. 等级、标签、分组删除时未校验用户引用导致数据不一致
8. 原有 Controller 接口行为出现回归（例如积分分页参数改变）
9. 事务边界混乱：签到+积分+经验未在同一个事务中完成

## 10. 分步执行计划

### 阶段 1：创建值对象（通用型，无依赖）
- LevelName、LevelValue、DiscountPercent、ExperienceRange
- GroupStatus、TagName
- PointDelta、PointBizRef
- SignInDay

### 阶段 2：创建独立聚合根（MemberTag、MemberGroup、MemberSignInConfig）
- MemberTag 聚合根（rename 方法封装名称更新）
- MemberGroup 聚合根（updateInfo、enable、disable）
- MemberSignInConfig 聚合根（update、enable、disable）

### 阶段 3：创建事件型聚合根（MemberPointRecord、MemberSignInRecord）
- MemberPointRecord 聚合根（只追加，create → save）
- MemberSignInRecord 聚合根（createSignIn、validateSigned）

### 阶段 4：创建复杂聚合根（MemberLevel）
- MemberLevel 聚合根（updateConfig、enable、disable）
- LevelUpgrader 领域服务（calculateNewLevel，引用所有等级配置）
- 注意：等级之间的经验值范围校验是核心复杂度

### 阶段 5：创建仓储接口 + RepositoryImpl（每个聚合根对）
- 领域层定义接口
- 基础设施层实现（委托 Mapper + DO ↔ Domain 转换）

### 阶段 6：创建领域事件 + 事件订阅者
- LevelChangedEvent
- PointChangedEvent
- SignInCompletedEvent

### 阶段 7：创建 ApplicationService，迁移编排逻辑
- 签到 ApplicationService 编排：保存记录 → 发放积分 → 增加经验 → 等级升降级
- 积分 ApplicationService 编排：检查余额 → 扣/增积分 → 创建记录
- 等级 ApplicationService 编排：校验名称/等级/经验范围唯一性

### 阶段 8：适配 Controller，精简旧 Service，编译验证
- Controller 注入 ApplicationService
- 编译通过 + 集成测试
