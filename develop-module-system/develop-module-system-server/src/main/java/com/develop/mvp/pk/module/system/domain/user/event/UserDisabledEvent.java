package com.develop.mvp.pk.module.system.domain.user.event;

// Skill: AggregateRoot_User_Validation_Skill — UserDisabledEvent
// 触发时机：用户状态变为禁用 → 消费者清理 OAuth2 Token（原 updateUserStatus L238-240）
// 验收标准 AC10：发布后由 UserDisabledTokenCleaner 消费

import java.time.LocalDateTime;

public record UserDisabledEvent(Long userId, LocalDateTime occurredAt) implements DomainEvent {
    public UserDisabledEvent(Long userId) {
        this(userId, LocalDateTime.now());
    }
}
