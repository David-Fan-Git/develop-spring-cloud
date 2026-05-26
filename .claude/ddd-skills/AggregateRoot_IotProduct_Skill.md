---
name: aggregate-root-iot-product-skill
description: Use when refactoring IoT Product aggregate lifecycle and product property table synchronization.
---

# AggregateRoot IoT Product Skill

## Purpose
IoT Product aggregate for product info, key/secret, device type, protocol type, publish status, dynamic registration, and TDengine product-property table sync.

## Boundaries
Product manages lifecycle. Device, ThingModel, and property-table sync collaborate through application ports.

## Dependencies
`IotDevicePropertyService.defineDevicePropertyData(id)`; Redis cache `RedisKeyConstants.PRODUCT` with current `@TenantIgnore`; Excel export.

## Invariants
`productKey` unique on create; create default `UNPUBLISHED`; `productSecret` uses `IdUtil.fastSimpleUUID()`; update cannot change `productKey`; published products cannot be deleted; products with devices cannot be deleted; publishing syncs TDengine product-property table.

## Refactor Steps
Preserve Controller/VO/Excel behavior. Migrate create/update/delete/status/list/page/syncProductPropertyTable into use-case ports.

## Acceptance Criteria
Product API/use cases complete; cache and TDengine sync unchanged; Excel file `产品.xls`, sheet `数据` unchanged.

## Verification
Product tests; domain forbidden-import grep; iot server compile.
