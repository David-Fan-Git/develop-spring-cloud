package com.develop.mvp.pk.module.system.application.permission.port.inbound;

import java.util.Collection;
import java.util.Set;

/**
 * Permission assignment use-case boundary for RBAC entry adapters.
 */
public interface PermissionUseCase {

    void assignRoleMenu(Long roleId, Set<Long> menuIds);

    void processRoleDeleted(Long roleId);

    void processMenuDeleted(Long menuId);

    Set<Long> getRoleMenuIds(Long roleId);

    Set<Long> getRoleMenuIds(Collection<Long> roleIds);

    Set<Long> getMenuRoleIds(Long menuId);

    void assignUserRole(Long userId, Set<Long> roleIds);

    void processUserDeleted(Long userId);

    Set<Long> getUserRoleIds(Long userId);

    Set<Long> getUserIdsByRoleIds(Collection<Long> roleIds);
}
