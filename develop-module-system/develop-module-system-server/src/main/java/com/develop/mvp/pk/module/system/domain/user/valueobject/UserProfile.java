package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 UserProfile
// DDD 角色：不可变值对象（整体替换），封装用户个人资料
// 验收标准 AC05：提供 withXxx() 方法而非 setter

import com.develop.mvp.pk.module.system.enums.common.SexEnum;

import java.util.Objects;

public final class UserProfile {

    private final String nickname;
    private final String avatar;
    private final Integer sex;    // SexEnum code
    private final String remark;

    private UserProfile(String nickname, String avatar, Integer sex, String remark) {
        this.nickname = nickname;
        this.avatar = avatar;
        this.sex = sex;
        this.remark = remark;
    }

    public static UserProfile of(String nickname, String avatar, Integer sex, String remark) {
        return new UserProfile(nickname, avatar, sex, remark);
    }

    // withXxx 方法 — 整体替换语义
    public UserProfile withNickname(String nickname) {
        return new UserProfile(nickname, this.avatar, this.sex, this.remark);
    }

    public UserProfile withAvatar(String avatar) {
        return new UserProfile(this.nickname, avatar, this.sex, this.remark);
    }

    public UserProfile withSex(Integer sex) {
        return new UserProfile(this.nickname, this.avatar, sex, this.remark);
    }

    public UserProfile withRemark(String remark) {
        return new UserProfile(this.nickname, this.avatar, this.sex, remark);
    }

    public String nickname() { return nickname; }
    public String avatar() { return avatar; }
    public Integer sex() { return sex; }
    public String remark() { return remark; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserProfile that)) return false;
        return Objects.equals(nickname, that.nickname)
            && Objects.equals(avatar, that.avatar)
            && Objects.equals(sex, that.sex)
            && Objects.equals(remark, that.remark);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nickname, avatar, sex, remark);
    }
}
