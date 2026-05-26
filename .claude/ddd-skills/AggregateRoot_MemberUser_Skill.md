---
name: aggregate-root-member-user-skill
description: Use when refactoring MemberUser account lifecycle, profile, auth-adjacent, points, experience, or API behavior.
---

# AggregateRoot MemberUser Skill

Status: production-review.

## Purpose
MemberUser aggregate: account lifecycle, create/auto-register, profile, mobile/password, login record, status, level, experience, points, group/tag query mapping.

## Boundaries
Legacy `MemberUserServiceImpl` and DDD coexist. Legacy still carries app/auth/SMS/Weixin/MQ behavior; do not delete until equivalent.

## Dependencies
`MemberUserApi`, `MemberUserRemoteClient`, MQ afterCommit, social/SMS/auth, group/tag/level/point relationships.

## Invariants
`MemberUserApiImpl#validateUser(Long id)` currently throws `USER_MOBILE_NOT_EXISTS` when id missing. Admin update currently only updates nickname/avatar. DDD create uses UUID mobile placeholder; legacy third-party create uses `mobile=null`. Current DDD create does not publish user-create MQ afterCommit. Repository mapping misses `name/sex/birthday/areaId/mark/createTime`; paging ignores date filters; point decrement lacks balance guard.

## Refactor Steps
Preserve stable API + remote Feign. Fix mapping, paging, MQ, error codes, and balance guard before replacing legacy paths.

## Acceptance Criteria
API DTO/Feign/local behavior, registration/login/SMS/social/MQ afterCommit, points/experience/level, and group/tag queries match legacy.

## Verification
Member API/server compile; `*MemberUser*Test`; compile likely consumers if API changes.
