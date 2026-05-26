---
name: aggregate-root-iot-device-skill
description: Use when refactoring IoT Device aggregate lifecycle, status, topology, grouping, firmware, location, registration, authentication, and cache behavior.
---

# AggregateRoot IoT Device Skill

## Purpose
IoT Device aggregate for device instance, secret, status, gateway/sub-device topology, group, firmware, location, dynamic registration, authentication, and cache.

## Boundaries
Device owns lifecycle and topology rules. Product, Group, Firmware, and Property collaborate by ID/application layer.

## Dependencies
Product supplies `productKey` and `deviceType`; Redis cache uses `RedisKeyConstants.DEVICE` and current `@TenantIgnore`; gateway/sub-device, firmware, and location are application orchestration.

## Invariants
`productKey + deviceName` unique; non-empty `serialNumber` globally unique; create copies product info, generates `deviceSecret`, default `INACTIVE`; update cannot change `deviceName` or `productId`; gateway with sub-devices cannot be deleted; gateway offline cascades online sub-devices offline.

## Refactor Steps
Keep legacy service behavior while extracting create, profile update, online/offline, gateway binding, group/firmware/location changes into aggregate/application use cases.

## Acceptance Criteria
Controller/API/cache key/tenant-ignore behavior unchanged; domain has no Spring/MyBatis/Mapper/DO/VO.

## Verification
Domain forbidden-import grep; Device tests; iot server compile.
