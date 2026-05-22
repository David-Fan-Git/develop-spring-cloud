# DDD Skill: AggregateRoot_User_Validation_Skill

## 1. 技能名称

`AggregateRoot_User_Validation_Skill` — 管理后台用户（AdminUser）聚合根的领域建模与重构技能

## 2. 适用场景

本技能针对 **管理后台用户（AdminUser）** 的完整生命周期管理，覆盖以下业务操作：

- 创建用户（含注册）
- 修改用户基本信息及岗位关联
- 修改用户状态（启用/禁用）
- 删除用户（单个/批量）
- 修改密码（个人修改/管理员重置）
- 修改个人资料
- 记录登录信息
- 批量导入用户
- 用户查询（按ID、用户名、手机、邮箱、部门、岗位、昵称、状态）
- 用户校验（存在性+启用状态）

## 3. DDD 构造块

### 3.1 聚合根：User

```
com.develop.mvp.pk.module.system.domain.user.User
```

**角色**：管理后台用户的领域聚合根，封装用户的完整生命周期和业务规则。

**聚合边界**：
- User（根实体）
- UserPost（聚合内部实体，表示用户-岗位关联）
- 不包含：Dept、Role、Permission、Tenant — 这些是外部聚合，仅通过 ID 引用

### 3.2 值对象（Value Objects）

| 值对象 | 类名 | 封装字段 | 不可变 | 自校验 |
|--------|------|---------|--------|--------|
| 用户名 | `Username` | `String value` | ✅ | 非空、格式校验 |
| 加密密码 | `EncodedPassword` | `String encodedValue` | ✅ | BCrypt 格式校验 |
| 明文密码 | `RawPassword` | `String rawValue` | ✅ | 长度≥6、非空 |
| 邮箱 | `Email` | `String value` | ✅ | 邮箱格式 |
| 手机号 | `Mobile` | `String value` | ✅ | 手机号格式 |
| 用户资料 | `UserProfile` | nickname, avatar, sex, remark | ❌ (整体替换) | 昵称长度 |
| 用户状态 | `UserStatus` | `Integer code` | ✅ | 只能是 ENABLE/DISABLE |
| 登录记录 | `LoginRecord` | loginIp, loginDate | ✅ | IP 格式、日期非空 |

### 3.3 仓储接口（Repository，领域层）

```
com.develop.mvp.pk.module.system.domain.user.UserRepository
```

领域层定义的接口，不依赖任何基础设施：

```java
public interface UserRepository {
    User save(User user);
    void delete(UserId id);
    User findById(UserId id);
    Optional<User> findByUsername(Username username);
    Optional<User> findByEmail(Email email);
    Optional<User> findByMobile(Mobile mobile);
    List<User> findByIds(Collection<UserId> ids);
    List<User> findByDeptIds(Collection<Long> deptIds);
    List<User> findByPostIds(Collection<Long> postIds);
    List<User> findByNickname(String nickname);
    List<User> findByStatus(UserStatus status);
    PageResult<User> findPage(UserPageQuery query);
    boolean existsByUsername(Username username);
    boolean existsByEmail(Email email);
    boolean existsByMobile(Mobile mobile);
    long count();
}
```

### 3.4 仓储实现（RepositoryImpl，基础设施层）

```
com.develop.mvp.pk.module.system.infrastructure.user.UserRepositoryImpl
```

在基础设施层实现 `UserRepository`，内部委托给现有的 MyBatis Mapper。负责：
- User 聚合根 ↔ AdminUserDO + UserPostDO 的映射
- 事务管理

### 3.5 领域服务（Domain Service）

| 领域服务 | 职责 | 原因 |
|---------|------|------|
| `PasswordEncoder` | 密码加密与匹配 | 加密算法是领域概念，不应直接依赖 Spring Security |
| `UserUniquenessChecker` | 检查用户名/邮箱/手机号唯一性 | 需要跨聚合查询，属于领域服务 |

**注意**：`PasswordEncoder` 在领域层定义为接口，基础设施层使用 Spring Security 的 `BCryptPasswordEncoder` 实现。

### 3.6 领域事件（Domain Events）

