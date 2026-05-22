package com.develop.mvp.pk.module.system.domain.permission.event;

import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import java.time.LocalDateTime;

public record RoleDeletedEvent(Long roleId, String code, LocalDateTime occurredAt) implements DomainEvent {
    public RoleDeletedEvent(Long roleId, String code) { this(roleId, code, LocalDateTime.now()); }
}
