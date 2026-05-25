package com.develop.mvp.pk.module.system.application.tenant.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.application.tenant.service.TenantInfoHandler;
import com.develop.mvp.pk.module.system.application.tenant.service.TenantMenuHandler;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantPageQuery;

import java.util.List;
import java.util.Set;

/**
 * tenant aggregate use-case boundary for legacy entries and new adapters.
 */
public interface TenantUseCase {

    Long createTenant(Long id, String name, String contactName, String contactMobile,
                      Integer status, List<String> websites, Long packageId,
                      java.time.LocalDateTime expireTime, Integer accountCount,
                      String username, String password);

    void updateTenant(Long id, String name, String contactName, String contactMobile,
                      Integer status, List<String> websites, Long packageId,
                      java.time.LocalDateTime expireTime, Integer accountCount);

    void deleteTenant(Long id);

    void deleteTenantList(List<Long> ids);

    Tenant getTenant(Long id);

    Tenant getAndValidateTenant(Long id);

    void validTenant(Long id);

    Tenant getTenantByName(String name);

    Tenant getTenantByWebsite(String website);

    PageResult<Tenant> getTenantPage(TenantPageQuery query);

    List<Tenant> getTenantDomainListByStatus(Integer statusCode);

    List<Tenant> getTenantDomainListByPackageId(Long packageId);

    Long getTenantCountByPackageId(Long packageId);

    List<Long> getTenantIdList();

    void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds);

    void handleTenantInfo(TenantInfoHandler handler);

    void handleTenantMenu(TenantMenuHandler handler);
}
