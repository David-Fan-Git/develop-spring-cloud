package com.develop.mvp.pk.module.system.domain.dept.valueobject;

import java.util.Objects;

public final class DeptId {
    private final Long value;
    private DeptId(Long value) { this.value = Objects.requireNonNull(value); }
    public static DeptId of(Long value) { return new DeptId(value); }
    public Long value() { return value; }
    @Override public boolean equals(Object o) { return o instanceof DeptId d && value.equals(d.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
