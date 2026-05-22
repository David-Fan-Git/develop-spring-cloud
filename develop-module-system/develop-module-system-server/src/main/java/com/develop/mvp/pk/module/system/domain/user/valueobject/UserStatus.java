package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 UserStatus
// 不变式 I05：status 只能是 ENABLE 或 DISABLE
// 规则 R07/R09：禁用/启用状态变更 + 默认启用
// 验收标准 AC04：final 字段，无 setter

import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;

import java.util.Objects;

public final class UserStatus {

    public static final UserStatus ENABLED = new UserStatus(CommonStatusEnum.ENABLE.getStatus());
    public static final UserStatus DISABLED = new UserStatus(CommonStatusEnum.DISABLE.getStatus());

    private final Integer code;

    private UserStatus(Integer code) {
        this.code = Objects.requireNonNull(code, "状态不能为空");
    }

    public static UserStatus of(Integer code) {
        if (CommonStatusEnum.ENABLE.getStatus().equals(code)) {
            return ENABLED;
        }
        if (CommonStatusEnum.DISABLE.getStatus().equals(code)) {
            return DISABLED;
        }
        throw new IllegalArgumentException("无效的用户状态: " + code);
    }

    /** 规则 R07：禁用一个用户 */
    public UserStatus disable() {
        return DISABLED;
    }

    public UserStatus enable() {
        return ENABLED;
    }

    public boolean isEnabled() {
        return code.equals(CommonStatusEnum.ENABLE.getStatus());
    }

    public boolean isDisabled() {
        return code.equals(CommonStatusEnum.DISABLE.getStatus());
    }

    public Integer code() {
        return code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserStatus that)) return false;
        return code.equals(that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return isEnabled() ? "ENABLED" : "DISABLED";
    }
}
