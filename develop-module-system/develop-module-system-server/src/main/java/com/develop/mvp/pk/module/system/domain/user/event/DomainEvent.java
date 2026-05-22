package com.develop.mvp.pk.module.system.domain.user.event;

import java.time.LocalDateTime;

// Skill: AggregateRoot_User_Validation_Skill — 领域事件基类
public interface DomainEvent {
    LocalDateTime occurredAt();
}
