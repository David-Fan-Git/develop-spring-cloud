---
name: aggregate-root-role-menu-skill
description: Use when refactoring System RBAC role, menu, permission, role-menu, user-role, or data-permission behavior.
---

# AggregateRoot Role/Menu Skill

Status: production-review.

## Purpose
System RBAC boundary: Role, Menu, Permission, RoleMenu, UserRole, menu filtering, permission checks, department data permission.

## Boundaries
Role/Menu DDD is incomplete. `RoleServiceImpl`, `MenuServiceImpl`, and `PermissionServiceImpl` are current facts. Migrate only one of Role write/query, Menu write/query, RoleMenu, UserRole, permission judgment, or data permission at a time.

## Dependencies
Controllers `/system/role`, `/system/menu`, `/system/permission`; stable `RoleApi`/`PermissionApi`; remote client `contextId`; `PermissionApiImpl` remains `@Primary`; TenantService, AdminUserService, DeptService, LogRecord, DataPermission, cache.

## Invariants
Menu is global system metadata. Role name/code unique. Super-admin code cannot be used by ordinary custom roles. System roles cannot be changed/deleted. Deleting role cleans user-role and role-menu. Button menu clears `component/componentName/icon/path`. Unknown permission string returns false. Preserve super-admin fallback, diff replacement in role/user assignment, all/self/custom/own/child data-permission semantics, and data-permission bypass.

## Refactor Steps
Create standard skeleton: `application/permission/port/inbound`, `port/outbound`, `service`, `infrastructure/permission/persistence|external|rpc|cache|messaging`. Old services may become thin but must keep cache, transaction, log, and tenant behavior.

## Acceptance Criteria
Controller/API/VO/Excel/page unchanged; errors use current `ErrorCodeConstants`; preserve `@DSTransactional`, cache eviction breadth, tenant menu filtering, two simple-list aliases, id-null create compatibility, valid role update.

## Verification
`RoleServiceImplTest`, `MenuServiceImplTest`, `PermissionServiceTest`, `SystemArchitectureTest`; system API/server compile.
