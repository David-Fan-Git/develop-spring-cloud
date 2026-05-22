package com.develop.mvp.pk.module.system.domain.user.valueobject;

// Skill: AggregateRoot_User_Validation_Skill — 值对象 LoginRecord
// DDD 角色：不可变值对象，记录用户登录的 IP 和时间
// 验收标准 AC16：User 聚合提供 recordLogin(LoginRecord) 方法
// 验收标准 AC04：final 字段，无 setter

import java.time.LocalDateTime;
import java.util.Objects;

public final class LoginRecord {

    private final String loginIp;
    private final LocalDateTime loginDate;

    private LoginRecord(String loginIp, LocalDateTime loginDate) {
        this.loginIp = loginIp;
        this.loginDate = Objects.requireNonNull(loginDate, "登录时间不能为空");
    }

    public static LoginRecord of(String loginIp) {
        return new LoginRecord(loginIp, LocalDateTime.now());
    }

    public static LoginRecord of(String loginIp, LocalDateTime loginDate) {
        return new LoginRecord(loginIp, loginDate);
    }

    public String loginIp() { return loginIp; }
    public LocalDateTime loginDate() { return loginDate; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoginRecord that)) return false;
        return Objects.equals(loginIp, that.loginIp) && loginDate.equals(that.loginDate);
    }

    @Override
    public int hashCode() { return Objects.hash(loginIp, loginDate); }

    @Override
    public String toString() {
        return "LoginRecord{ip='" + loginIp + "', date=" + loginDate + '}';
    }
}
