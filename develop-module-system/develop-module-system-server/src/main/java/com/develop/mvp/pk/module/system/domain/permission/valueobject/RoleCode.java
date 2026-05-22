package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import java.util.Objects;

public final class RoleCode {
    private final String value;
    private RoleCode(String value) { this.value = Objects.requireNonNull(value, "角色编码不能为空"); }
    public static RoleCode of(String value) { return new RoleCode(value); }
    public String value() { return value; }
    public boolean isSuperAdmin() { return RoleCodeEnum.isSuperAdmin(value); }
    @Override public boolean equals(Object o) { return o instanceof RoleCode r && value.equals(r.value); }
    @Override public int hashCode() { return Objects.hash(value); }
    @Override public String toString() { return value; }
}
