# DDD Skill: AggregateRoot_Role_Menu_Skill

## 1. 技能名称

`AggregateRoot_Role_Menu_Skill` — 角色(Role)与菜单(Menu)聚合根的领域建模与重构技能

## 2. 适用场景

本技能针对 **RBAC 权限模型** 的核心领域，覆盖：

- **Role**: 创建/更新/删除角色、数据权限设置、角色校验
- **Menu**: 创建/更新/删除菜单（树形结构）、菜单过滤
- **Permission**: 用户-角色分配、角色-菜单分配、权限判断、数据权限查询

## 3. DDD 构造块

### 3.1 聚合根

| 聚合根 | 类名 | 角色 |
|--------|------|------|
| Role | `domain.permission.Role` | 角色聚合根，封装角色身份、状态、数据范围，内部持有菜单关联 |
| Menu | `domain.permission.Menu` | 菜单聚合根，树形结构，封装菜单属性与层级校验 |

### 3.2 值对象（Role 聚合）

| 值对象 | 类名 | 封装字段 | 不变式 |
|--------|------|---------|--------|
| 角色ID | `RoleId` | Long value | 非空 |
| 角色名称 | `RoleName` | String value | 非空，同租户不可重复 |
| 角色编码 | `RoleCode` | String value | 非空，同租户不可重复，禁止使用 SUPER_ADMIN |
| 角色类型 | `RoleType` | Integer code | SYSTEM(1)/CUSTOM(2)，系统角色不可删除 |
| 角色状态 | `RoleStatus` | Integer code | ENABLE/DISABLE |
| 数据范围 | `DataScope` | Integer scope + Set<Long> deptIds | ALL/DEPT_CUSTOM/DEPT_ONLY/DEPT_AND_CHILD/SELF |

### 3.3 值对象（Menu 聚合）

| 值对象 | 类名 | 封装字段 | 不变式 |
|--------|------|---------|--------|
| 菜单ID | `MenuId` | Long value | 非空 |
| 菜单名称 | `MenuName` | String value | 同父节点下不可重复 |
| 菜单类型 | `MenuType` | Integer type | DIR(1)/MENU(2)/BUTTON(3) |
| 权限标识 | `MenuPermission` | String value | 格式: system:module:action |

### 3.4 仓储接口

- `RoleRepository` — 领域层接口，封装 Role 持久化
- `MenuRepository` — 领域层接口，封装 Menu 持久化

### 3.5 领域服务

| 领域服务 | 职责 |
|---------|------|
| `PermissionChecker` | 权限判断（hasAnyPermissions, hasAnyRoles） |
| `RoleMenuAssigner` | 角色-菜单分配（跨Role-Menu聚合） |
| `UserRoleAssigner` | 用户-角色分配（跨User-Role聚合） |

### 3.6 领域事件

| 事件 | 触发时机 |
|------|---------|
| `RoleCreatedEvent` | 角色创建成功 |
| `RoleDeletedEvent` | 角色删除（发布后由订阅者清理 UserRole + RoleMenu） |
| `MenuDeletedEvent` | 菜单删除（发布后由订阅者清理 RoleMenu） |
| `UserRoleAssignedEvent` | 用户分配角色成功 |

## 4. 职责边界

### 4.1 Role 聚合根规则

| 编号 | 规则 | 原代码 |
|------|------|--------|
| RR01 | 创建时默认状态 ENABLED，默认 DataScope=ALL | `createRole()` L64-65 |
| RR02 | 名称在同租户不可重复 | `validateRoleDuplicate()` L154-157 |
| RR03 | 编码在同租户不可重复 | `validateRoleDuplicate()` L163-167 |
| RR04 | 禁止使用 SUPER_ADMIN 编码创建角色 | `validateRoleDuplicate()` L150-152 |
| RR05 | 系统角色(type=SYSTEM)不可删除/修改 | `validateRoleForUpdate()` L181-183 |
| RR06 | 删除角色时需清理 UserRole + RoleMenu 关联数据 | `deleteRole()` L119 |

### 4.2 Menu 聚合根规则

| 编号 | 规则 | 原代码 |
|------|------|--------|
| MR01 | 父菜单必须存在且类型为 DIR 或 MENU | `validateParentMenu()` L222-239 |
| MR02 | 不能设置自己为父菜单 | `validateParentMenu()` L227-228 |
| MR03 | 同父节点下菜单名不可重复 | `validateMenuName()` L252-264 |
| MR04 | 组件名(componentName)全局不可重复 | `validateMenuComponentName()` L272-288 |
| MR05 | 删除菜单时，需先检查是否有子菜单 | `deleteMenu()` L94-96 |
| MR06 | 按钮类型菜单需清空 component/icon/path | `initMenuProperty()` L297-305 |

### 4.3 严禁外泄

| 禁止 | 应由谁处理 |
|------|----------|
| Role 聚合直接操作 RoleMapper | RoleRepository |
| Menu 聚合直接操作 MenuMapper | MenuRepository |
| 权限判断直接查数据库 | PermissionChecker 领域服务（内部用缓存） |
| Role 聚合包含 User 引用 | 仅通过 UserRole 关联表引用 userId |

## 5. 不变式

| 编号 | 不变式 |
|------|--------|
| RI01 | Role.name 在同租户内唯一 |
| RI02 | Role.code 在同租户内唯一 |
| RI03 | 系统角色(type=SYSTEM)永久存在，不可删除 |
| RI04 | Menu.parentId 必须指向存在的 DIR 或 MENU 类型节点 |
| RI05 | Menu 不能作为自己的父节点 |
| RI06 | 相同父节点下 Menu.name 唯一 |
| RI07 | BUTTON 类型的 Menu 不展示在侧边栏（无 component/icon/path） |

## 6. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | Role 类无 MyBatis/Spring 注解 | 代码审查 |
| AC02 | Menu 类无 MyBatis/Spring 注解 | 代码审查 |
| AC03 | RoleId, RoleName, RoleCode, RoleType, RoleStatus, DataScope 为不可变值对象 | 代码审查 |
| AC04 | MenuId, MenuName, MenuType, MenuPermission 为不可变值对象 | 代码审查 |
| AC05 | RoleRepository/MenuRepository 接口在领域层 | 代码审查 |
| AC06 | 编译通过 | mvn compile |
| AC07 | Controller 使用新 ApplicationService | 代码审查 |
| AC08 | 领域服务 PermissionChecker/UserRoleAssigner/RoleMenuAssigner 在领域层定义 | 代码审查 |

## 7. 分步执行计划

**阶段 1**: 创建值对象（Role: RoleId/RoleName/RoleCode/RoleType/RoleStatus/DataScope; Menu: MenuId/MenuName/MenuType/MenuPermission）
**阶段 2**: 创建领域事件 + 仓储接口
**阶段 3**: 创建 Role 聚合根 + Menu 聚合根 + 工厂
**阶段 4**: 创建领域服务接口（PermissionChecker/UserRoleAssigner/RoleMenuAssigner）
**阶段 5**: 实现基础设施层（RepositoryImpl + 领域服务Impl）
**阶段 6**: 创建应用层（PermissionApplicationService）
**阶段 7**: 适配 Controller
**阶段 8**: 编译验证

## 8. 回滚条件

1. 编译失败
2. Role/Menu 聚合注入基础设施依赖
3. 值对象存在 setter
4. Controller 权限校验行为回归
