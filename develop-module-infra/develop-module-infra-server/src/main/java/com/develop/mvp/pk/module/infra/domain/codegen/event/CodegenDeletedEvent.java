package com.develop.mvp.pk.module.infra.domain.codegen.event;

// DDD 角色：代码生成表定义删除后发布
import com.develop.mvp.pk.module.infra.domain.event.DomainEvent;

import java.time.LocalDateTime;

public record CodegenDeletedEvent(Long tableId, String tableName, LocalDateTime occurredAt) implements DomainEvent {
    public CodegenDeletedEvent(Long tableId, String tableName) {
        this(tableId, tableName, LocalDateTime.now());
    }
}
