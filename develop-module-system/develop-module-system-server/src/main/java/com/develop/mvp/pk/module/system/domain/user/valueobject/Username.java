package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 Username
// DDD 角色：不可变值对象，封装用户名
// 不变式 I01：用户名在同一个租户内不可重复（由领域服务 UserUniquenessChecker 保证）
// 验收标准 AC04：final 字段，无 setter

import com.develop.mvp.pk.framework.common.exception.ServiceException;

import java.util.Objects;

import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.USER_USERNAME_EXISTS;

/**
 * 用户名值对象 — 不可变，自校验非空
 */
public final class Username {

    private final String value;

    private Username(String value) {
        // R02: 用户名不能为空
        if (value == null || value.isBlank()) {
            throw new ServiceException(USER_USERNAME_EXISTS.getCode(), "用户名不能为空");
        }
        this.value = value.trim();
    }

    public static Username of(String value) {
        return new Username(value);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Username that)) return false;
        return value.equals(that.value);
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
