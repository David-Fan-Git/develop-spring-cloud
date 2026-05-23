# DDD Skill: AggregateRoot_Bpm_Skill

## 1. 技能名称

`AggregateRoot_Bpm_Skill` — 工作流(BPM)模块聚合根的领域建模与重构技能

## 2. 适用场景

BPM 工作流管理模块，涵盖以下 **7 个已实施 DDD 的聚合** 和 **4 个 Flowable 集成型聚合（待 DDD 重构）**：

### 2.1 已实施 DDD 的聚合（7 个）

| 聚合 | 包路径 | 说明 |
|------|--------|------|
| **BpmCategory** | `domain/definition/` | 流程分类，含名称/编码唯一性、删除校验、批量排序 |
| **BpmForm** | `domain/form/` | 动态表单配置，含表单字段定义、vModel 重复校验 |
| **BpmUserGroup** | `domain/usergroup/` | 用户组，含成员管理、启用/禁用、分组校验 |
| **BpmProcessExpression** | `domain/expression/` | 流程表达式，Spring EL 表达式管理 |
| **BpmProcessListener** | `domain/listener/` | 流程监听器，含 CLASS 类型接口校验、EXPRESSION 格式校验 |
| **BpmOALeave** | `domain/leave/` | OA 请假申请，与 BPM 流程引擎集成，状态联动 |
| **BpmProcessInstanceCopy** | `domain/copy/` | 流程抄送记录，关联任务/流程实例/流程定义 |

### 2.2 待 DDD 重构的 Flowable 集成型聚合（4 个）

| 聚合 | 说明 | 关键外部依赖 |
|------|------|-------------|
| **BpmModel** | 流程模型，直接操作 Flowable `RepositoryService` | Flowable API |
| **BpmProcessDefinition** | 流程定义，管理 `Deployment` 生命周期 | Flowable API + `AdminUserApi` |
| **BpmProcessInstance** | 流程实例，运行时 + 历史实例管理 | Flowable API + `AdminUserApi` + `DeptApi` |
| **BpmTask** | 流程任务，审批/退回/加签/转办/委派/撤回 | Flowable API + `AdminUserApi` + `DeptApi` |

## 3. DDD 构造块

### 3.1 聚合根（7 个已实施）

#### BpmCategory — 流程分类聚合根

```
com.develop.mvp.pk.module.bpm.domain.definition.BpmCategory
```

**聚合边界**：
- BpmCategory（根实体）
- 不包含：BpmModel（仅通过 category code 引用）、BpmProcessDefinition（仅通过 category 字段引用）

**状态流转**：
```
ENABLED <--> DISABLED (通过 enable()/disable())
删除 → markDeleted() 发布 CategoryDeletedEvent
```

**值对象**：

| 值对象 | 类名 | 封装字段 | 不可变 | 自校验 |
|--------|------|---------|--------|--------|
| 分类ID | `CategoryId` | `Long value` | ✅ | 非空 |
| 分类名称 | `CategoryName` | `String value` | ✅ | 非空、非空白 |
| 分类编码 | `CategoryCode` | `String value` | ✅ | 非空、非空白 |
| 分类状态 | `CategoryStatus` | `Integer code` | ✅ | 只能是 ENABLE/DISABLE |

**仓储接口**：
```
com.develop.mvp.pk.module.bpm.domain.definition.repository.BpmCategoryRepository
```

```java
public interface BpmCategoryRepository {
    void save(BpmCategory c);
    void delete(CategoryId id);
    BpmCategory findById(CategoryId id);
    Optional<BpmCategory> findByCode(CategoryCode code);
    Optional<BpmCategory> findByName(CategoryName name);
    List<BpmCategory> findAll();
    List<BpmCategory> findByStatus(CategoryStatus status);
    PageResult<BpmCategory> findPage(String name, String code, Integer status, Integer pageNo, Integer pageSize);
    long getModelCountByCategory(String code);
}
```

**工厂**：`BpmCategoryFactory` — `create(id, name, code, sort)` / `reconstitute(id, name, code, sort, status)`

**领域事件**：
| 事件 | 触发时机 |
|------|---------|
| `CategoryDeletedEvent` | 调用 `markDeleted()` 时 |

**应用服务**：`BpmCategoryApplicationService`
- `create(name, code, status, sort)` — 创建前校验名称+编码唯一性
- `update(id, name, code, status, sort)` — 更新前校验存在+名称/编码唯一性
- `delete(id)` — 删除前校验未被模型使用
- `updateSortBatch(ids)` — 批量更新排序值
- `get(id)` / `getByStatus(status)` / `getAll()` / `getPage(name, code, status, pageNo, pageSize)` — 查询

---

#### BpmForm — 动态表单聚合根

```
com.develop.mvp.pk.module.bpm.domain.form.BpmForm
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 表单ID | `FormId` | `Long value` |
| 表单名称 | `FormName` | `String value` |
| 表单状态 | `FormStatus` | `Integer code` |

**仓储接口**：`BpmFormRepository`
```java
void save(BpmForm form);
void delete(FormId id);
BpmForm findById(FormId id);
List<BpmForm> findAll();
List<BpmForm> findByIds(Collection<FormId> ids);
PageResult<BpmForm> findPage(String name, Integer pageNo, Integer pageSize);
```

**工厂**：`BpmFormFactory`

**领域事件**：`FormDeletedEvent`

**应用服务**：`BpmFormApplicationService`

---

#### BpmUserGroup — 用户组聚合根

```
com.develop.mvp.pk.module.bpm.domain.usergroup.BpmUserGroup
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 用户组ID | `UserGroupId` | `Long value` |
| 用户组名称 | `UserGroupName` | `String value` |
| 用户组状态 | `UserGroupStatus` | `Integer code` |

**仓储接口**：`BpmUserGroupRepository`
```java
void save(BpmUserGroup group);
void delete(UserGroupId id);
BpmUserGroup findById(UserGroupId id);
List<BpmUserGroup> findByIds(Collection<UserGroupId> ids);
List<BpmUserGroup> findByStatus(UserGroupStatus status);
List<BpmUserGroup> findAll();
PageResult<BpmUserGroup> findPage(String name, Integer status, Integer pageNo, Integer pageSize);
```

