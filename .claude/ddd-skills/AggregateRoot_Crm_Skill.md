---
name: aggregate-root-crm-skill
description: Use when splitting CRM draft guidance into production-ready aggregate skills.
---

# AggregateRoot CRM Skill

Status: draft; do not use for bulk production refactoring.

## Purpose
CRM multi-aggregate scoping guide.

## Boundaries
Potential aggregates: Customer, Clue, Contact, Business, Contract, Receivable, CrmPermission, FollowUpRecord. This file covers too many aggregates for direct implementation.

## Dependencies
Contract/Receivable reference BPM approval. Customer references Clue/Contact/Business/Contract by ID. Permission uses `(bizType, bizId, userId)`.

## Invariants
Customer name globally unique; deal status is one-way; Customer deletion requires no related Contact/Business/Contract; workflow keys are `crm-contract-audit` and `crm-receivable-audit`; each business object has exactly one OWNER.

## Refactor Steps
Create a single-aggregate production skill with facts, fields, errors, transactions, and tests before changing code.

## Acceptance Criteria
This draft is only for scoping; never use it to batch-change CRM production code.

## Verification
Add aggregate-specific Maven compile/test commands when upgrading.
