# DDD Skill: AggregateRoot_Erp_Skill

## Status

此文件是 ERP 模块 DDD skill 的占位入口，不是生产级重构指南。

旧内容与 IOT、MES、MP、Report 的占位 skill 完全重复，已合并到统一草稿：`AggregateRoot_Module_Draft_Skill.md`。

## How To Use

1. 先阅读 `DDD_Skill_Production_Readiness_Standard.md`。
2. 再阅读 `AggregateRoot_Module_Draft_Skill.md` 了解通用草稿约束。
3. 在修改 ERP 聚合前，必须基于当前 ERP 代码事实源升级本文件，补齐 Controller、VO/DTO、DO、Mapper、Service、Convert、ErrorCode、事务边界、外部契约和验证命令。

## Red Flag

如果本文件仍未升级为 ERP 专属生产级 skill，禁止按它直接执行 ERP DDD 重构。
