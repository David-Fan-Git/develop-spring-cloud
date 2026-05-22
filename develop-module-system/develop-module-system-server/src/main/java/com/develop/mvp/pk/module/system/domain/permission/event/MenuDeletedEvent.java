package com.develop.mvp.pk.module.system.domain.permission.event;

import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import java.time.LocalDateTime;

public record MenuDeletedEvent(Long menuId, LocalDateTime occurredAt) implements DomainEvent {
    public MenuDeletedEvent(Long menuId) { this(menuId, LocalDateTime.now()); }
}
