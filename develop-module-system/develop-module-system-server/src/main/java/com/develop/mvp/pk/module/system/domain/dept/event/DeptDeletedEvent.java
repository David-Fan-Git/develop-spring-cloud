package com.develop.mvp.pk.module.system.domain.dept.event;
import java.time.LocalDateTime;
public record DeptDeletedEvent(Long deptId, LocalDateTime occurredAt) implements DeptDomainEvent {
    public DeptDeletedEvent(Long deptId) { this(deptId, LocalDateTime.now()); }
}
