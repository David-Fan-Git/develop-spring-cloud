package com.develop.mvp.pk.module.system.infrastructure.user;

// Skill: AggregateRoot_User_Validation_Skill — 基础设施适配器
// DDD 角色：DomainEventPublisher 的实现，委托 Spring ApplicationEventPublisher

import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher springPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher springPublisher) {
        this.springPublisher = springPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        springPublisher.publishEvent(event);
    }
}
