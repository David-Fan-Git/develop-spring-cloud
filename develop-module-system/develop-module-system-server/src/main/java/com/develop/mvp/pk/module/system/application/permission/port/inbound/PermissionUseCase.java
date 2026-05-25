package com.develop.mvp.pk.module.system.application.permission.port.inbound;

import com.develop.mvp.pk.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;

import java.util.Collection;
import java.util.List;
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

    // ---- Additional methods from PermissionApplicationService ----

    boolean hasAnyPermissions(Long userId, String... permissions);

    boolean hasAnyRoles(Long userId, String... roles);

    Set<Long> getRoleMenuListByRoleId(Long roleId);

    Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds);

    Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId);

    Set<Long> getUserRoleIdListByUserId(Long userId);

    Set<Long> getUserRoleIdListByUserIdFromCache(Long userId);

    Set<Long> getUserRoleIdListByRoleId(Collection<Long> roleIds);

    void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds);

    DeptDataPermissionRespDTO getDeptDataPermission(Long userId);

    List<RoleDO> getEnableUserRoleListByUserIdFromCache(Long userId);
}
