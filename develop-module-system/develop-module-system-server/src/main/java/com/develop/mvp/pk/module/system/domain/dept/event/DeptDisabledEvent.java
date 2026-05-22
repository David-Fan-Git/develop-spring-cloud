package com.develop.mvp.pk.module.system.domain.dept.event;
import java.time.LocalDateTime;
public record DeptDisabledEvent(Long deptId, LocalDateTime occurredAt) implements DeptDomainEvent {
    public DeptDisabledEvent(Long deptId) { this(deptId, LocalDateTime.now()); }
}
