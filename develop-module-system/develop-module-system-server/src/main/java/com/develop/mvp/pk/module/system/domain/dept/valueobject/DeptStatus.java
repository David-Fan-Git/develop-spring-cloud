package com.develop.mvp.pk.module.system.domain.dept.valueobject;

import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import java.util.Objects;

public final class DeptStatus {
    public static final DeptStatus ENABLED = new DeptStatus(CommonStatusEnum.ENABLE.getStatus());
    public static final DeptStatus DISABLED = new DeptStatus(CommonStatusEnum.DISABLE.getStatus());
    private final Integer code;
    private DeptStatus(Integer code) { this.code = Objects.requireNonNull(code); }
    public static DeptStatus of(Integer code) {
        return CommonStatusEnum.ENABLE.getStatus().equals(code) ? ENABLED : DISABLED;
    }
    public boolean isEnabled() { return code.equals(CommonStatusEnum.ENABLE.getStatus()); }
    public Integer code() { return code; }
    public DeptStatus disable() { return DISABLED; }
    @Override public boolean equals(Object o) { return o instanceof DeptStatus s && code.equals(s.code); }
    @Override public int hashCode() { return Objects.hash(code); }
}
