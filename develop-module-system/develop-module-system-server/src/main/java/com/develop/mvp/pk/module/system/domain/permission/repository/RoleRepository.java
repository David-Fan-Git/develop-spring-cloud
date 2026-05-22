package com.develop.mvp.pk.module.system.domain.permission.repository;

// Skill: AggregateRoot_Role_Menu_Skill — 仓储接口 RoleRepository
// 验收标准 AC05：领域层接口，不 import MyBatis

import com.develop.mvp.pk.module.system.domain.permission.Role;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;
import com.develop.mvp.pk.framework.common.pojo.PageResult;

import java.time.LocalDateTime;
import java.util.*;

public interface RoleRepository {
    Role save(Role role);
    void delete(RoleId id);
    Role findById(RoleId id);
    List<Role> findByIds(Collection<RoleId> ids);
    List<Role> findByStatus(Collection<Integer> statuses);
    List<Role> findAll();
    PageResult<Role> findPage(String name, String code, Integer status,
                              LocalDateTime[] createTime, Integer pageNo, Integer pageSize);
    Optional<Role> findByName(String name);
    Optional<Role> findByCode(String code);
}
