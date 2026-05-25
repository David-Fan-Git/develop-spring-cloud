package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.infra.api.config.ConfigApi;
import com.develop.mvp.pk.module.system.application.dept.port.inbound.DeptUseCase;
import com.develop.mvp.pk.module.system.application.oauth2.port.inbound.OAuth2UseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantUseCase;
import com.develop.mvp.pk.module.system.application.user.port.inbound.UserUseCase;
import com.develop.mvp.pk.module.system.application.user.service.AdminUserApplicationService;
import com.develop.mvp.pk.module.system.dal.mysql.dept.UserPostMapper;
import com.develop.mvp.pk.module.system.dal.mysql.user.AdminUserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserApplicationServiceConfiguration {

    @Bean
    public AdminUserApplicationService adminUserApplicationService(
            AdminUserMapper userMapper,
            UserUseCase userApplicationService,
            DeptUseCase deptUseCase,
            DeptUseCase postUseCase,
            @Lazy PermissionUseCase permissionService,
            PasswordEncoder passwordEncoder,
            @Lazy TenantUseCase tenantService,
            @Lazy OAuth2UseCase oauth2TokenService,
            UserPostMapper userPostMapper,
            ConfigApi configApi) {
        return new AdminUserApplicationService(userMapper, userApplicationService,
                deptUseCase, postUseCase, permissionService, passwordEncoder,
                tenantService, oauth2TokenService, userPostMapper, configApi);
    }
}
