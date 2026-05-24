package com.develop.mvp.pk.module.system.application.permission.port.inbound;

import com.develop.mvp.pk.module.system.domain.permission.Role;

import java.util.Set;

public interface RoleUseCase {

    Role createRole(String name, String code, Integer sort, Integer status, String remark, Integer type);

    Role updateRole(Long id, String name, String code, Integer sort, Integer status, String remark);

    Role updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds);

    Role deleteRole(Long id);

    Role getRole(Long id);

    void validateRoleDuplicate(String name, String code, Long id);

    Role validateRoleForUpdate(Long id);
}
