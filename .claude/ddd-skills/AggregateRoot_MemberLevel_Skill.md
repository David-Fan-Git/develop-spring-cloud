---
name: aggregate-root-member-level-skill
description: Use when refactoring MemberLevel configuration and experience-driven level changes.
---

# AggregateRoot MemberLevel Skill

Status: production-review.

## Purpose
MemberLevel aggregate: member level configuration, admin/app API, level config, and experience-driven level changes.

## Boundaries
Only MemberLevel. Exclude MemberGroup, MemberTag, MemberPointRecord, MemberSignInConfig, MemberSignInRecord. Controller uses `MemberLevelApplicationService`; RPC `MemberLevelApiImpl` still uses legacy service.

## Dependencies
Member user, experience record mapper, RPC `MemberLevelApi`, legacy `MemberLevelService`.

## Invariants
Name unique; numeric level unique; experience lies in adjacent level interval; levels with users cannot be deleted; zero experience delta is no-op; positive non-add biz type standardizes to negative; total experience cannot be negative; new level is highest enabled level matching experience; unchanged level does not write record to match legacy.

## Refactor Steps
First resolve migration debts: user-not-found behavior in `addExperience`, same-level extra record, missing `createTime/icon` mapping, `findByNameLike` stub, `findAll` sorting, direct mapper injection in application.

## Acceptance Criteria
Admin/app/RPC behavior, errors, field mapping, experience records, and level-change semantics match legacy.

## Verification
Member API/server compile; `MemberLevelTest` and focused tests.
