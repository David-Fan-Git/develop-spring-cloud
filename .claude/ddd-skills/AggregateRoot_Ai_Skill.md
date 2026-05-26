---
name: aggregate-root-ai-skill
description: Use when upgrading or refactoring the AI Model aggregate draft.
---

# AggregateRoot Ai Skill

Status: draft; upgrade before production refactoring.

## Purpose
AI Model configuration lifecycle: create, update, delete, enable/disable, query, default model selection, validation, and Spring AI integration boundary.

## Boundaries
Aggregate root: `AiModel`. Do not include `AiApiKey`, `AiChatRole`, or `AiTool`; reference them only by ID.

## Dependencies
Use `AiModelRepository` in domain/application boundary. Spring AI integration belongs outside domain.

## Invariants
Model status, type, platform, default selection, and enable/disable rules must be protected by aggregate/application logic. Facts are incomplete; do not use this file alone for production migration.

## Refactor Steps
First add current source anchors, field mapping, error codes, transaction boundary, external contract, and verification commands; then migrate by DDD layers.

## Acceptance Criteria
Domain stays pure; API/VO/DTO behavior is preserved; this skill covers only `AiModel`.

## Verification
`mvn compile -pl develop-module-ai/develop-module-ai-server -am`
