package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.framework.tenant.config.TenantProperties;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.MenuUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.RoleUseCase;
import com.develop.mvp.pk.module.system.application.tenant.service.TenantApplicationService;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantPackageUseCase;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantRepository;
import com.develop.mvp.pk.module.system.domain.tenant.service.TenantUniquenessChecker;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TenantApplicationServiceConfiguration {

    @Bean
    public TenantApplicationService tenantApplicationService(
            TenantRepository tenantRepository,
            TenantUniquenessChecker uniquenessChecker,
            DomainEventPublisher eventPublisher,
            TenantPackageUseCase tenantPackageService,
            AdminUserUseCase adminUserService,
            RoleUseCase roleService,
            PermissionUseCase permissionService,
            MenuUseCase menuService,
            @Autowired(required = false) TenantProperties tenantProperties) {
        TenantApplicationService svc = new TenantApplicationService(
                tenantRepository, uniquenessChecker, eventPublisher,
                tenantPackageService, adminUserService, roleService,
                permissionService, menuService);
        if (tenantProperties != null) {
            svc.setTenantProperties(tenantProperties);
        }
        return svc;
    }
}
