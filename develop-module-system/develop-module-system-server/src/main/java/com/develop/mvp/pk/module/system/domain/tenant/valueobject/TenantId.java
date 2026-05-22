package com.develop.mvp.pk.module.system.domain.tenant.valueobject;

// Skill: AggregateRoot_Tenant_Validation_Skill — 值对象 TenantId
// DDD 角色：租户聚合根标识
// 验收标准 AC04：final 字段，无 setter

import java.util.Objects;

public final class TenantId {
    private final Long value;

    private TenantId(Long value) {
        this.value = Objects.requireNonNull(value, "tenantId 不能为空");
    }

    public static TenantId of(Long value) { return new TenantId(value); }

    public Long value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return "TenantId{" + value + '}'; }
}
