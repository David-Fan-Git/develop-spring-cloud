---
name: aggregate-root-mall-trade-skill
description: Use when refactoring Mall Trade cart, order, after-sale, brokerage, delivery, config, payment, or notification behavior.
---

# AggregateRoot Mall Trade Skill

Status: production-review.

## Purpose
Mall Trade subdomain: cart, order, after-sale, brokerage, delivery, config, payment, notification.

## Boundaries
`TradeOrderUpdateServiceImpl` remains production fact source. Current DDD application covers only a subset.

## Dependencies
PayOrder/PayRefund, handler chain, price calculator chain, `@TenantJob`, `@TradeOrderLog`, `@AfterSaleLog`, site-message short-circuit behavior.

## Invariants
Order creation performs price calculation, before/after handlers, order/detail insert, cart deletion, PayOrder creation, and logs. Zero-yuan orders do not create PayOrder. Payment callback is idempotent by `payOrderId` and validates status/amount/merchantOrderId. Status updates use `updateByIdAndStatus`. Cancellation re-queries PayOrder to avoid delayed-pay race. Zero-refund after-sale completes directly. Refund callback validates PayRefund status/amount/merchantRefundId. Order-item after-sale status rollback/success update is critical.

## Refactor Steps
Migrate one closed loop at a time: cart/order/after-sale/etc. Add regression tests first, then let legacy facade delegate to DDD application.

## Acceptance Criteria
Order, payment, refund, after-sale state machines plus logs/notifications/tenant/jobs unchanged. Known debts must be handled: repository constructing Controller PageReqVO, `payChannelCode` null mapping, raw domain exceptions.

## Verification
Trade API/server compile; Cart, TradeOrder, OrderItem, AfterSale, application service tests.
