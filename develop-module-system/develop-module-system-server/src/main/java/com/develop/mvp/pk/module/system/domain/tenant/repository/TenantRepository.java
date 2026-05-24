package com.develop.mvp.pk.module.system.domain.tenant.repository;

// Skill: AggregateRoot_Tenant_Validation_Skill — 仓储接口 TenantRepository
// DDD 角色：领域层定义的仓储接口，不依赖任何基础设施
// 验收标准 AC05：不 import MyBatis 类

import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.TenantId;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.TenantName;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.TenantPackageRef;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.TenantStatus;
import com.develop.mvp.pk.framework.common.pojo.PageResult;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TenantRepository {
    Tenant create(String name, Long contactUserId, String contactName, String contactMobile,
                  TenantStatus status, java.util.List<String> websites, Long packageId,
                  java.time.LocalDateTime expireTime, Integer accountCount);
    Tenant save(Tenant tenant);
    void delete(TenantId id);
    Tenant findById(TenantId id);
    Optional<Tenant> findByName(TenantName name);
    List<Tenant> findByWebsite(String website);
    List<Tenant> findByPackageId(TenantPackageRef packageRef);
    List<Tenant> findByStatus(TenantStatus status);
    PageResult<Tenant> findPage(TenantPageQuery query);
    long countByPackageId(TenantPackageRef packageRef);
    List<Tenant> findByIds(Collection<TenantId> ids);
    List<Tenant> findAll();
    boolean existsByName(TenantName name);
}
