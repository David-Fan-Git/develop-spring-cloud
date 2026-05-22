package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 Email
// 不变式 I02：邮箱在同一租户内不可重复（UserUniquenessChecker 保证）
// 验收标准 AC04：final 字段，无 setter

import com.develop.mvp.pk.framework.common.exception.ServiceException;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.USER_EMAIL_EXISTS;

public final class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    private Email(String value) {
        if (value != null && !value.isBlank() && !EMAIL_PATTERN.matcher(value).matches()) {
            throw new ServiceException(USER_EMAIL_EXISTS.getCode(), "邮箱格式不正确");
        }
        this.value = (value == null || value.isBlank()) ? null : value.trim().toLowerCase();
    }

    public static Email of(String value) {
        return new Email(value);
    }

    public static Email empty() {
        return new Email(null);
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
        if (!(o instanceof Email that)) return false;
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
