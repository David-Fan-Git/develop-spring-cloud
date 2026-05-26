---
name: ddd-skill-module-index
description: Use when locating DDD aggregate skills and deciding whether a skill is production-ready or draft.
---

# DDD Skill Module Index

先读：
- `.claude/ddd-skills/DDD_Skill_Production_Readiness_Standard.md`
- `.claude/ddd-skills/Module_Structure_Standard.md`

## Production Rule

未满足生产就绪标准的聚合 skill 只能作为草稿；必须先按当前代码事实升级，再用于生产级 DDD 重构。

## Known Skills

- System/Infra: Tenant Validation, User Validation, Role Menu, Infra。
- Member: MemberUser, MemberLevel。
- Mall: Mall, MallProduct, MallPromotion, MallTrade, MallStatistics。
- IoT: IotDevice, IotProduct, IotThingModel, IotCommand。
- Other modules: Pay, BPM, CRM, WMS, AI, Module Draft。

## Use Order

1. 先用模块结构标准判断是否涉及 API、runtime unit、DDD 层或特殊模块收口。
2. 再用生产就绪标准判断目标聚合 skill 是否可执行。
3. 读取目标聚合 skill；如果与当前代码冲突，以当前可编译外部行为为准，先修 skill。
4. 每次只处理一个聚合或小子域。

## Red Flags

不要直接执行：无 frontmatter、无事实源路径、无字段映射、无错误码/事务/测试、无 AI Execution Contract、覆盖多个复杂聚合但无边界。
