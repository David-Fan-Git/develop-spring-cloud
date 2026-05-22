package com.develop.mvp.pk.module.system.domain.permission;

import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;

public final class MenuFactory {
    private MenuFactory() {}

    public static Menu create(Long id, String name, String permission, Integer type, Integer sort,
                               Long parentId, String path, String icon, String component,
                               String componentName, Integer status, Boolean visible,
                               Boolean keepAlive, Boolean alwaysShow) {
        return new Menu(MenuId.of(id), MenuName.of(name), MenuPermission.of(permission),
                MenuType.of(type), sort, MenuId.of(parentId != null ? parentId : MenuId.ROOT_ID),
                path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
    }

    public static Menu reconstitute(Long id, String name, String permission, Integer type,
                                     Integer sort, Long parentId, String path, String icon,
                                     String component, String componentName, Integer status,
                                     Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        return new Menu(MenuId.of(id), MenuName.of(name),
                permission != null ? MenuPermission.of(permission) : MenuPermission.empty(),
                MenuType.of(type), sort,
                MenuId.of(parentId != null ? parentId : MenuId.ROOT_ID),
                path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
    }
}
