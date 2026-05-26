---
name: aggregate-root-mall-promotion-skill
description: Use when refactoring Mall Promotion activities, coupons, discounts, rewards, combination, bargain, or point activity behavior.
---

# AggregateRoot Mall Promotion Skill

Status: production-review.

## Purpose
Mall Promotion subdomain: banner, seckill, coupon, discount, reward, combination, bargain, point activity.

## Boundaries
DDD currently covers only parts of Banner, CouponTemplate, and SeckillActivity. Most behavior still follows legacy services.

## Dependencies
Product APIs, TradeOrderApi, WebSocketSenderApi, Member/System clients, `CouponExpireJob`, `CombinationRecordExpireJob`, `CouponTakeByRegisterConsumer`.

## Invariants
`SeckillActivityApi` currently uses `/discount-activity` prefix; keep it. `PointActivityApi` tag currently says “秒杀活动”; do not fix casually. Seckill/Point/Bargain stock updates keep affected-row guard. Coupon state machine and CAS semantics stay unchanged.

## Refactor Steps
Migrate one activity, stock, or coupon closed loop per batch. Local/remote API split cannot change DTOs or signatures.

## Acceptance Criteria
Join validation, stock increase/decrease, coupon use/return, combination/bargain/point behavior matches legacy.

## Verification
Promotion API/server compile; focused Banner, CouponTemplate, Seckill tests.
