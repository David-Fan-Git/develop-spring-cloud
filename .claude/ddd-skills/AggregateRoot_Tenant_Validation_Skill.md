---
name: aggregate-root-tenant-validation-skill
description: Use when refactoring System Tenant aggregate lifecycle, package collaboration, admin bootstrap, permission assignment, or validation behavior.
---

# AggregateRoot Tenant Validation Skill

Status: production-ready.

## Purpose
Tenant aggregate blueprint for lifecycle, package collaboration, administrator bootstrap, permission assignment, validation, and DDD boundaries.

## Boundaries
`Tenant` manages name, status, website/domain, package reference, expiry, account quota, system tenant protection, enable/disable, and deletion mark. TenantPackage/User/Role/Menu/Permission collaborate only in application layer.

## Dependencies
`TenantPackageService.validTenantPackage(packageId)`, admin role/user creation, permission assignment, `DomainEventPublisher`, legacy `TenantServiceImpl#createTenant`, `@DSTransactional`, `@DataPermission(enable=false)`.

## Invariants
`TenantDO.PACKAGE_ID_SYSTEM = 0L`; create request `id` is usually null and DDD create must insert first then backfill ID; preserve request status or default ENABLE only when missing; name and each website globally unique with self-exclusion on update; system tenant cannot be modified/deleted; package changes sync role menus; `getAndValidateTenant` maps missing/disabled/expired to `TENANT_NOT_EXISTS`, `TENANT_DISABLE(name)`, `TENANT_EXPIRE(name)`.

## Refactor Steps
Align DO/VO/Domain/Repository/Application against current facts. Split create/update/delete/list. Domain collects events; application publishes and orchestrates external collaboration.

## Acceptance Criteria
`Tenant` has no infrastructure dependency; factory create/reconstitute semantics separated; repository does not expose Controller VO; create handles `id=null`, returns generated ID, creates admin role/user, assigns permissions, and writes `contactUserId`.

## Verification
`mvn compile -pl develop-module-system/develop-module-system-server -am`
`mvn test -pl develop-module-system/develop-module-system-server -Dtest=*Tenant*`
