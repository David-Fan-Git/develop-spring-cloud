package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import java.util.Objects;

public final class MenuPermission {
    private final String value;
    private MenuPermission(String value) { this.value = value; }
    public static MenuPermission of(String value) { return new MenuPermission(value); }
    public static MenuPermission empty() { return new MenuPermission(null); }
    public String value() { return value; }
    public boolean isPresent() { return value != null && !value.isBlank(); }
    @Override public boolean equals(Object o) { return o instanceof MenuPermission m && Objects.equals(value, m.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
