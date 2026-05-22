package com.develop.mvp.pk.module.system.domain.permission.repository;

// Skill: AggregateRoot_Role_Menu_Skill — 仓储接口 RoleMenuRepository
// DDD 角色：管理角色-菜单关联的仓储

import java.util.*;

public interface RoleMenuRepository {
    void assign(Long roleId, Set<Long> menuIds);
    Set<Long> findByRoleId(Long roleId);
    Set<Long> findByRoleIds(Collection<Long> roleIds);
    Set<Long> findByMenuId(Long menuId);
    void deleteByRoleId(Long roleId);
    void deleteByMenuId(Long menuId);
}
