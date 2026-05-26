---
name: aggregate-root-mall-skill
description: Use only to choose the correct Mall subdomain skill before refactoring Mall code.
---

# AggregateRoot Mall Skill

Status: navigation only; not a production aggregate guide.

## Purpose
Route Mall work to Product, Promotion, Trade, or Statistics skills.

## Boundaries
Do not use this file to design orders, products, promotions, or statistics. Choose one subdomain skill.

## Dependencies
Use `AggregateRoot_MallProduct_Skill.md`, `AggregateRoot_MallPromotion_Skill.md`, `AggregateRoot_MallTrade_Skill.md`, or `AggregateRoot_MallStatistics_Skill.md`.

## Invariants
Never change Product/Promotion/Trade/Statistics in one batch. Do not move DTO/VO/DO/Mapper/MQ/Job/statistics objects just for directory uniformity.

## Refactor Steps
Read production standard, then the target subdomain skill, then current source facts.

## Acceptance Criteria
This file remains navigation only; concrete fields, errors, transactions, and commands belong in subdomain skills.

## Verification
Use the chosen subdomain skill verification.
