package com.develop.mvp.pk.module.system.domain.tenant;

// Skill: AggregateRoot_Tenant_Validation_Skill — 工厂 TenantFactory
// DDD 角色：工厂，负责创建和重建 Tenant 聚合（与 Tenant 同包，可访问包级构造器）
// 规则 R01：创建时默认状态为 ENABLED

import com.develop.mvp.pk.module.system.domain.tenant.valueobject.*;

import java.time.LocalDateTime;
import java.util.List;

public final class TenantFactory {

    private TenantFactory() {}

    /** 创建新租户（规则 R01：默认状态 ENABLED） */
    public static Tenant create(Long id, String name, Long contactUserId, String contactName,
                                 String contactMobile, List<String> websites,
                                 Long packageId, LocalDateTime expireTime, Integer accountCount) {
        return new Tenant(
                TenantId.of(id),
                TenantName.of(name),
                contactUserId,
                contactName,
                contactMobile,
                TenantStatus.ENABLED, // 规则 R01
                websites,
                TenantPackageRef.of(packageId),
                TenantExpireTime.of(expireTime),
                accountCount
        );
    }

    /** 从持久化数据重建 Tenant 聚合（供仓储实现调用） */
    public static Tenant reconstitute(Long id, String name, Long contactUserId,
                                       String contactName, String contactMobile,
                                       Integer statusCode, List<String> websites,
                                       Long packageId, LocalDateTime expireTime,
                                       Integer accountCount) {
        return new Tenant(
                TenantId.of(id),
                TenantName.of(name),
                contactUserId,
                contactName,
                contactMobile,
                TenantStatus.of(statusCode),
                websites,
                TenantPackageRef.of(packageId),
                TenantExpireTime.of(expireTime),
                accountCount
        );
    }
}
