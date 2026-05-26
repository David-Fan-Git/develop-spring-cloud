---
name: aggregate-root-pay-skill
description: Use when refactoring Pay high-risk money-flow aggregates without changing external payment behavior.
---

# AggregateRoot Pay Skill

Status: production-review.

## Purpose
Pay production refactoring contract for PayApp, PayChannel, PayOrder, PayOrderExtension, PayRefund, PayTransfer, PayWallet, PayWalletRecharge, PayWalletRechargePackage, PayWalletTransaction, and PayNotify.

## Boundaries
Migrate one aggregate or small flow per batch. Legacy service is the behavior source. PayNotify is DB-backed retry/log/lock pipeline, not a simple domain event.

## Dependencies
API/DTO Feign annotation migration debt; `PayClientFactory`/`PayClient`; Redis number/lock DAO; `TenantUtils.execute(...)`; `TenantUtils.addTenantHeader(...)`; `@TenantJob`; callback `@TenantIgnore`; notify task/log DO.

## Invariants
Submit order intentionally avoids wrapping third-party remote call in rollback transaction. Create extension before channel call. Order/refund/transfer status uses expected-status optimistic update. Duplicate payment callback is idempotent. Refund creation swallows channel exception because remote may succeed. Wallet get-or-create uses Redis double-check lock. Balance cannot be negative and every balance change writes transaction. Notify keeps tenant header, retry, lock, and log behavior.

## Refactor Steps
Use small batches: PayOrder submit/notify, PayRefund notify, PayTransfer notify, PayWallet balance, API local/remote split. Read legacy source and tests for the target flow first.

## Acceptance Criteria
API/Controller/callback DTO, errors, tenant, transactions, optimistic locks, wallet locks, and notify persistence unchanged. Domain has no Spring/MyBatis/Feign/Redis/Mapper/DO/VO/HTTP client.

## Verification
`mvn compile -pl develop-module-pay/develop-module-pay-api -am -DskipTests`
`mvn compile -pl develop-module-pay/develop-module-pay-server -am -DskipTests`
Run focused PayApp, PayChannel, PayOrder, PayRefund, PayNotify, PayTransfer, PayWallet, and PayWalletTransaction tests. Docs-only: `git diff --check -- .claude/ddd-skills/AggregateRoot_Pay_Skill.md`.
