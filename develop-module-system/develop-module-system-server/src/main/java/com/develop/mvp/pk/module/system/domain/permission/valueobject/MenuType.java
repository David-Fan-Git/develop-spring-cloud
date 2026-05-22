package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import com.develop.mvp.pk.module.system.enums.permission.MenuTypeEnum;
import java.util.Objects;

public final class MenuType {
    public static final MenuType DIR = new MenuType(MenuTypeEnum.DIR.getType());
    public static final MenuType MENU = new MenuType(MenuTypeEnum.MENU.getType());
    public static final MenuType BUTTON = new MenuType(MenuTypeEnum.BUTTON.getType());
    private final Integer code;
    private MenuType(Integer code) { this.code = Objects.requireNonNull(code); }
    public static MenuType of(Integer code) {
        if (MenuTypeEnum.DIR.getType().equals(code)) return DIR;
        if (MenuTypeEnum.MENU.getType().equals(code)) return MENU;
        if (MenuTypeEnum.BUTTON.getType().equals(code)) return BUTTON;
        throw new IllegalArgumentException("无效菜单类型: " + code);
    }
    public boolean isButton() { return code.equals(MenuTypeEnum.BUTTON.getType()); }
    public boolean isDirOrMenu() { return code.equals(MenuTypeEnum.DIR.getType()) || code.equals(MenuTypeEnum.MENU.getType()); }
    public Integer code() { return code; }
    @Override public boolean equals(Object o) { return o instanceof MenuType m && code.equals(m.code); }
    @Override public int hashCode() { return Objects.hash(code); }
}
