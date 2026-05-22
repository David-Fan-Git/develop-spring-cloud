package com.develop.mvp.pk.module.system.domain.tenant.valueobject;

// Skill: AggregateRoot_Tenant_Validation_Skill — 值对象 TenantPackageRef
// DDD 角色：外部聚合（TenantPackage）的 ID 引用
// 不变式 I04：系统租户使用 packageId=0 标识
// 验收标准 AC04：final 字段，无 setter

import java.util.Objects;

public final class TenantPackageRef {
    public static final Long SYSTEM_PACKAGE_ID = 0L;

    private final Long packageId;

    private TenantPackageRef(Long packageId) {
        this.packageId = Objects.requireNonNull(packageId, "packageId 不能为空");
    }

    public static TenantPackageRef of(Long packageId) { return new TenantPackageRef(packageId); }

    public Long packageId() { return packageId; }

    /** 规则 R04：判断是否为系统租户 */
    public boolean isSystem() { return SYSTEM_PACKAGE_ID.equals(packageId); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantPackageRef that)) return false;
        return packageId.equals(that.packageId);
    }

    @Override
    public int hashCode() { return Objects.hash(packageId); }

    @Override
    public String toString() { return "TenantPackageRef{" + packageId + '}'; }
}
