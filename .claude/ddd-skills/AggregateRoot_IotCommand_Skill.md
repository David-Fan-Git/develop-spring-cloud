---
name: aggregate-root-iot-command-skill
description: Use when introducing or refactoring IoT command behavior around existing device message semantics.
---

# AggregateRoot IoT Command Skill

## Purpose
Introduce a Command aggregate only around existing DeviceMessage command/reply behavior.

## Boundaries
No independent `iot_command` table exists now. Facts come from `IotDeviceMessage`, `IotDeviceMessageServiceImpl`, TDengine logs, and MQ producer.

## Dependencies
`requestId`, reply message, `serverId` routing, TDengine log, and MQ downstream send.

## Invariants
Preserve `requestId/method/params/data/code/msg` and `id/reportTime/deviceId/tenantId/serverId`. Missing downstream `serverId` throws `DEVICE_DOWNSTREAM_FAILED_SERVER_ID_NULL`. Do not replace current message/reply chain with a new state machine.

## Refactor Steps
Abstract current DeviceMessage command facts first. New state may wrap but not change reply, ACK, log, or routing semantics.

## Acceptance Criteria
Domain pure; command send, ACK, failure, timeout, query, log, and MQ behavior unchanged.

## Verification
IoT forbidden-import grep, IoT core tests, command/message tests, iot server compile.
