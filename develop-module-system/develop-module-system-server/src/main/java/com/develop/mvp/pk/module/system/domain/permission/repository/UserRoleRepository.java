package com.develop.mvp.pk.module.system.domain.permission.repository;

// Skill: AggregateRoot_Role_Menu_Skill — 仓储接口 UserRoleRepository
// DDD 角色：管理用户-角色关联的仓储

import java.util.*;

public interface UserRoleRepository {
    void assign(Long userId, Set<Long> roleIds);
    Set<Long> findByUserId(Long userId);
    Set<Long> findByRoleIds(Collection<Long> roleIds);
    void deleteByUserId(Long userId);
    void deleteByRoleId(Long roleId);
}
