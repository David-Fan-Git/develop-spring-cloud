package com.develop.mvp.pk.module.system.domain.tenant.valueobject;

// Skill: AggregateRoot_Tenant_Validation_Skill — 值对象 TenantExpireTime
// 不变式 I05：过期时间不能为空，过期租户不可被校验通过
// 验收标准 AC04：final 字段，无 setter

import java.time.LocalDateTime;
import java.util.Objects;

public final class TenantExpireTime {
    private final LocalDateTime value;

    private TenantExpireTime(LocalDateTime value) {
        this.value = Objects.requireNonNull(value, "过期时间不能为空");
    }

    public static TenantExpireTime of(LocalDateTime value) { return new TenantExpireTime(value); }

    public LocalDateTime value() { return value; }

    /** 不变式 I05：租户是否已过期 */
    public boolean isExpired() {
        return value.isBefore(LocalDateTime.now());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantExpireTime that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value.toString(); }
}
