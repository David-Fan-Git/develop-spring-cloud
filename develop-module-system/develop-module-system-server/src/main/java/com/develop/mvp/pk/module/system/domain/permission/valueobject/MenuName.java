package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import java.util.Objects;

public final class MenuName {
    private final String value;
    private MenuName(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("菜单名称不能为空");
        this.value = value;
    }
    public static MenuName of(String value) { return new MenuName(value); }
    public String value() { return value; }
    @Override public boolean equals(Object o) { return o instanceof MenuName m && value.equals(m.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