**工厂**：`BpmUserGroupFactory`

**领域事件**：`UserGroupDeletedEvent`

**应用服务**：`BpmUserGroupApplicationService`
- `validateGroups(ids)` — 校验用户组存在且已启用（R08-R09），供任务分配策略使用

---

#### BpmProcessExpression — 流程表达式聚合根

```
com.develop.mvp.pk.module.bpm.domain.expression.BpmProcessExpression
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 表达式ID | `ExpressionId` | `Long value` |
| 表达式名称 | `ExpressionName` | `String value` |
| 表达式状态 | `ExpressionStatus` | `Integer code` |

**仓储接口**：`BpmProcessExpressionRepository`

**工厂**：`BpmProcessExpressionFactory`

**领域事件**：`ExpressionDeletedEvent`

**应用服务**：`BpmProcessExpressionApplicationService`

---

#### BpmProcessListener — 流程监听器聚合根

```
com.develop.mvp.pk.module.bpm.domain.listener.BpmProcessListener
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 监听器ID | `ListenerId` | `Long value` |
| 监听器名称 | `ListenerName` | `String value` |
| 监听器状态 | `ListenerStatus` | `Integer code` |

**仓储接口**：`BpmProcessListenerRepository`

**工厂**：`BpmProcessListenerFactory`

**领域事件**：`ListenerDeletedEvent`

**应用服务**：`BpmProcessListenerApplicationService`
- `validateListenerValue(type, valueType, value)` — 校验 CLASS 类型必须实现对应接口（R11），EXPRESSION 类型必须为 `${}` 格式（R12）

---

#### BpmOALeave — OA 请假单聚合根

```
com.develop.mvp.pk.module.bpm.domain.leave.BpmOALeave
```

**聚合边界**：
- BpmOALeave（根实体）
- 不包含：BpmProcessInstance（通过 processInstanceId 引用，BPM 流程引擎管理）

**状态流转**：
```
RUNNING --> APPROVE / REJECT / CANCEL (通过 updateStatus())
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 请假单ID | `LeaveId` | `Long value` |
| 请假单状态 | `LeaveStatus` | `Integer code` |

**仓储接口**：`BpmOALeaveRepository`
```java
void save(BpmOALeave leave);
BpmOALeave findById(LeaveId id);
PageResult<BpmOALeave> findPage(Long userId, Integer status, Integer type, String reason,
                                Integer pageNo, Integer pageSize);
```

**工厂**：`BpmOALeaveFactory`

**领域事件**：`LeaveStatusUpdatedEvent`

**应用服务**：`BpmOALeaveApplicationService`
- 使用固定 process key `"oa_leave"` 发起 BPM 流程

---

#### BpmProcessInstanceCopy — 流程抄送聚合根

```
com.develop.mvp.pk.module.bpm.domain.copy.BpmProcessInstanceCopy
```

**值对象**：

| 值对象 | 类名 | 封装字段 |
|--------|------|---------|
| 抄送ID | `CopyId` | `Long value` |
| 抄送用户ID | `CopyUserId` | `Long value` |

**仓储接口**：`BpmProcessInstanceCopyRepository`
```java
void save(BpmProcessInstanceCopy copy);
void saveBatch(List<BpmProcessInstanceCopy> copies);
void deleteByProcessInstanceId(String processInstanceId);
PageResult<BpmProcessInstanceCopy> findPage(Long userId, String processInstanceName,
                                             Integer pageNo, Integer pageSize);
