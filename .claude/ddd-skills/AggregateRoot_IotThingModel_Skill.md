---
name: aggregate-root-iot-thing-model-skill
description: Use when refactoring IoT ThingModel aggregate for product properties, events, and services.
---

# AggregateRoot IoT ThingModel Skill

## Purpose
Product thing-model function definitions: properties, events, and services.

## Boundaries
Primary association is `productId`; `productKey` is redundant. Modbus point updates only sync redundant identifier/name.

## Dependencies
Product publish-status validation; cache `RedisKeyConstants.THING_MODEL_LIST`; current methods use `@TenantIgnore`; `get-tsl` returns `properties/services/events`.

## Invariants
Within one product, `identifier` and `name` are unique; reserved identifiers `set/get/post/property/event/time/value` are forbidden; published products block create/update/delete; product-not-found currently returns `success(null)`; `THING_MODEL_NAME_EXISTS` and `THING_MODEL_IDENTIFIER_INVALID` sharing a numeric value is current fact.

## Refactor Steps
Preserve legacy query/cache semantics, then migrate create/update/delete/get/list/page/validate/getTsl use cases.

## Acceptance Criteria
TSL output, cache, published-state guard, and error-code behavior unchanged; domain is infrastructure-free.

## Verification
ThingModel tests; domain forbidden-import grep; iot server compile.