| 事件 | 触发时机 | 携带数据 | 消费者 |
|------|---------|---------|--------|
| `UserCreatedEvent` | 用户创建成功后 | userId, username, tenantId | 操作日志记录、通知 |
| `UserDisabledEvent` | 用户状态变为禁用 | userId | OAuth2 Token 清理 |
| `UserDeletedEvent` | 用户删除成功后 | userId, username | 权限清理、岗位清理、操作日志 |
| `UserPasswordChangedEvent` | 密码变更成功后 | userId | 通知、Token 失效（可选） |
| `UserProfileUpdatedEvent` | 个人资料更新后 | userId | 操作日志 |
| `UserLoggedInEvent` | 登录信息更新后 | userId, loginIp, loginDate | 登录日志 |

### 3.7 工厂（Factory）

`UserFactory`：负责创建复杂的 User 聚合，特别是：
- 创建时：组装 Username、RawPassword → EncodedPassword、UserProfile
- 导入时：从 Excel VO 重建 User 聚合
- 注册时：带默认状态的 User 创建

### 3.8 规约（Specification）

`UserSpecification`：封装用户查询的复杂条件逻辑，如：
- 按部门范围查询（包含子部门）
- 按角色筛选用户

## 4. 职责边界

### 4.1 User 聚合根必须负责的规则

| 规则编号 | 规则描述 | 对应原代码位置 |
|---------|---------|-------------|
| R01 | 创建时，必须对明文密码进行 BCrypt 加密后才能存储 | `encodePassword()` L548 |
| R02 | 创建/修改时，必须校验用户名在租户内唯一 | `validateUsernameUnique()` L406 |
| R03 | 创建/修改时，必须校验邮箱在租户内唯一（如提供） | `validateEmailUnique()` L424 |
| R04 | 创建/修改时，必须校验手机号在租户内唯一（如提供） | `validateMobileUnique()` L442 |
| R05 | 修改密码时，必须校验旧密码匹配 | `validateOldPassword()` L465 |
| R06 | 修改密码时，必须对新密码进行 BCrypt 加密 | `updateUserPassword()` L205 |
| R07 | 禁用用户时，状态变更为 DISABLE | `updateUserStatus()` L232-234 |
| R08 | 删除用户时，必须同时清理岗位关联、权限数据 | `deleteUser()` L252-256 |
| R09 | 创建时，默认状态为 ENABLE | `createUser()` L108 |
| R10 | 导入用户时，如用户名已存在且不允许更新，则拒绝 | `importUserList()` L519-522 |
| R11 | 用户状态只能是 ENABLE(0) 或 DISABLE(1) | UserStatus 值对象 |
| R12 | 注册用户时，必须检查系统是否开启注册功能 | `registerUser()` L125-127 |
| R13 | 创建/注册时，必须检查租户配额是否已满 | `createUser()` L97-101 |

### 4.2 严禁外泄的职责（不可放在 User 聚合内）

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接操作数据库/调用 Mapper | 破坏持久化无关性 | Repository 实现 |
| 校验部门是否存在且启用 | Dept 是独立聚合 | DeptDomainService 或应用层 |
| 校验岗位是否存在且启用 | Post 是独立聚合 | PostDomainService 或应用层 |
| 处理角色/权限分配 | Permission 是独立聚合 | PermissionDomainService |
| 读取系统配置（注册开关等） | 基础设施关注点 | 应用层传入 |
| 校验租户配额（直接查租户） | Tenant 是独立聚合 | 应用层或领域服务 |
| 处理 UI 层的 VO 转换 | 表示层关注点 | Controller/Convert |
| 记录操作日志 | 基础设施关注点 | 领域事件订阅者 |
| Excel 导入导出逻辑 | 基础设施/应用关注点 | 应用层 Service |

## 5. 依赖与协作

### 5.1 领域层依赖（向内）

User 聚合根仅依赖：
- 自身值对象（Username, Password, Email, Mobile, UserProfile, UserStatus, LoginRecord）
- 领域服务接口（PasswordEncoder、UserUniquenessChecker）
- 仓储接口（UserRepository）
- 领域事件发布器（DomainEventPublisher）

### 5.2 跨聚合协作（仅通过 ID 引用）

| 外部聚合 | 引用方式 | 协作场景 |
|---------|---------|---------|
| Dept（部门） | `deptId: Long` | 创建/修改时校验部门存在性（应用层负责） |
| Post（岗位） | `postIds: Set<Long>` | 通过 UserPost 关联表（聚合内部管理） |
| Tenant（租户） | `tenantId: Long` | 创建时校验配额（应用层负责） |
| Permission（权限） | 无直接引用 | 删除时发布 UserDeletedEvent |
| OAuth2Token（令牌） | 无直接引用 | 禁用时发布 UserDisabledEvent |

