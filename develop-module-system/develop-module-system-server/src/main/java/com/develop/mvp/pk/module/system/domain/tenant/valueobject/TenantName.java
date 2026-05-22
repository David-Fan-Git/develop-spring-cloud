package com.develop.mvp.pk.module.system.domain.tenant.valueobject;

// Skill: AggregateRoot_Tenant_Validation_Skill — 值对象 TenantName
// 不变式 I01：租户名称在全局不可重复（唯一性由 TenantUniquenessChecker 保证）
// 规则 R02：创建/修改时校验租户名称唯一性
// 验收标准 AC04：final 字段，无 setter

import java.util.Objects;

public final class TenantName {
    private final String value;

    private TenantName(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("租户名不能为空");
        }
        this.value = value;
    }

    public static TenantName of(String value) { return new TenantName(value); }

    public String value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return value; }
}
