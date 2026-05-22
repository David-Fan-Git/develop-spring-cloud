package com.develop.mvp.pk.module.system.domain.user;

// Skill: AggregateRoot_User_Validation_Skill — 聚合内部实体 UserPost
// DDD 角色：User 聚合内部实体，表示用户-岗位关联
// 不变式 I07：删除用户时，其所有岗位关联必须同时删除

import java.util.Objects;

public final class UserPost {

    private final Long userId;
    private final Long postId;

    public UserPost(Long userId, Long postId) {
        this.userId = Objects.requireNonNull(userId, "userId 不能为空");
        this.postId = Objects.requireNonNull(postId, "postId 不能为空");
    }

    public Long userId() { return userId; }
    public Long postId() { return postId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPost that)) return false;
        return userId.equals(that.userId) && postId.equals(that.postId);
    }

    @Override
    public int hashCode() { return Objects.hash(userId, postId); }
}
