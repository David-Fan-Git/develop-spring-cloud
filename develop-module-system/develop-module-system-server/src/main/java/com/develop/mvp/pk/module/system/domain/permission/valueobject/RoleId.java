package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import java.util.Objects;

public final class RoleId {
    private final Long value;
    private RoleId(Long value) { this.value = Objects.requireNonNull(value); }
    public static RoleId of(Long value) { return new RoleId(value); }
    public Long value() { return value; }
    @Override public boolean equals(Object o) { return o instanceof RoleId r && value.equals(r.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
