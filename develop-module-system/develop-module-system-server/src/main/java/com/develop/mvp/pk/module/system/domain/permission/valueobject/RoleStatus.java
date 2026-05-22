package com.develop.mvp.pk.module.system.domain.permission.valueobject;

import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import java.util.Objects;

public final class RoleStatus {
    public static final RoleStatus ENABLED = new RoleStatus(CommonStatusEnum.ENABLE.getStatus());
    public static final RoleStatus DISABLED = new RoleStatus(CommonStatusEnum.DISABLE.getStatus());
    private final Integer code;
    private RoleStatus(Integer code) { this.code = Objects.requireNonNull(code); }
    public static RoleStatus of(Integer code) {
        if (CommonStatusEnum.ENABLE.getStatus().equals(code)) return ENABLED;
        if (CommonStatusEnum.DISABLE.getStatus().equals(code)) return DISABLED;
        throw new IllegalArgumentException("无效状态: " + code);
    }
    public boolean isEnabled() { return code.equals(CommonStatusEnum.ENABLE.getStatus()); }
    public boolean isDisabled() { return code.equals(CommonStatusEnum.DISABLE.getStatus()); }
    public Integer code() { return code; }
    @Override public boolean equals(Object o) { return o instanceof RoleStatus r && code.equals(r.code); }
    @Override public int hashCode() { return Objects.hash(code); }
}
