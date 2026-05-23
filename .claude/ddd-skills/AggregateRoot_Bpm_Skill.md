# DDD Skill: AggregateRoot_Bpm_Skill

## 1. 技能名称
`AggregateRoot_Bpm_Skill` — 工作流(BPM)聚合根的领域建模与重构技能

## 2. 适用场景
工作流管理：流程定义(Definition)、流程模型(Model)、流程实例(Task)、OA审批。

## 3. DDD 构造块

### 3.1 聚合根
- `BpmDefinition` — 流程定义聚合根
- `BpmTask` — 流程任务聚合根
- `BpmOa` — OA审批聚合根

### 3.2 值对象
- `ProcessKey`, `TaskStatus`, `ApprovalResult`, `FormConfig`
- 不可变，自校验

### 3.3 仓储接口
- `BpmDefinitionRepository`, `BpmTaskRepository`, `BpmOaRepository`

### 3.4 领域服务
- `BpmEngine` — 流程引擎接口（封装Flowable，接口在领域层，实现在infrastructure层）
- `TaskAssigner` — 任务分配

## 4. 职责边界
- **聚合负责**: 流程状态、审批逻辑、任务规则
- **严禁外泄**: 直接操作Flowable API、直接操作Mapper

## 5. 验收标准
- AC01: 聚合根无MyBatis/Spring/Flowable注解
- AC02: 值对象不可变
- AC03: 仓储接口在领域层
- AC04: BpmEngine接口在领域层，实现在infrastructure层
- AC05: 编译通过
