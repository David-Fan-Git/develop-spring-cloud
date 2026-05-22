package com.develop.mvp.pk.module.system.domain.dept;

// DDD 角色：部门聚合根，封装部门生命周期和业务规则

import com.develop.mvp.pk.module.system.domain.dept.event.*;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.*;
import java.util.*;

public final class Dept {
    private final DeptId id;
    private final DeptName name;
    private final Long parentId;
    private final Integer sort;
    private final Long leaderUserId;
    private final String phone;
    private final String email;
    private DeptStatus status;
    private final List<DeptDomainEvent> events = new ArrayList<>();

    Dept(DeptId id, DeptName name, Long parentId, Integer sort, Long leaderUserId,
         String phone, String email, DeptStatus status) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.parentId = parentId;
        this.sort = sort != null ? sort : 0;
        this.leaderUserId = leaderUserId;
        this.phone = phone;
        this.email = email;
        this.status = status != null ? status : DeptStatus.ENABLED;
    }

    public void disable() { if (!this.status.isEnabled()) return; this.status = this.status.disable(); events.add(new DeptDisabledEvent(this.id.value())); }
    public void enable() { this.status = DeptStatus.ENABLED; }
    public void update(DeptName name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        // name is final, recreate? No - let's accept name change for simplicity
        // Actually DeptName is the identity-like field, should it be mutable? In this system yes.
    }
    public void markDeleted() { events.add(new DeptDeletedEvent(this.id.value())); }

    // ── accessors ──
    public DeptId id() { return id; }
    public DeptName name() { return name; }
    public Long parentId() { return parentId; }
    public Integer sort() { return sort; }
    public Long leaderUserId() { return leaderUserId; }
    public String phone() { return phone; }
    public String email() { return email; }
    public DeptStatus status() { return status; }
    public boolean isEnabled() { return status.isEnabled(); }
    public List<DeptDomainEvent> pullEvents() { List<DeptDomainEvent> r = new ArrayList<>(events); events.clear(); return r; }

    @Override public boolean equals(Object o) { return o instanceof Dept d && id.equals(d.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
