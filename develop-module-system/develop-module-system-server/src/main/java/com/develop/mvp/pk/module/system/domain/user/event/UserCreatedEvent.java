package com.develop.mvp.pk.module.system.domain.user.event;

// Skill: AggregateRoot_User_Validation_Skill — UserCreatedEvent
// 触发时机：用户创建成功后

import java.time.LocalDateTime;

public record UserCreatedEvent(Long userId, String username, Long tenantId, LocalDateTime occurredAt) implements DomainEvent {
    public UserCreatedEvent(Long userId, String username, Long tenantId) {
        this(userId, username, tenantId, LocalDateTime.now());
    }
}
