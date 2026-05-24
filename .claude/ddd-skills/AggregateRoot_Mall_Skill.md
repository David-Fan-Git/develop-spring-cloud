# DDD Skill: AggregateRoot_Mall_Skill

## Status

这是 Mall 模块 DDD skill 的父级导航入口，不是可直接执行的生产级聚合重构指南。

Mall 已拆成多个独立业务子域。执行 DDD 重构时必须进入对应子域 skill，不能用本文件覆盖 Product、Promotion、Trade、Statistics 的具体边界。

## Child Skills

| 子域 | 使用文件 | 适用范围 |
|---|---|---|
| Product | `AggregateRoot_MallProduct_Skill.md` | 商品分类、品牌、SPU、SKU、属性、评论、收藏、浏览记录 |
| Promotion | `AggregateRoot_MallPromotion_Skill.md` | 优惠券、秒杀、满减、奖励、拼团、砍价、积分活动 |
| Trade | `AggregateRoot_MallTrade_Skill.md` | 订单、购物车、售后、配送、交易状态流转 |
| Statistics | `AggregateRoot_MallStatistics_Skill.md` | 交易、商品、用户、营销统计口径 |

## How To Use

1. 先阅读 `DDD_Skill_Production_Readiness_Standard.md`。
2. 按本轮修改目标选择唯一子域 skill。
3. 读取对应子域当前代码事实源后再决定是否升级该子域 skill。
4. 每次只处理一个子域或一个小聚合集合，不把 Mall 当作一个整体批量重构。

## Red Flags

- 试图用本文件直接设计订单、商品或营销聚合。
- 一次同时改 Product、Promotion、Trade、Statistics 多个子域。
- 子域 skill 与当前代码事实冲突时继续按文档改代码。
- 为了目录统一移动 DTO、VO、DO、Mapper、MQ、Job 或统计口径对象。

## Acceptance Criteria

- 本文件只承担导航和边界提醒。
- 具体业务规则、字段映射、错误码、事务边界和验证命令必须写在子域 skill 中。
- 修改 Mall 子域前，必须确认使用的是对应子域 skill，而不是本父级入口。
