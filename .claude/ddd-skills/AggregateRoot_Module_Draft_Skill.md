---
name: aggregate-root-module-draft-skill
description: Use when replacing placeholder DDD module skills with production-ready aggregate skills.
---

# AggregateRoot Module Draft Skill

Status: draft; not a production refactoring guide.

## Purpose
Shared placeholder for ERP, IOT, MES, MP, Report, and similar modules before dedicated aggregate skills exist.

## Boundaries
Use only for triage. Real work requires an independent, reproducible, production-ready aggregate skill.

## Dependencies
Upgrade against `DDD_Skill_Production_Readiness_Standard.md` and current Controller, VO/DTO, DO, Mapper, Service, Convert, ErrorCode, and tests.

## Invariants
Aggregate root owns business rules/lifecycle; value objects immutable; repository interface lives in domain; domain services do not perform external calls, transactions, or DTO conversion.

## Refactor Steps
Create module-specific skill with facts, data model, signatures, rules, errors, transactions, integrations, acceptance criteria, Maven commands, red flags, rollback, and self-check.

## Acceptance Criteria
No module uses this draft as sole implementation authority; external APIs, permissions, tenants, errors, and DTOs are not implicitly changed.

## Verification
Dedicated skill must provide module-specific compile/test commands.
