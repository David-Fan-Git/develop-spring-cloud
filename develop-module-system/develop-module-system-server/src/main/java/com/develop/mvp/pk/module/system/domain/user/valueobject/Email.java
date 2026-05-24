package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 Email
// 不变式 I02：邮箱在同一租户内不可重复（UserUniquenessChecker 保证）
// 验收标准 AC04：final 字段，无 setter

import java.util.Objects;

public final class Email {

    private final String value;

    private Email(String value) {
        this.value = (value == null || value.isBlank()) ? null : value.trim();
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
