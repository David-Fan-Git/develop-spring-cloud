---
name: aggregate-root-infra-skill
description: Use when refactoring Infra configuration, file, codegen, logging, or WebSocket capabilities.
---

# AggregateRoot Infra Skill

Status: production-review.

## Purpose
Define DDD boundaries for Infra config, datasource, file/file-config, codegen, logs, and WebSocket.

## Boundaries
Infra provides shared platform capabilities and must not carry caller business logic. Stable APIs currently carrying `@FeignClient` are migration debt.

## Dependencies
`ConfigApi`, `FileApi`, `WebSocketSenderApi`, FileClient cache, Codegen DB introspection/builder/engine, Logger tenant fallback.

## Invariants
FileClient master pseudo-key is `0L`; `testFileConfig` uploads `file/erweima.jpg` and returns URL; Codegen sync preserves no-change and column IDs; logging truncates fields and uses `TenantUtils.executeIgnore(...)`; API error-log creation failure must not affect caller business.

## Refactor Steps
Migrate one Infra capability at a time. Preserve API signatures, caches, generators, logs, and WebSocket contracts. Split stable contract from remote client gradually.

## Acceptance Criteria
Domain is free of Mapper/DO/Spring/Feign; Config/File/WebSocket API behavior unchanged; Codegen/File/Logger behavior tested.

## Verification
Compile infra API/server; run config, file config, codegen, and logger focused tests.
