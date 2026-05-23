# DDD Skill: AggregateRoot_Module_Skill

## 1. 技能名称
`AggregateRoot_Module_Skill` — 模块聚合根的领域建模与重构技能

## 2. 适用场景
业务模块管理

## 3. DDD 构造块

### 3.1 聚合根
- 每个业务实体对应一个聚合根，封装业务规则和生命周期

### 3.2 值对象
- 不可变类(final class, final字段, 无setter)，构造方法自校验

### 3.3 仓储接口
- 每个聚合根对应一个Repository接口，定义在domain层，无infrastructure imports

### 3.4 领域服务
- 跨聚合操作通过领域服务接口定义

## 4. 职责边界
- **聚合负责**: 业务规则、状态流转、数据校验
- **严禁外泄**: 直接操作Mapper、外部API调用

## 5. 验收标准
- AC01: 聚合根无MyBatis/Spring注解
- AC02: 值对象不可变(final class, final字段, 无setter)
- AC03: 仓储接口在领域层
- AC04: 仓储实现在infrastructure层
- AC05: 编译通过
- AC06: Controller使用ApplicationService
