package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import java.util.Objects;

public final class MenuId {
    public static final Long ROOT_ID = 0L;
    private final Long value;
    private MenuId(Long value) { this.value = Objects.requireNonNull(value); }
    public static MenuId of(Long value) { return new MenuId(value); }
    public static MenuId root() { return new MenuId(ROOT_ID); }
    public Long value() { return value; }
    public boolean isRoot() { return ROOT_ID.equals(value); }
    @Override public boolean equals(Object o) { return o instanceof MenuId m && value.equals(m.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