### 5.3 基础设施依赖（向外，通过接口倒置）

```
领域层定义接口                   基础设施层实现
─────────────                   ──────────────
UserRepository       ←──        UserRepositoryImpl (委托 AdminUserMapper)
PasswordEncoder      ←──        BCryptPasswordEncoderAdapter (委托 Spring Security)
DomainEventPublisher ←──        SpringDomainEventPublisher (委托 Spring ApplicationEventPublisher)
```

## 6. 不变式与约束（Invariants）

以下规则在 User 聚合的整个生命周期中永远为真：

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I01 | `username` 在同一个租户内不可重复 | 跨聚合唯一性 | 创建/修改时 |
| I02 | `email` 在同一个租户内不可重复（非空时） | 跨聚合唯一性 | 创建/修改时 |
| I03 | `mobile` 在同一个租户内不可重复（非空时） | 跨聚合唯一性 | 创建/修改时 |
| I04 | `password` 永远以 BCrypt 密文存储，不可明文回读 | 聚合内部 | 创建/修改密码时 |
| I05 | `status` 只能是 ENABLE 或 DISABLE | 聚合内部（值对象） | 状态变更时 |
| I06 | 被禁用的用户无法登录 | 聚合外部（应用层） | 认证时 |
| I07 | 删除用户时，其所有岗位关联必须同时删除 | 聚合内部 | 删除时 |
| I08 | 租户下用户数量不得超过租户套餐配额 | 跨聚合约束 | 创建时 |
| I09 | 注册功能受系统配置控制（未开启则禁止注册） | 应用层约束 | 注册时 |
| I10 | 修改密码时必须提供正确的旧密码 | 聚合内部 | 个人修改密码时 |
| I11 | 旧密码不能与新密码相同 | 聚合内部（可选校验） | 个人修改密码时 |

## 7. 验收标准

每一条必须可独立验证，通过检查代码和运行测试来确认。

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | User 类不包含任何 MyBatis/Spring 注解（`@TableName`, `@TableId` 等） | 代码审查 |
| AC02 | User 类不直接注入或调用 Mapper/Repository 实现类 | 代码审查 |
| AC03 | User 构造方法或工厂方法确保所有不变式在创建时得到满足 | 单元测试 |
| AC04 | `Username`、`Email`、`Mobile`、`EncodedPassword`、`UserStatus` 为不可变值对象（final 字段，无 setter） | 代码审查 |
| AC05 | `UserProfile` 为整体替换的值对象（提供 `withXxx()` 方法而非 setter） | 代码审查 |
| AC06 | UserRepository 接口定义在领域层包（`domain.user`），不 import MyBatis 类 | 代码审查 |
| AC07 | UserRepositoryImpl 在基础设施层（`infrastructure.user`），import MyBatis 类并负责 DO↔领域模型映射 | 代码审查 |
| AC08 | User 聚合的公共方法（`disable()`, `changePassword()`, `updateProfile()` 等）名称体现业务语义 | 代码审查 |
| AC09 | AdminUserServiceImpl 仅保留编排逻辑，所有业务规则迁移到 User 聚合或值对象 | 代码审查 |
| AC10 | 禁用用户时，UserDisabledEvent 被发布且被 OAuth2Token 清理订阅者消费 | 集成测试 |
| AC11 | 删除用户时，UserDeletedEvent 被发布且被权限清理、岗位清理订阅者消费 | 集成测试 |
| AC12 | 创建用户时，加密密码在 User 聚合内部完成（不在 Service 中手动调用 encode） | 代码审查 |
| AC13 | 唯一性校验通过 UserUniquenessChecker 领域服务接口完成（不在聚合根内直接查 DB） | 代码审查 |
| AC14 | 所有现有单元测试 `AdminUserServiceImplTest` 通过（适配新的领域模型后） | 运行测试 |
| AC15 | 密码加密/匹配通过领域层 PasswordEncoder 接口调用，不直接依赖 Spring Security | 代码审查 |
| AC16 | User 聚合提供 `recordLogin(LoginRecord)` 方法，而非直接 setLoginIp/setLoginDate | 代码审查 |
| AC17 | 每个类/方法注释中标明遵循的技能名称和对应规则编号 | 代码审查 |

