---
name: aggregate-root-mall-statistics-skill
description: Use when refactoring Mall Statistics read models, jobs, dashboards, or API enrichment.
---

# AggregateRoot Mall Statistics Skill

Status: production-review.

## Purpose
Mall Statistics product/trade read models, statistics jobs, dashboard queries, and API enrichment.

## Boundaries
This is a read-model/statistics context, not a command aggregate. Member/pay dashboard legacy read services are not forced into ProductStatistics/TradeStatistics.

## Dependencies
Product/Trade statistics routes, Excel, XXL Job, `@TenantJob`, API module `TimeRangeTypeEnum`.

## Invariants
Product DDD currently lacks `browseUserCount/afterSaleCount/afterSaleRefundPrice/browseConvertPercent`. Trade DDD currently lacks wallet/recharge fields. Day-stat job idempotence returns existing-data message, not upsert. Formulas: `browseConvertPercent = 100 * orderPayCount / browseUserCount`; `turnoverPrice = orderPayPrice + rechargePayPrice`; `expensePrice = walletPayPrice + brokerageSettlementPrice + afterSaleRefundPrice`.

## Refactor Steps
Keep `/statistics/product/*`, `/statistics/trade/*`, Excel files, sheets, and VO. Fill missing fields before replacing read paths.

## Acceptance Criteria
Product/trade statistics, export, trend, dashboard metrics, and job parameter semantics unchanged.

## Verification
`ProductStatisticsTest`, `TradeStatisticsTest`, statistics server compile, domain forbidden-import grep.
