package com.develop.mvp.pk.module.system.domain.user.event;

// Skill: AggregateRoot_User_Validation_Skill — UserPasswordChangedEvent
// 触发时机：密码变更成功后

import java.time.LocalDateTime;

public record UserPasswordChangedEvent(Long userId, LocalDateTime occurredAt) implements DomainEvent {
    public UserPasswordChangedEvent(Long userId) {
        this(userId, LocalDateTime.now());
    }
}
