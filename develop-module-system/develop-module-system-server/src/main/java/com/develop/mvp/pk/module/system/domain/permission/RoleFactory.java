package com.develop.mvp.pk.module.system.domain.permission;

import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;

import java.util.Set;

public final class RoleFactory {
    private RoleFactory() {}

    public static Role create(Long id, String name, String code, Integer sort, Integer status,
                              Integer type, String remark, Long tenantId,
                              Integer dataScope, Set<Long> dataScopeDeptIds) {
        return new Role(id != null ? RoleId.of(id) : null, RoleName.of(name), RoleCode.of(code),
                sort, status != null ? RoleStatus.of(status) : RoleStatus.ENABLED,
                type != null ? RoleType.of(type) : RoleType.CUSTOM, remark, tenantId,
                DataScope.of(dataScope, dataScopeDeptIds), null);
    }

    public static Role reconstitute(Long id, String name, String code, Integer sort,
                                     Integer status, Integer type, String remark, Long tenantId,
                                     Integer dataScope, Set<Long> dataScopeDeptIds,
                                     Set<Long> menuIds) {
        return new Role(id != null ? RoleId.of(id) : null, RoleName.of(name), RoleCode.of(code),
                sort, RoleStatus.fromPersisted(status), RoleType.fromPersisted(type), remark, tenantId,
                DataScope.of(dataScope, dataScopeDeptIds), menuIds);
    }
}
