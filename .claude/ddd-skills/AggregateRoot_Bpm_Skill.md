---
name: aggregate-root-bpm-skill
description: Use when refactoring BPM/Flowable aggregates without changing workflow behavior.
---

# AggregateRoot BPM Skill

Status: production-review.

## Purpose
Define BPM/Flowable DDD boundaries for models, process instances, tasks, OA leave, forms, groups, listeners, expressions, and categories.

## Boundaries
Flowable core behavior remains sourced from legacy services until each closed loop is proven equivalent. Existing DDD directories are not a production replacement by themselves.

## Dependencies
Flowable services (`RepositoryService`, `RuntimeService`, `TaskService`, `HistoryService`, `ManagementService`), `BpmnModel`, and `BpmnModelUtils` stay in application/infrastructure or legacy service boundary, never domain.

## Invariants
Domain must not depend on Flowable, Spring, MyBatis, DO, or VO. Preserve process/task state, candidates, events, `BpmProcessInstanceStatusEvent`, errors, and transaction boundaries.

## Refactor Steps
Migrate one BPM closed loop at a time. Read Controller, VO, DO, Mapper, Service, and tests first. If docs conflict with compilable behavior, current behavior wins.

## Acceptance Criteria
Controller/API/DTO/error/event contracts unchanged; candidate rules, process state, task approval, forms, and categories covered by regression tests.

## Verification
Compile BPM API/server and run focused BPM tests such as category, form, user group, and task candidate tests.
