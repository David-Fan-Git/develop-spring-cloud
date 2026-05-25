package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.permission.service.RoleApplicationService;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMapper;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class RoleApplicationServiceConfiguration {

    @Bean
    public RoleApplicationService roleApplicationService(
            RoleRepository roleRepository,
            RoleMapper roleMapper,
            @Lazy PermissionUseCase permissionUseCase) {
        return new RoleApplicationService(roleRepository, roleMapper, permissionUseCase);
    }
}
