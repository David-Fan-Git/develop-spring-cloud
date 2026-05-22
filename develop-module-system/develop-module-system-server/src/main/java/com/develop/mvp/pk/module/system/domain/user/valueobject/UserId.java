package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 聚合根标识 UserId
// DDD 角色：不可变值对象，作为 User 聚合根的唯一标识

import java.util.Objects;

public final class UserId {

    private final Long value;

    private UserId(Long value) {
        this.value = Objects.requireNonNull(value, "用户ID不能为空");
    }

    public static UserId of(Long value) {
        return new UserId(value);
    }

    public Long value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return "UserId{" + value + '}'; }
}
