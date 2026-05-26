---
name: aggregate-root-mall-product-skill
description: Use when refactoring Mall Product category, brand, SPU, SKU, property, comment, favorite, or browse-history behavior.
---

# AggregateRoot Mall Product Skill

Status: production-review.

## Purpose
Mall Product subdomain boundaries: category, brand, SPU, SKU, properties, comments, favorites, browse history.

## Boundaries
Product APIs currently still carry Feign annotations. Production behavior mostly remains in legacy services. Some DDD exists for ProductCategory/ProductBrand/ProductSpu; Property/Comment/Favorite/BrowseHistory are not complete DDD aggregates.

## Dependencies
Product API, Controller/VO/DO/Mapper/Convert/Service, SKU stock mapper affected-row guard, Excel, paging, comments, browse history.

## Invariants
SKU stock decrement requires `stock >= abs(incrCount)` affected-row guard and throws `SKU_STOCK_NOT_ENOUGH`; SPU price/stock derive from SKU list; category deletion rejects child categories and bound SPUs; SPU deletion requires RECYCLE; comments can read deleted SKU/SPU; browse history ignores empty user, de-duplicates by user/SPU, and keeps at most 100 records.

## Refactor Steps
Migrate one aggregate/closed loop at a time. Split stable contract and remote client first. Do not casually move DTO/VO/DO/Mapper.

## Acceptance Criteria
Product stock, category, SPU/SKU, property, comment, favorite, browse-history API fields and behavior unchanged.

## Verification
Product API/server compile; stock behavior tests and focused Product service tests.
