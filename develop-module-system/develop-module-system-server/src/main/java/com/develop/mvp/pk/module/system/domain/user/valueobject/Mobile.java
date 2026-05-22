package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 Mobile
// 不变式 I03：手机号在同一租户内不可重复（UserUniquenessChecker 保证）
// 验收标准 AC04：final 字段，无 setter

import com.develop.mvp.pk.framework.common.exception.ServiceException;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.USER_MOBILE_EXISTS;

public final class Mobile {

    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private final String value;

    private Mobile(String value) {
        if (value != null && !value.isBlank() && !MOBILE_PATTERN.matcher(value).matches()) {
            throw new ServiceException(USER_MOBILE_EXISTS.getCode(), "手机号格式不正确");
        }
        this.value = (value == null || value.isBlank()) ? null : value.trim();
    }

    public static Mobile of(String value) {
        return new Mobile(value);
    }

    public static Mobile empty() {
        return new Mobile(null);
    }

    public String value() {
        return value;
    }

    public boolean isPresent() {
        return value != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mobile that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