```

**工厂**：`BpmProcessInstanceCopyFactory`

**领域事件**：`CopyCreatedEvent`

**应用服务**：`BpmProcessInstanceCopyApplicationService`
- 需要依赖 `BpmTaskService`、`BpmProcessInstanceService`、`BpmProcessDefinitionService`

---

### 3.2 聚合根（4 个未实施 DDD，仍为旧三层架构）

以下聚合根直接操作 Flowable API，暂未进行 DDD 重构：

#### BpmModel — 流程模型

**位置**：`service/definition/BpmModelService` / `BpmModelServiceImpl`
**核心依赖**：`RepositoryService`（Flowable）、`BpmProcessDefinitionService`、`BpmFormService`、`BpmTaskCandidateInvoker`
**核心操作**：CRUD、部署、激活/挂起、清理全部流程数据、BPMN/SimpleModel 转换

#### BpmProcessDefinition — 流程定义

**位置**：`service/definition/BpmProcessDefinitionService` / `BpmProcessDefinitionServiceImpl`
**核心依赖**：`RepositoryService`（Flowable）、`AdminUserApi`
**核心操作**：查询（活跃/挂起）、部署、状态变更、用户发起权限校验

#### BpmProcessInstance — 流程实例

**位置**：`service/task/BpmProcessInstanceService` / `BpmProcessInstanceServiceImpl`
**核心依赖**：`RuntimeService`、`HistoryService`（Flowable）、`AdminUserApi`、`DeptApi`、`BpmTaskCandidateInvoker`
**核心操作**：发起、取消（发起人/管理员）、审批详情、BPMN 模型视图、事件处理（完成/创建）

#### BpmTask — 流程任务

**位置**：`service/task/BpmTaskService` / `BpmTaskServiceImpl`
**核心依赖**：`TaskService`、`HistoryService`、`RuntimeService`、`ManagementService`（Flowable）、`AdminUserApi`、`DeptApi`
**核心操作**：审批通过/拒绝、退回、委派、转办、加签/减签、抄送、撤回、超时处理

---

### 3.3 值对象汇总

| 聚合 | 值对象 | 不可变 | 自校验 |
|------|--------|--------|--------|
| BpmCategory | `CategoryId`, `CategoryName`, `CategoryCode`, `CategoryStatus` | ✅ | ✅ |
| BpmForm | `FormId`, `FormName`, `FormStatus` | ✅ | ✅ |
| BpmUserGroup | `UserGroupId`, `UserGroupName`, `UserGroupStatus` | ✅ | ✅ |
| BpmProcessExpression | `ExpressionId`, `ExpressionName`, `ExpressionStatus` | ✅ | ✅ |
| BpmProcessListener | `ListenerId`, `ListenerName`, `ListenerStatus` | ✅ | ✅ |
| BpmOALeave | `LeaveId`, `LeaveStatus` | ✅ | ✅ |
| BpmProcessInstanceCopy | `CopyId`, `CopyUserId` | ✅ | ✅ |

所有值对象为 `final class`，字段为 `final`，构造方法自校验，`equals/hashCode` 基于 value 字段。

### 3.4 领域事件汇总

| 聚合 | 事件 | 触发时机 | 携带数据 |
|------|------|---------|---------|
| BpmCategory | `CategoryDeletedEvent` | 分类标记删除 | id |
| BpmForm | `FormDeletedEvent` | 表单标记删除 | id |
| BpmUserGroup | `UserGroupDeletedEvent` | 用户组标记删除 | id |
| BpmProcessExpression | `ExpressionDeletedEvent` | 表达式标记删除 | id |
| BpmProcessListener | `ListenerDeletedEvent` | 监听器标记删除 | id |
| BpmOALeave | `LeaveStatusUpdatedEvent` | 请假单状态更新 | id, newStatus |
| BpmProcessInstanceCopy | `CopyCreatedEvent` | 抄送创建 | id, userId, processInstanceId |

### 3.5 仓储实现（基础设施层）

所有 7 个已实施 DDD 的聚合均有 `RepositoryImpl` 位于 `infrastructure/` 下：

```
infrastructure/definition/BpmCategoryRepositoryImpl.java
infrastructure/form/BpmFormRepositoryImpl.java
infrastructure/usergroup/BpmUserGroupRepositoryImpl.java
infrastructure/expression/BpmProcessExpressionRepositoryImpl.java
infrastructure/listener/BpmProcessListenerRepositoryImpl.java
infrastructure/leave/BpmOALeaveRepositoryImpl.java
infrastructure/copy/BpmProcessInstanceCopyRepositoryImpl.java
```

负责：聚合根 ↔ MyBatis DO 的映射转换，委托给对应的 Mapper 实现持久化。

### 3.6 应用服务

所有 7 个已实施 DDD 的聚合均有 `ApplicationService` 位于 `application/` 下：

```
application/definition/BpmCategoryApplicationService.java
application/form/BpmFormApplicationService.java
application/usergroup/BpmUserGroupApplicationService.java
application/expression/BpmProcessExpressionApplicationService.java
application/listener/BpmProcessListenerApplicationService.java
application/leave/BpmOALeaveApplicationService.java
application/copy/BpmProcessInstanceCopyApplicationService.java
```

编排职责：校验→调用工厂→仓储持久化→发布领域事件

## 4. 职责边界

### 4.1 已实施 DDD 聚合的业务规则

#### BpmCategory 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R01 | 分类名称全局唯一 | `BpmCategoryServiceImpl.java` L62-69 — `validateCategoryNameUnique()` |
| R02 | 分类编码全局唯一 | `BpmCategoryServiceImpl.java` L71-78 — `validateCategoryCodeUnique()` |
| R03 | 分类删除前校验是否被流程模型引用 | `BpmCategoryServiceImpl.java` L85-88 — `deleteCategory()` 调用 `modelService.getModelCountByCategory()` |
| R04 | 批量排序时所有分类 ID 必须存在，排序值从 0 开始递增 | `BpmCategoryServiceImpl.java` L126-138 — `updateCategorySortBatch()` |

#### BpmForm 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R05 | 表单字段 vModel 值不可重复（当前为 Vue3 兼容暂时跳过校验） | `BpmFormServiceImpl.java` L96-112 — `validateFields()`，`if(true) return;` 兼容 Vue3 表单设计器 |
| R06 | 表单更新/删除前必须校验存在性 | `BpmFormServiceImpl.java` L48, L57 — `validateFormExists()` |

#### BpmUserGroup 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R07 | 用户组更新/删除前校验存在性 | `BpmUserGroupServiceImpl.java` L46, L55 — `validateUserGroupExists()` |
| R08 | 校验用户组集合：每个 ID 必须存在且已启用 | `BpmUserGroupServiceImpl.java` L88-105 — `validUserGroups()` |
| R09 | 用户组状态为 DISABLE 时不允许用于任务分配 | `BpmUserGroupServiceImpl.java` L101-103 — `USER_GROUP_IS_DISABLE` |

#### BpmProcessListener 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R10 | CLASS 类型的监听器值必须是可实例化的类名 | `BpmProcessListenerServiceImpl.java` L56 — `Class.forName(value)` |
| R11 | EXECUTION 类型的 CLASS 监听器必须实现 `JavaDelegate` 接口 | `BpmProcessListenerServiceImpl.java` L58-61 |
| R12 | TASK 类型的 CLASS 监听器必须实现 `TaskListener` 接口 | `BpmProcessListenerServiceImpl.java` L62-65 |
| R13 | EXPRESSION 类型的监听器值必须为 `${}` 包围的表达式 | `BpmProcessListenerServiceImpl.java` L73-75 |

#### BpmOALeave 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R14 | 请假天数 = endTime - startTime，按天计算 | `BpmOALeaveServiceImpl.java` L49 — `LocalDateTimeUtil.between()` |
| R15 | OA 请假使用固定流程 Key `"oa_leave"` | `BpmOALeaveServiceImpl.java` L37 — `PROCESS_KEY` 常量 |
| R16 | 创建请假单时自动发起 BPM 流程实例 | `BpmOALeaveServiceImpl.java` L54-61 — 通过 `processInstanceApi.createProcessInstance()` |
| R17 | 请假单状态由流程实例生命周期监听器更新 | `BpmOALeaveServiceImpl.java` L68-71 — `updateLeaveStatus()` + `BpmOALeaveStatusListener` |
| R18 | 状态更新前校验请假单存在 | `BpmOALeaveServiceImpl.java` L69 — `validateLeaveExists()` |

#### BpmProcessInstanceCopy 聚合根必须负责的规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R19 | 从任务发起抄送时校验任务存在 | `BpmProcessInstanceCopyServiceImpl.java` L51-54 |
| R20 | 抄送发起时校验流程实例存在 | `BpmProcessInstanceCopyServiceImpl.java` L64-67 |
| R21 | 抄送发起时校验流程定义存在 | `BpmProcessInstanceCopyServiceImpl.java` L68-73 |
| R22 | 抄送记录携带完整的流程上下文：实例名、定义ID、分类、活动ID、活动名、任务ID | `BpmProcessInstanceCopyServiceImpl.java` L76-81 |
| R23 | 多个抄送对象批量插入 | `BpmProcessInstanceCopyServiceImpl.java` L82 — `insertBatch()` |

### 4.2 待 DDD 重构聚合的业务规则

#### BpmModel 相关规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R24 | 流程标识（key）必须为 XML NCName 格式 | `BpmModelServiceImpl.java` L102-104 — `ValidationUtils.isXmlNCName()` |
| R25 | 流程标识（key）全局唯一 | `BpmModelServiceImpl.java` L106-109 — `getModelByKey()` |
| R26 | 流程模型更新/部署/删除需要管理员权限 | `BpmModelServiceImpl.java` L204-211 — `validateModelManager()` |
| R27 | 部署前校验 BPMN 图：必须有 StartEvent，UserTask 必须有 name，首节点不能为"审批人自选" | `BpmModelServiceImpl.java` L242-268 — `validateBpmnXml()` |
| R28 | 部署前校验表单已配置 | `BpmModelServiceImpl.java` L357-378 — `validateFormConfig()` |
| R29 | 部署前校验任务分配规则已配置 | `BpmModelServiceImpl.java` L225 — `taskCandidateInvoker.validateBpmnConfig()` |
| R30 | 模型删除后自动挂起关联的流程定义 | `BpmModelServiceImpl.java` L279 — `updateProcessDefinitionSuspended()` |
| R31 | 清理模型时删除所有运行中和历史流程实例及抄送 | `BpmModelServiceImpl.java` L283-308 — `cleanModel()` |
| R32 | Simple Model JSON ↔ BpmnModel 双向转换 | `BpmModelServiceImpl.java` L149-158 — `SimpleModelUtils.buildBpmnModel()` |

#### BpmProcessDefinition 相关规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R33 | 部署时 ProcessDefinition 的 key 必须与 Model 的 key 一致 | `BpmProcessDefinitionServiceImpl.java` L152-154 |
| R34 | 部署时 ProcessDefinition 的 name 必须与 Model 的 name 一致 | `BpmProcessDefinitionServiceImpl.java` L155-157 |
| R35 | 用户发起权限校验：在 startUserIds 中，或在 startDeptIds 所在部门中，或两者皆空（所有人可发起） | `BpmProcessDefinitionServiceImpl.java` L92-112 — `canUserStartProcessDefinition()` |
| R36 | 流程定义状态切换：激活/挂起 | `BpmProcessDefinitionServiceImpl.java` L171-194 |
| R37 | 多次部署后只有最新部署的流程定义为活跃状态 | `BpmModelServiceImpl.java` L234 — `updateProcessDefinitionSuspended()` |

#### BpmProcessInstance 相关规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R38 | 发起流程时校验流程定义存在且未挂起 | `BpmProcessInstanceServiceImpl.java` L784-789 |
| R39 | 发起流程时校验用户有发起权限 | `BpmProcessInstanceServiceImpl.java` L796-798 |
| R40 | 发起流程时校验发起人自选审批人已配置且用户存在 | `BpmProcessInstanceServiceImpl.java` L834-862 — `validateStartUserSelectAssignees()` |
| R41 | 发起流程时过滤系统级变量，防止被用户占用 | `BpmProcessInstanceServiceImpl.java` L806 — `filterProcessInstanceFormVariable()` |
| R42 | 流程名称可根据模板自动生成（含发起人昵称、时间、定义名） | `BpmProcessInstanceServiceImpl.java` L864-881 — `generateProcessInstanceName()` |
| R43 | 发起人取消流程：只能取消自己的 | `BpmProcessInstanceServiceImpl.java` L886-894 |
| R44 | 发起人取消流程：校验 `allowCancelRunningProcess` 配置 | `BpmProcessInstanceServiceImpl.java` L896-902 |
| R45 | 子流程不允许独立取消 | `BpmProcessInstanceServiceImpl.java` L904-906 |
| R46 | 取消流程时级联取消所有子流程 | `BpmProcessInstanceServiceImpl.java` L928-942 — `updateProcessInstanceCancel()` |
| R47 | 流程完成时若状态仍为 `RUNNING` 则自动变为 `APPROVE` | `BpmProcessInstanceServiceImpl.java` L975-979 |
| R48 | 子流程拒绝时级联拒绝父流程并结束 | `BpmProcessInstanceServiceImpl.java` L983-1001 |
| R49 | 流程审批通过/拒绝后发送短信通知发起人 | `BpmProcessInstanceServiceImpl.java` L1005-1011 |
| R50 | 流程完成时发送状态事件 | `BpmProcessInstanceServiceImpl.java` L1014-1015 |
| R51 | 流程审批通过后触发后置 HTTP 通知 | `BpmProcessInstanceServiceImpl.java` L1018-1028 |
| R52 | 流程创建后触发前置 HTTP 通知 | `BpmProcessInstanceServiceImpl.java` L1052-1063 |

#### BpmTask 相关规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R53 | 审批任务前校验任务存在且分配给当前用户 | `BpmTaskServiceImpl.java` L556 — `validateTask()` |
| R54 | 需要签名时校验签名图片必传 | `BpmTaskServiceImpl.java` L563-567 |
| R55 | 需要审批意见时校验意见必传 | `BpmTaskServiceImpl.java` L569-572 |
| R56 | 被委派的任务（DelegationState.PENDING）调用 resolveTask 而非 complete | `BpmTaskServiceImpl.java` L574-578 |
| R57 | 有后加签的任务审批时进入 APPROVING 中间状态，激活子任务 | `BpmTaskServiceImpl.java` L581-583, L710-721 |
| R58 | 审批通过时合并历史流程变量和前端变量（前端覆盖） | `BpmTaskServiceImpl.java` L600-606 |
| R59 | 审批通过时校验并设置下一个节点的审批人（发起人自选/审批人自选策略） | `BpmTaskServiceImpl.java` L609, L641-699 |
| R60 | 审批拒绝时更新任务状态+根父任务状态（加签场景） | `BpmTaskServiceImpl.java` L808-822 |
| R61 | 拒绝处理策略：驳回到指定节点 或 标记不通过并结束流程 | `BpmTaskServiceImpl.java` L824-839 |
| R62 | 退回时校验目标节点可串行到达 | `BpmTaskServiceImpl.java` L891-905 — `validateTargetTaskCanReturn()` |
| R63 | 退回时使用 Flowable `changeActivityStateBuilder` 执行 | `BpmTaskServiceImpl.java` L953-961 |
| R64 | 委派时设置 owner 为原审批人，assignee 为被委派人 | `BpmTaskServiceImpl.java` L1014-1018 |
| R65 | 转办时直接设置 assignee 为新审批人 | `BpmTaskServiceImpl.java` L1050 |
| R66 | 加签时校验前后加签不能同时存在 | `BpmTaskServiceImpl.java` L1142-1161 — `validateTaskCanCreateSign()` |
| R67 | 加签时校验被加签人与现有审批人不重复 | `BpmTaskServiceImpl.java` L1151-1159 |
| R68 | 减签时级联删除所有子任务 | `BpmTaskServiceImpl.java` L1213-1242 — `deleteSignTask()` |
| R69 | 撤回时校验流程允许撤回配置 | `BpmTaskServiceImpl.java` L1267-1269 — `allowWithdrawTask` |
| R70 | 撤回时校验下一个节点未被审批过 | `BpmTaskServiceImpl.java` L1270-1289 |
| R71 | 审批人为空时根据配置自动通过/自动拒绝 | `BpmTaskServiceImpl.java` L1383-1394 |
| R72 | 自动去重策略：APPROVE_ALL（同人任意节点）或 APPROVE_SEQUENT（同人相邻节点） | `BpmTaskServiceImpl.java` L1468-1497 |
| R73 | 发起人节点自动通过审批 | `BpmTaskServiceImpl.java` L1514-1521 |
| R74 | 审批人与发起人相同时策略：自动跳过 或 转交部门负责人，退回时跳过此策略 | `BpmTaskServiceImpl.java` L1523-1558 |
| R75 | 任务超时处理策略：自动提醒/自动同意/自动拒绝 | `BpmTaskServiceImpl.java` L1594-1629 |
| R76 | 任务分配时发送待办通知 | `BpmTaskServiceImpl.java` L1561-1564 |

#### BpmMessage 相关规则

| 编号 | 规则描述 | 对应原代码位置 |
|------|---------|-------------|
| R77 | 流程审批通过 → 短信通知发起人 | `BpmMessageServiceImpl.java` L36-42 |
| R78 | 流程审批拒绝 → 短信通知发起人（含原因） | `BpmMessageServiceImpl.java` L45-52 |
| R79 | 任务分配 → 短信通知审批人 | `BpmMessageServiceImpl.java` L55-63 |
| R80 | 任务超时 → 短信提醒 | `BpmMessageServiceImpl.java` L66-73 |

### 4.3 严禁外泄的职责

| 禁止行为 | 原因 | 应由谁处理 |
|---------|------|----------|
| 直接操作 Flowable `RepositoryService`/`RuntimeService`/`TaskService`/`HistoryService` | 破坏持久化无关性 | Repository 实现或 Flowable 适配器 |
| 在聚合根内调用 System API（`AdminUserApi`/`DeptApi`） | 跨模块依赖 | ApplicationService 编排 |
| 直接操作 MyBatis Mapper | 破坏持久化无关性 | RepositoryImpl |
| 直接处理 DTO/VO 转换 | 表示层关注点 | Convert 层 / Controller |
| 在聚合根内使用 Spring 注解 | 领域层应纯 Java | N/A |
| 在聚合根外直接修改状态字段 | 破坏封装性 | 聚合根方法 |
| 直接操作 `processInstanceVariables`（流程变量） | BpmProcessInstance 聚合的职责 | BpmProcessInstance 聚合 或 ApplicationService |

## 5. 依赖与协作

### 5.1 领域层依赖（7 个已实施 DDD 的聚合）

每个 DDD 聚合根仅依赖：
- 自身值对象
- 自身仓储接口
- `java.util` 标准库
- 框架公共类（`PageResult` — 无基础设施依赖）

### 5.2 跨聚合协作

| 源聚合 | 目标聚合 | 协作方式 | 场景 |
|--------|---------|---------|------|
| BpmCategoryService | BpmModelService | 通过 category code 计数 | 分类删除时校验被模型使用（R03） |
| BpmOALeave | BpmProcessInstance | 通过 `processInstanceApi` 创建流程实例 | 创建请假单时发起 BPM 流程（R16） |
| BpmOALeave | BpmProcessInstanceStatusEvent | 通过事件监听更新状态 | 流程完成时更新请假单状态（R17） |
| BpmProcessInstanceCopy | BpmTask / BpmProcessInstance | 通过 Service 校验存在性 | 抄送创建时校验任务和流程实例（R19-R21） |
| BpmUserGroup | BpmTaskCandidateInvoker | 通过 ID 引用校验 | 任务分配策略中的用户组校验（R08-R09） |
| BpmProcessExpression | BpmTaskCandidateInvoker | 通过 ID 引用 | 流程条件表达式的使用 |
| BpmForm | BpmModel | 通过 `formId` 引用 | 模型部署时校验表单配置（R28） |

### 5.3 基础设施依赖（通过接口倒置）

```
领域层定义接口                             基础设施层实现
─────────────                             ──────────────
BpmCategoryRepository              ←──    BpmCategoryRepositoryImpl (委托 BpmCategoryMapper)
BpmFormRepository                  ←──    BpmFormRepositoryImpl (委托 BpmFormMapper)
BpmUserGroupRepository             ←──    BpmUserGroupRepositoryImpl (委托 BpmUserGroupMapper)
BpmProcessExpressionRepository     ←──    BpmProcessExpressionRepositoryImpl (委托 BpmProcessExpressionMapper)
BpmProcessListenerRepository       ←──    BpmProcessListenerRepositoryImpl (委托 BpmProcessListenerMapper)
BpmOALeaveRepository               ←──    BpmOALeaveRepositoryImpl (委托 BpmOALeaveMapper)
BpmProcessInstanceCopyRepository   ←──    BpmProcessInstanceCopyRepositoryImpl (委托 BpmProcessInstanceCopyMapper)
```

### 5.4 外部系统依赖（仅 ApplicationService 层）

| 外部 API | 使用场景 |
|---------|---------|
| `AdminUserApi` | 校验用户存在、获取用户部门、发起人信息 |
| `DeptApi` | 获取部门负责人 |
| `SmsSendApi` | 发送流程通知短信 |
| `BpmProcessInstanceApi` | OA 请假发起 BPM 流程 |

## 6. 不变式与约束（Invariants）

| 编号 | 不变式 | 类型 | 验证点 |
|------|--------|------|--------|
| I01 | BpmCategory 的 `name` 和 `code` 各自全局唯一 | 跨聚合唯一性 | create/update 时 |
| I02 | BpmCategory 被模型引用时不可删除 | 跨聚合引用完整性 | delete 时 |
| I03 | BpmForm 字段 (`fields`) 的 `vModel` 值不可重复 | 聚合内部 | create/update 时（目前兼容跳过了 Vue3 校验） |
| I04 | BpmUserGroup 用于任务分配时必须为 ENABLED 状态 | 跨聚合状态约束 | `validateGroups()` 时 |
| I05 | BpmProcessListener (CLASS) 的 `value` 类必须实现 `JavaDelegate` 或 `TaskListener` | 聚合内部 | create/update 时 |
| I06 | BpmProcessListener (EXPRESSION) 的 value 必须为 `${}` 格式 | 聚合内部 | create/update 时 |
| I07 | BpmOALeave 的 `day` 必须为 `endTime - startTime` 计算所得 | 聚合内部 | create 时 |
| I08 | BpmOALeave 的状态仅由 `updateStatus()` 方法变更 | 聚合内部 | 全生命周期 |
| I09 | BpmProcessInstanceCopy 创建时必须关联有效的 task + processInstance + processDefinition | 跨聚合引用完整性 | create 时 |
| I10 | 流程模型 key 一旦创建不可变更 | 跨聚合约束 | create/update 时 |
| I11 | 部署时 ProcessDefinition 的 key/name 必须与 Model 一致 | 跨聚合一致性 | deploy 时 |
| I12 | 发起人取消流程时只能取消自己的 | 安全约束 | cancel 时 |
| I13 | 子流程不能独立取消，必须由父流程级联 | 聚合关系约束 | cancel 时 |
| I14 | 审批任务时当前用户必须是 assignee | 安全约束 | approve/reject 前 |
| I15 | 退回目标节点必须串行可达 | 流程拓扑约束 | return 时 |
| I16 | 加签类型（前/后）在同一条任务链上不可混用 | 流程语义约束 | createSign 时 |
| I17 | 多人场景下或签票通过后其余任务自动取消 | 流程语义约束 | 审批通过时 |
| I18 | 撤回时下一个节点必须尚未被审批 | 流程语义约束 | withdraw 时 |
| I19 | 发起人自选审批人必须在流程发起时就全部指定 | 流程语义约束 | createProcessInstance 时 |
| I20 | 多个表单权限场景下变量合并以前端为准 | 数据一致性约束 | approveTask 时 |

## 7. 验收标准

| 编号 | 验收标准 | 验证方法 |
|------|---------|---------|
| AC01 | 7 个已实施 DDD 的聚合根均无 MyBatis/Spring/Flowable 注解 | 代码审查 |
| AC02 | 所有值对象为 final class，字段为 final，无 setter，构造方法自校验 | 代码审查 |
| AC03 | 所有 7 个仓储接口定义在 domain 层，不 import infrastructure 类 | 代码审查 |
| AC04 | 所有 7 个 RepositoryImpl 在 infrastructure 层，负责 DO↔Domain 映射 | 代码审查 |
| AC05 | 所有 7 个 ApplicationService 使用 Repository 接口 + Factory，不直接操作 Mapper/Flowable | 代码审查 |
| AC06 | BpmCategoryApplicationService.create() 校验 name/code 唯一性 | 代码审查 + 集成测试 |
| AC07 | BpmCategoryApplicationService.delete() 校验不被模型引用 | 代码审查 + 集成测试 |
| AC08 | BpmUserGroupApplicationService.validateGroups() 校验存在 + 启用 | 代码审查 |
| AC09 | BpmProcessListenerApplicationService.validateListenerValue() 校验 CLASS 实现接口 | 代码审查 + 单元测试 |
| AC10 | BpmOALeave 创建时自动发起 BPM 流程实例 | 集成测试 |
| AC11 | 领域事件在 ApplicationService 中被正确发布 | 集成测试 |
| AC12 | Controller 注入 ApplicationService（7 个已 DDD 聚合）而非旧 Service | 代码审查 |
| AC13 | 编译通过 | `mvn compile -pl develop-module-bpm/develop-module-bpm-server -am` |
| AC14 | 旧 BpmProcessInstanceCopyService/BpmUserGroupService 等对 DDD 聚合的调用应改为 ApplicationService | 代码审查 |
| AC15 | BpmOALeaveStatusListener 使用 ApplicationService 替代旧 Service | 代码审查 |
| AC16 | 4 个 Flowable 聚合（Model/ProcessDefinition/ProcessInstance/Task）的 DDD 重构计划已就绪 | 计划审查 |

## 8. 目录结构规划

### 8.1 当前目录结构（已实施 7 个 DDD 聚合）

```
develop-module-bpm/develop-module-bpm-server/src/main/java/com/develop/mvp/pk/module/bpm/
├── domain/
│   ├── definition/                    # BpmCategory 聚合
│   │   ├── BpmCategory.java
│   │   ├── BpmCategoryFactory.java
│   │   ├── valueobject/
│   │   │   ├── CategoryId.java
│   │   │   ├── CategoryName.java
│   │   │   ├── CategoryCode.java
│   │   │   └── CategoryStatus.java
│   │   ├── event/
│   │   │   ├── CategoryDomainEvent.java
│   │   │   └── CategoryDeletedEvent.java
│   │   └── repository/
│   │       └── BpmCategoryRepository.java
│   ├── form/                          # BpmForm 聚合
│   │   ├── BpmForm.java
│   │   ├── BpmFormFactory.java
│   │   ├── valueobject/ (FormId, FormName, FormStatus)
│   │   ├── event/ (FormDomainEvent, FormDeletedEvent)
│   │   └── repository/ (BpmFormRepository)
│   ├── usergroup/                     # BpmUserGroup 聚合
│   │   ├── BpmUserGroup.java
│   │   ├── BpmUserGroupFactory.java
│   │   ├── valueobject/ (UserGroupId, UserGroupName, UserGroupStatus)
│   │   ├── event/ (UserGroupDomainEvent, UserGroupDeletedEvent)
│   │   └── repository/ (BpmUserGroupRepository)
│   ├── expression/                    # BpmProcessExpression 聚合
│   │   ├── BpmProcessExpression.java
│   │   ├── BpmProcessExpressionFactory.java
│   │   ├── valueobject/ (ExpressionId, ExpressionName, ExpressionStatus)
│   │   ├── event/ (ExpressionDomainEvent, ExpressionDeletedEvent)
│   │   └── repository/ (BpmProcessExpressionRepository)
│   ├── listener/                      # BpmProcessListener 聚合
│   │   ├── BpmProcessListener.java
│   │   ├── BpmProcessListenerFactory.java
│   │   ├── valueobject/ (ListenerId, ListenerName, ListenerStatus)
│   │   ├── event/ (ListenerDomainEvent, ListenerDeletedEvent)
│   │   └── repository/ (BpmProcessListenerRepository)
│   ├── leave/                         # BpmOALeave 聚合
│   │   ├── BpmOALeave.java
│   │   ├── BpmOALeaveFactory.java
│   │   ├── valueobject/ (LeaveId, LeaveStatus)
│   │   ├── event/ (LeaveDomainEvent, LeaveStatusUpdatedEvent)
│   │   └── repository/ (BpmOALeaveRepository)
│   └── copy/                          # BpmProcessInstanceCopy 聚合
│       ├── BpmProcessInstanceCopy.java
│       ├── BpmProcessInstanceCopyFactory.java
│       ├── valueobject/ (CopyId, CopyUserId)
│       ├── event/ (CopyDomainEvent, CopyCreatedEvent)
│       └── repository/ (BpmProcessInstanceCopyRepository)
├── application/
│   ├── definition/BpmCategoryApplicationService.java
│   ├── form/BpmFormApplicationService.java
│   ├── usergroup/BpmUserGroupApplicationService.java
│   ├── expression/BpmProcessExpressionApplicationService.java
│   ├── listener/BpmProcessListenerApplicationService.java
│   ├── leave/BpmOALeaveApplicationService.java
│   └── copy/BpmProcessInstanceCopyApplicationService.java
├── infrastructure/
│   ├── definition/BpmCategoryRepositoryImpl.java
│   ├── form/BpmFormRepositoryImpl.java
│   ├── usergroup/BpmUserGroupRepositoryImpl.java
│   ├── expression/BpmProcessExpressionRepositoryImpl.java
│   ├── listener/BpmProcessListenerRepositoryImpl.java
│   ├── leave/BpmOALeaveRepositoryImpl.java
│   └── copy/BpmProcessInstanceCopyRepositoryImpl.java
├── service/                           # ← 旧三层架构（仍被 Controller 使用 + 4 个 Flowable 聚合）
│   ├── definition/
│   │   ├── BpmCategoryService[Impl].java       # 旧 Service，应被 ApplicationService 替代
│   │   ├── BpmFormService[Impl].java            # 旧 Service，应被 ApplicationService 替代
│   │   ├── BpmUserGroupService[Impl].java       # 旧 Service，应被 ApplicationService 替代
│   │   ├── BpmProcessExpressionService[Impl].java
│   │   ├── BpmProcessListenerService[Impl].java
│   │   ├── BpmModelService[Impl].java            # Flowable 聚合，待 DDD
│   │   └── BpmProcessDefinitionService[Impl].java # Flowable 聚合，待 DDD
│   ├── task/
│   │   ├── BpmProcessInstanceService[Impl].java  # Flowable 聚合，待 DDD
│   │   ├── BpmProcessInstanceCopyService[Impl].java
│   │   ├── BpmTaskService[Impl].java             # Flowable 聚合，待 DDD
│   │   ├── listener/ (BpmUserTaskListener, BpmCallActivityListener)
│   │   └── trigger/ (BpmTrigger, BpmFormDeleteTrigger, BpmFormUpdateTrigger, BpmHttpCallbackTrigger, BpmSyncHttpRequestTrigger)
│   ├── oa/
│   │   ├── BpmOALeaveService[Impl].java
│   │   └── listener/BpmOALeaveStatusListener.java
│   └── message/
│       ├── BpmMessageService[Impl].java
│       └── dto/ (4 个消息 DTO)
└── controller/                        # ← Controller 层，应注入 ApplicationService
    ├── admin/definition/vo/...
    ├── admin/task/vo/...
    ├── admin/oa/vo/...
    └── ...
