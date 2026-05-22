package com.develop.mvp.pk.module.system.domain.permission;

import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;

import java.util.Set;

public final class RoleFactory {
    private RoleFactory() {}

    public static Role create(Long id, String name, String code, Integer sort, String remark,
                               Long tenantId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        return new Role(RoleId.of(id), RoleName.of(name), RoleCode.of(code),
                sort, RoleStatus.ENABLED, RoleType.CUSTOM, remark, tenantId,
                DataScope.of(dataScope, dataScopeDeptIds), null);
    }

    public static Role reconstitute(Long id, String name, String code, Integer sort,
                                     Integer status, Integer type, String remark, Long tenantId,
                                     Integer dataScope, Set<Long> dataScopeDeptIds,
                                     Set<Long> menuIds) {
        return new Role(RoleId.of(id), RoleName.of(name), RoleCode.of(code),
                sort, RoleStatus.of(status), RoleType.of(type), remark, tenantId,
                DataScope.of(dataScope, dataScopeDeptIds), menuIds);
    }
}
