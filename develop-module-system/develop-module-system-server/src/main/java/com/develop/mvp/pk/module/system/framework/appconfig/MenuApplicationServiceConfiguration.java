package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.permission.service.MenuApplicationService;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantUseCase;
import com.develop.mvp.pk.module.system.dal.mysql.permission.MenuMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class MenuApplicationServiceConfiguration {

    @Bean
    public MenuApplicationService menuApplicationService(
            MenuMapper menuMapper,
            @Lazy PermissionUseCase permissionUseCase,
            @Lazy TenantUseCase tenantUseCase) {
        return new MenuApplicationService(menuMapper, permissionUseCase, tenantUseCase);
    }
}
