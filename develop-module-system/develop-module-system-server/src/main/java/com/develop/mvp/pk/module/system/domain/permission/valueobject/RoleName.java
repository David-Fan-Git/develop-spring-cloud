package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import java.util.Objects;

public final class RoleName {
    private final String value;
    private RoleName(String value) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException("角色名称不能为空");
        this.value = value;
    }
    public static RoleName of(String value) { return new RoleName(value); }
    public String value() { return value; }
    @Override public boolean equals(Object o) { return o instanceof RoleName r && value.equals(r.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
