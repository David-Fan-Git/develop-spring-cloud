package com.develop.mvp.pk.module.system.domain.permission.repository;

// Skill: AggregateRoot_Role_Menu_Skill — 仓储接口 MenuRepository

import com.develop.mvp.pk.module.system.domain.permission.Menu;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.MenuId;

import java.util.*;

public interface MenuRepository {
    Menu save(Menu menu);
    void delete(MenuId id);
    Menu findById(MenuId id);
    List<Menu> findByIds(Collection<MenuId> ids);
    List<Menu> findAll();
    List<Menu> findByPermission(String permission);
    Optional<Menu> findByParentIdAndName(Long parentId, String name);
    Optional<Menu> findByComponentName(String componentName);
    long countByParentId(MenuId parentId);
}
