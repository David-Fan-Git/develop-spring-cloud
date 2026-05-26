# Markdown Token Trim Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Reduce token cost from Claude context Markdown while preserving mandatory project, DDD, build, and Git workflow constraints.

**Architecture:** Use layered compression. `CLAUDE.md` becomes a compact operational entry point, DDD skills keep production constraints but lose repeated prose, and historical `docs/superpowers` artifacts become concise summaries or indexes.

**Tech Stack:** Markdown, Git, shell validation, Maven command references only.

---

### Task 1: Baseline Measurement

**Files:**
- Read: `CLAUDE.md`
- Read: `.claude/ddd-skills/*.md`
- Read: `docs/superpowers/{plans,reports,specs}/*.md`

- [ ] Run baseline line-count command:

```bash
find . -path "./.git" -prune -o \( -name "CLAUDE.md" -o -path "./.claude/ddd-skills/*.md" -o -path "./docs/superpowers/*.md" -o -path "./docs/superpowers/*/*.md" \) -type f -print0 | xargs -0 wc -l | sort -nr > /tmp/md-token-trim-before.txt
```

- [ ] Confirm total line count is captured from the final `total` row.

### Task 2: Compress Root CLAUDE.md

**Files:**
- Modify: `CLAUDE.md`

- [ ] Replace long prose with compact sections:
  - Project snapshot
  - Commands
  - DDD hard rules
  - Module structure
  - Git workflow
  - Safety rules

- [ ] Preserve exact Maven commands for compile, package, tests, server, and gateway.
- [ ] Preserve DDD rules: domain purity, application orchestration, infrastructure adapters, `service/dal` as migration sources, API local/remote contract strategy.

### Task 3: Compress DDD Standards

**Files:**
- Modify: `.claude/ddd-skills/DDD_Skill_Production_Readiness_Standard.md`
- Modify: `.claude/ddd-skills/Module_Structure_Standard.md`
- Modify: `.claude/ddd-skills/DDD_Skill_Module_Index.md`

- [ ] Keep mandatory production-readiness checks.
- [ ] Keep module runtime/API/layer placement rules.
- [ ] Convert long explanations into short checklists.

### Task 4: Compress Aggregate Skills

**Files:**
- Modify: `.claude/ddd-skills/AggregateRoot_*.md`

- [ ] For each aggregate skill, keep only:
  - Purpose
  - Boundaries
  - Dependencies
  - Invariants
  - Refactor steps
  - Acceptance criteria
- [ ] Remove repeated background, generic DDD explanations, and duplicated template prose.
- [ ] Do not remove aggregate-specific business constraints.

### Task 5: Index Historical Superpowers Docs

**Files:**
- Modify: `docs/superpowers/plans/*.md`
- Modify: `docs/superpowers/reports/*.md`
- Modify: `docs/superpowers/specs/*.md`
- Modify: `docs/superpowers/system-api-remote-adapter-change-report.md`

- [ ] For completed historical artifacts, replace long bodies with concise summaries containing:
  - Status
  - Scope
  - Key decision/result
  - Validation reference
- [ ] Keep the new design and plan files detailed enough for this task.

### Task 6: Validate Constraints and Savings

**Files:**
- Read/validate all modified Markdown files

- [ ] Run after line-count command:

```bash
find . -path "./.git" -prune -o \( -name "CLAUDE.md" -o -path "./.claude/ddd-skills/*.md" -o -path "./docs/superpowers/*.md" -o -path "./docs/superpowers/*/*.md" \) -type f -print0 | xargs -0 wc -l | sort -nr > /tmp/md-token-trim-after.txt
```

- [ ] Run keyword guard:

```bash
grep -R "domain\|application\|infrastructure\|service/dal\|local/remote\|mvn\|PR" CLAUDE.md .claude/ddd-skills docs/superpowers >/tmp/md-token-trim-keywords.txt
```

- [ ] Run whitespace check:

```bash
git diff --check
```

### Task 7: Review and Commit

**Files:**
- Review: `git diff --stat`
- Review: `git diff -- CLAUDE.md .claude/ddd-skills docs/superpowers`

- [ ] Confirm no Java, Maven, SQL, or application behavior files changed.
- [ ] Commit only Markdown context changes and related design/plan files.

```bash
git add CLAUDE.md .claude/ddd-skills docs/superpowers

git commit -m "docs: trim Claude context markdown"
```
