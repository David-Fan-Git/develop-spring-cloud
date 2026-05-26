---
name: aggregate-root-wms-inventory-skill
description: Use when refactoring WMS Inventory aggregate stock quantity behavior.
---

# AggregateRoot WMS Inventory Skill

Status: draft.

## Purpose
WMS Inventory aggregate draft for stock query and quantity-change slice.

## Boundaries
`WmsInventory` owns one inventory balance: SKU, warehouse, quantity, remark, and quantity changes. Exclude `WmsInventoryHistory`, inbound/outbound/transfer/check orders, SKU master, warehouse master.

## Dependencies
`WmsInventoryRepository`, `WmsInventoryMapper`, `WmsInventoryDO`, `WmsInventoryPageReqVO` adapter, `DomainEventPublisher`, Spring `ApplicationEventPublisher` only in infrastructure.

## Invariants
SKU ID and warehouse ID required; null quantity input becomes 0; quantity cannot be negative; add/subtract amount must be positive; subtract cannot make negative; quantity change emits `WmsInventoryStockChangedEvent`; create persistence emits `WmsInventoryCreatedEvent` with ID; transient aggregate may have `id == null`; equals/hashCode are null-safe.

## Refactor Steps
Keep legacy `WmsInventoryService` batch changes, checks, and history. Only migrate create/find/page/add/subtract/setQuantity/remark and event-publish slice.

## Acceptance Criteria
`WmsInventory` has no Spring/MyBatis/Mapper/DO/VO/Service dependency; `WmsInventoryId` immutable and rejects null; repository is in domain; infrastructure maps DO/domain; insert returns aggregate with persisted ID; negative stock rejected.

## Verification
`mvn compile -pl develop-module-wms/develop-module-wms-server -am`
