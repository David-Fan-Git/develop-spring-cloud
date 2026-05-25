package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.dept.port.inbound.DeptUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.MenuUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.RoleUseCase;
import com.develop.mvp.pk.module.system.application.permission.service.PermissionApplicationService;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleMenuRepository;
import com.develop.mvp.pk.module.system.domain.permission.repository.UserRoleRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class PermissionApplicationServiceConfiguration {

    @Bean
    public PermissionApplicationService permissionApplicationService(
            UserRoleRepository userRoleRepository,
            RoleMenuRepository roleMenuRepository,
            @Lazy RoleUseCase roleUseCase,
            @Lazy MenuUseCase menuUseCase,
            DeptUseCase deptUseCase,
            @Lazy AdminUserUseCase adminUserUseCase) {
        return new PermissionApplicationService(
                userRoleRepository, roleMenuRepository,
                roleUseCase, menuUseCase,
                deptUseCase, adminUserUseCase);
    }
}
