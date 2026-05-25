package com.develop.mvp.pk.module.system.application.permission.port.inbound;

import com.develop.mvp.pk.module.system.controller.admin.permission.vo.menu.MenuListReqVO;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.MenuDO;

import java.util.Collection;
import java.util.List;

/**
 * Menu use-case boundary for RBAC entry adapters.
 */
public interface MenuUseCase {

    Long createMenu(MenuSaveVO createReqVO);

    void updateMenu(MenuSaveVO updateReqVO);

    void deleteMenu(Long id);

    void deleteMenuList(List<Long> ids);

    List<MenuDO> getMenuList();

    List<MenuDO> getMenuListByTenant(MenuListReqVO reqVO);

    List<MenuDO> filterDisableMenus(List<MenuDO> menuList);

    List<MenuDO> getMenuList(MenuListReqVO reqVO);

    List<Long> getMenuIdListByPermissionFromCache(String permission);

    MenuDO getMenu(Long id);

    List<MenuDO> getMenuList(Collection<Long> ids);
}
