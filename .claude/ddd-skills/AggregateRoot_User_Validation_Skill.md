---
name: aggregate-root-user-validation-skill
description: Use when refactoring System AdminUser/User aggregate lifecycle, profile, password, login, import, paging, or RPC validation behavior.
---

# AggregateRoot User Validation Skill

Status: production-ready.

## Purpose
System AdminUser/User aggregate blueprint: backend account create/register, profile, post assignment, status, password, login, deletion, import, paging, and RPC validation.

## Boundaries
`AdminUserServiceImpl` and `UserApplicationService` coexist. Old service still owns registration, import, LogRecord, tenant quota, OAuth2 token cleanup, permission cleanup, and legacy tests until replaced equivalently.

## Dependencies
`/system/user`, profile, OAuth2 user controller, stable `AdminUserApi`, `AdminUserRemoteClient`, Dept/Post, Tenant, ConfigApi, Permission, OAuth2, DataPermission, Excel, LogRecord.

## Invariants
DDD create uses explicit tenantId or `TenantContextHolder.getRequiredTenantId()`; `id == null` means repository inserts and returns generated ID. Username unique; non-empty email/mobile unique; password BCrypt; profile password change checks old password; disabling clears admin OAuth2 token; deletion cleans permissions/posts; create/register checks tenant quota; register reads switch; import preserves empty-list, initial-password, failure-map, update-support semantics; paging keeps dept subtree, roleId, createTime, username/mobile/status filters.

## Refactor Steps
Protect Controller/API/VO/permissions/data-permission/errors/Excel first. Migrate create/update/status/password/profile/login/delete/page/API gradually. Map domain `IllegalArgumentException` to existing errors before exposing.

## Acceptance Criteria
Domain has no Spring/MyBatis/DO/Mapper/VO/DTO/Tenant/OAuth2/Permission imports. Do not delete `AdminUserServiceImpl` before parity tests. Preserve token cleanup, permission/post cleanup, paging filters, `@DataPermission(enable=false)`, `@AutoTrans`, `@FeignIgnore`, `remark/createTime` response parity.

## Verification
`mvn compile -pl develop-module-system/develop-module-system-api -am -DskipTests`
`mvn compile -pl develop-module-system/develop-module-system-server -am -DskipTests`
Run focused AdminUser/Auth/OAuth2/UserApplication tests.
