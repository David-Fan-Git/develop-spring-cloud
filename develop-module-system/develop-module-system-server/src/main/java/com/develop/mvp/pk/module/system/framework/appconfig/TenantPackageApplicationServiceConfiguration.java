package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantUseCase;
import com.develop.mvp.pk.module.system.application.tenant.service.TenantPackageApplicationService;
import com.develop.mvp.pk.module.system.dal.mysql.tenant.TenantPackageMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class TenantPackageApplicationServiceConfiguration {

    @Bean
    public TenantPackageApplicationService tenantPackageApplicationService(
            TenantPackageMapper tenantPackageMapper,
            @Lazy TenantUseCase tenantUseCase) {
        return new TenantPackageApplicationService(tenantPackageMapper, tenantUseCase);
    }
}
