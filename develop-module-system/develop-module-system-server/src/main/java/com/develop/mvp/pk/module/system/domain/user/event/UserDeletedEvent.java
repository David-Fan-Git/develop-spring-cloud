package com.develop.mvp.pk.module.system.domain.user.event;

// Skill: AggregateRoot_User_Validation_Skill — UserDeletedEvent
// 触发时机：用户删除成功后 → 消费者清理权限+岗位（原 deleteUser L252-256）
// 验收标准 AC11：发布后由 UserDeletedPermissionCleaner 消费

import java.time.LocalDateTime;

public record UserDeletedEvent(Long userId, String username, LocalDateTime occurredAt) implements DomainEvent {
    public UserDeletedEvent(Long userId, String username) {
        this(userId, username, LocalDateTime.now());
    }
}