## 8. 目录结构规划（重构后）

```
develop-module-system/develop-module-system-server/src/main/java/com/develop/mvp/pk/module/system/
├── domain/                                          # 领域层（新建）
│   └── user/
│       ├── User.java                                # 聚合根
│       ├── UserId.java                              # 聚合根标识（值对象）
│       ├── UserPost.java                            # 聚合内部实体
│       ├── valueobject/
│       │   ├── Username.java
│       │   ├── RawPassword.java
│       │   ├── EncodedPassword.java
│       │   ├── Email.java
│       │   ├── Mobile.java
│       │   ├── UserProfile.java
│       │   ├── UserStatus.java
│       │   └── LoginRecord.java
│       ├── event/
│       │   ├── UserCreatedEvent.java
│       │   ├── UserDisabledEvent.java
│       │   ├── UserDeletedEvent.java
│       │   ├── UserPasswordChangedEvent.java
│       │   ├── UserProfileUpdatedEvent.java
│       │   └── UserLoggedInEvent.java
│       ├── service/
│       │   ├── PasswordEncoder.java                 # 领域服务接口
│       │   └── UserUniquenessChecker.java           # 领域服务接口
│       ├── repository/
│       │   └── UserRepository.java                  # 仓储接口
│       ├── factory/
│       │   └── UserFactory.java                     # 工厂
│       └── specification/
│           └── UserSpecification.java               # 规约
├── application/                                     # 应用层（新建）
│   └── user/
│       ├── UserApplicationService.java              # 应用服务（编排）
│       ├── command/
│       │   ├── CreateUserCommand.java
│       │   ├── UpdateUserCommand.java
│       │   ├── DisableUserCommand.java
│       │   ├── DeleteUserCommand.java
│       │   ├── ChangePasswordCommand.java
│       │   └── ImportUserCommand.java
│       └── query/
│           ├── UserPageQuery.java
│           └── UserDetailQuery.java
├── infrastructure/                                  # 基础设施层（新建）
│   └── user/
│       ├── UserRepositoryImpl.java                  # 仓储实现
│       ├── BCryptPasswordEncoderAdapter.java        # 密码加密器实现
│       ├── SpringDomainEventPublisher.java          # 事件发布器实现
│       └── subscriber/
│           ├── UserDisabledTokenCleaner.java         # UserDisabledEvent 订阅者
│           └── UserDeletedPermissionCleaner.java     # UserDeletedEvent 订阅者
├── controller/                                      # 接口层（保留，精简）
│   └── admin/user/UserController.java
├── dal/                                             # 数据访问层（保留，重构）
│   ├── dataobject/user/AdminUserDO.java
│   └── mysql/user/AdminUserMapper.java
└── convert/                                         # 转换层（保留）
    └── user/UserConvert.java
```

## 9. 回滚条件

如果以下任一情况发生，应回滚当前修改并重新分析：

1. 单元测试无法通过（编译失败或行为回归）
2. 聚合根内部注入了基础设施依赖（循环依赖或违反依赖倒置）
3. 值对象被发现有 setter 或可变字段
4. 业务规则从聚合根泄漏回 Service 层
5. 跨聚合操作未通过领域事件解耦

## 10. 分步执行计划（Skill 内嵌）

由于当前 `AdminUserServiceImpl` 是「上帝服务」，需要分阶段拆分：

**阶段 1**：创建值对象（Username, Email, Mobile, UserStatus, EncodedPassword, RawPassword, LoginRecord, UserProfile）
**阶段 2**：创建 User 聚合根 + UserId + UserPost
**阶段 3**：创建领域服务接口（PasswordEncoder, UserUniquenessChecker）+ 仓储接口（UserRepository）
**阶段 4**：创建领域事件类 + 事件发布器接口
**阶段 5**：创建工厂（UserFactory）
**阶段 6**：实现基础设施层（UserRepositoryImpl, BCryptPasswordEncoderAdapter, 事件订阅者）
**阶段 7**：创建应用层（UserApplicationService），迁移编排逻辑
**阶段 8**：精简原有 Service/Controller，适配新领域模型
**阶段 9**：更新测试，确保回归通过
