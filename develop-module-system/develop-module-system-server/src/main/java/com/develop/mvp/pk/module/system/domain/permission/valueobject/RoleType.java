package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import com.develop.mvp.pk.module.system.enums.permission.RoleTypeEnum;
import java.util.Objects;

public final class RoleType {
    public static final RoleType SYSTEM = new RoleType(RoleTypeEnum.SYSTEM.getType());
    public static final RoleType CUSTOM = new RoleType(RoleTypeEnum.CUSTOM.getType());
    private final Integer code;
    private RoleType(Integer code) { this.code = Objects.requireNonNull(code); }
    public static RoleType of(Integer code) {
        if (RoleTypeEnum.SYSTEM.getType().equals(code)) return SYSTEM;
        if (RoleTypeEnum.CUSTOM.getType().equals(code)) return CUSTOM;
        throw new IllegalArgumentException("无效的角色类型: " + code);
    }
    public static RoleType fromPersisted(Integer code) {
        return RoleTypeEnum.SYSTEM.getType().equals(code) ? SYSTEM : CUSTOM;
    }
    public boolean isSystem() { return code.equals(RoleTypeEnum.SYSTEM.getType()); }
    public Integer code() { return code; }
    @Override public boolean equals(Object o) { return o instanceof RoleType r && code.equals(r.code); }
    @Override public int hashCode() { return Objects.hash(code); }
}