```

### 8.2 未来 DDD 重构规划目录（4 个 Flowable 聚合）

```
domain/
├── model/             # BpmModel 聚合（待创建）
│   ├── BpmModel.java
│   ├── valueobject/ (ModelId, ModelKey, ModelMetaInfo...)
│   ├── event/ (ModelDeployedEvent, ModelDeletedEvent...)
│   └── repository/ (BpmModelRepository)
├── processdefinition/ # BpmProcessDefinition 聚合（待创建）
│   ├── BpmProcessDefinition.java
│   ├── ...
├── processinstance/   # BpmProcessInstance 聚合（待创建）
│   ├── BpmProcessInstance.java
│   └── ...
└── task/              # BpmTask 聚合（待创建）
    ├── BpmTask.java
    ├── valueobject/ (TaskAssignee, TaskStatus, TaskSignType...)
    ├── event/ (TaskApprovedEvent, TaskRejectedEvent, TaskReturnedEvent...)
    ├── service/ (TaskAssignmentService, TaskTimeoutService...)
    └── repository/ (BpmTaskRepository)
application/
├── model/BpmModelApplicationService.java
├── processdefinition/BpmProcessDefinitionApplicationService.java
├── processinstance/BpmProcessInstanceApplicationService.java
└── task/BpmTaskApplicationService.java
infrastructure/
├── model/BpmModelRepositoryImpl.java        # 封装 Flowable RepositoryService
├── processdefinition/BpmProcessDefinitionRepositoryImpl.java
├── processinstance/BpmProcessInstanceRepositoryImpl.java  # 封装 Flowable RuntimeService/HistoryService
└── task/BpmTaskRepositoryImpl.java          # 封装 Flowable TaskService
framework/flowable/   # Flowable 适配层（保持现有不动）
├── core/candidate/    # 候选人计算
├── core/enums/        # Flowable 常量枚举
├── core/util/         # BpmnModelUtils, FlowableUtils, SimpleModelUtils
└── core/event/        # 事件发布
```

## 9. 回滚条件

1. 编译失败（`mvn compile -pl develop-module-bpm/develop-module-bpm-server -am`）
2. 已 DDD 的聚合根内部注入基础设施依赖（MyBatis Mapper、Flowable API、Spring Bean）
3. 值对象存在 setter 或非 final 字段
4. 仓储接口 import 基础设施类
5. ApplicationService 绕过 Factory 直接 new 聚合根
6. 删除旧 Service 时 Controller 编译报错
7. 领域事件未被正确发布或消费
8. 唯一性校验（名称/编码）被移除
9. 状态校验（启用/禁用）被绕过
10. Flowable 集成型聚合的 DDD 重构导致流程引擎行为改变

## 10. 分步执行计划

### 第一阶段：完善已 DDD 聚合的剩余工作

**目标**：确保 7 个已实施 DDD 的聚合完全替代旧 Service，Controller 切到 ApplicationService。

**步骤 1.1** — 审查 Controller 使用情况：
- 检查 `controller/` 下所有 BPM Controller 注入的 Service，将旧 Service 引用改为 ApplicationService
- 涉及分类(BpmCategory)、表单(BpmForm)、用户组(BpmUserGroup)、表达式(BpmProcessExpression)、监听器(BpmProcessListener)、请假(BpmOALeave)、抄送(BpmProcessInstanceCopy)

**步骤 1.2** — 更新旧 Service 调用者：
- 检查 `BpmOALeaveStatusListener` 是否使用 ApplicationService 替代旧 `BpmOALeaveService`
- 检查 `BpmProcessInstanceCopyService` 等旧 Service 中调用了其他旧 Service 的地方
- 检查触发器（`BpmTrigger` 实现类）是否使用了旧 Service

**步骤 1.3** — 编译验证：
- `mvn compile -pl develop-module-bpm/develop-module-bpm-server -am`

### 第二阶段：补充缺失的校验与测试

**步骤 2.1** — 审查 BpmForm 的 `validateFields` 方法：
- 确定 Vue3 兼容期是否结束，是否需要恢复重复 vModel 校验

**步骤 2.2** — 补充单元测试：
- BpmCategory 名称/编码唯一性校验
- BpmProcessListener CLASS 类型接口实现校验
- BpmOALeave 天数计算逻辑
- 所有值对象的非法入参拒绝

**步骤 2.3** — 补充集成测试：
- BpmCategory 删除时被引用的拒绝场景
- BpmOALeave 创建 + BPM 流程发起集成
- BpmProcessInstanceCopy 创建时依赖校验

### 第三阶段：重构 4 个 Flowable 聚合（长线）

**步骤 3.1** — BpmProcessTask（审批任务）聚合 DDD 重构：
- 创建 `domain/task/` 聚合包，封装任务审批、退回、转办、加签等行为
- `BpmTaskRepository` 封装 Flowable `TaskService` 的查询和写入
- 将所有 `BpmTaskServiceImpl` 中的业务规则迁移到聚合根方法

**步骤 3.2** — BpmProcessInstance（流程实例）聚合 DDD 重构：
- 创建 `domain/processinstance/` 聚合包
- 将状态变更、取消、子流程级联等行为封装到聚合根
- `BpmProcessInstanceRepository` 封装 Flowable `RuntimeService`/`HistoryService`

**步骤 3.3** — BpmModel 和 BpmProcessDefinition 聚合 DDD 重构：
- 将 BPMN 校验、SimpleModel 转换、部署等逻辑迁移到领域层
- `BpmModelRepository` 封装 Flowable `RepositoryService`

**步骤 3.4** — BpmMessage（消息通知）聚合 DDD 重构：
- 消息发送逻辑可抽象为领域事件消费者
- ApplicationService 发布事件 → 消息监听器发送短信

### 第四阶段：旧 Service 清理

**步骤 4.1** — 逐个删除已替代的旧 Service：
- 确认 Controller 和所有调用者已迁移
- 删除旧 Service 接口和实现类（BpmCategoryService、BpmFormService、BpmUserGroupService、BpmProcessExpressionService、BpmProcessListenerService、BpmOALeaveService、BpmProcessInstanceCopyService）

**步骤 4.2** — 清理 `service/` 下相关包结构，保持只有 Flowable 集成型聚合的服务
