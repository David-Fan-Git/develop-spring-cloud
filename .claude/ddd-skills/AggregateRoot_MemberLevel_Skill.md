# DDD Skill: AggregateRoot_MemberLevel_Skill

## 1. 技能名称
`AggregateRoot_MemberLevel_Skill` — 会员等级(MemberLevel)聚合根的领域建模与重构技能

## 2. 适用场景
会员等级管理：等级创建/更新/删除、等级查询、等级校验、经验记录、等级变更记录。

## 3. DDD 构造块

### 3.1 聚合根
- `MemberLevel` — 会员等级聚合根，封装等级名称、级别、折扣、经验范围
- `MemberLevelRecord` — 等级变更记录（用户每次等级变更时记录）

### 3.2 值对象
- `LevelName` — 等级名称，非空
- `LevelValue` — 等级数值(0-100)
- `DiscountPercent` — 折扣百分比
- `ExperienceRange` — 经验值范围(min, max)

### 3.3 仓储接口
- `MemberLevelRepository` — MemberLevel 持久化接口
- `MemberLevelRecordRepository` — MemberLevelRecord 持久化接口

### 3.4 领域服务
- `MemberLevelUpgrader` — 等级升级逻辑，根据经验值判断升级到哪个等级

## 4. 职责边界
- **聚合负责**: 等级名称唯一性、等级数值范围、折扣范围、经验范围不重叠
- **严禁外泄**: 直接操作Mapper、用户经验值更新

## 5. 验收标准
- AC01: MemberLevel 无MyBatis/Spring注解
- AC02: 值对象不可变
- AC03: 仓储接口在领域层
- AC04: 仓储实现在infrastructure层
- AC05: 编译通过
