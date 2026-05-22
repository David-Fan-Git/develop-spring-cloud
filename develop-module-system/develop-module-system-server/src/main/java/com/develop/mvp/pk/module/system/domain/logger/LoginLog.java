package com.develop.mvp.pk.module.system.domain.logger;

// DDD 角色：登录日志（值对象性质的聚合根 — 只追加写入，不修改）
import java.time.LocalDateTime; import java.util.Objects;

public final class LoginLog {
    private final Long id; private final Long userId; private final Integer userType;
    private final String traceId, username, userIp, userAgent;
    private final Integer result; private final LocalDateTime loginTime;
    private LoginLog(Long id, Long userId, Integer userType) { this.id = id; this.userId = userId; this.userType = userType; this.traceId = null; this.username = null; this.userIp = null; this.userAgent = null; this.result = null; this.loginTime = null; }
    private LoginLog(Builder b) { this.id = b.id; this.userId = b.userId; this.userType = b.userType; this.traceId = b.traceId; this.username = b.username; this.userIp = b.userIp; this.userAgent = b.userAgent; this.result = b.result; this.loginTime = b.loginTime; }
    public static Builder builder() { return new Builder(); }
    public Long id() { return id; } public Long userId() { return userId; } public Integer userType() { return userType; }
    public String traceId() { return traceId; } public String username() { return username; }
    public String userIp() { return userIp; } public String userAgent() { return userAgent; }
    public Integer result() { return result; } public LocalDateTime loginTime() { return loginTime; }
    public static class Builder {
        private Long id, userId; private Integer userType; private String traceId, username, userIp, userAgent; private Integer result; private LocalDateTime loginTime;
        public Builder id(Long v) { id = v; return this; } public Builder userId(Long v) { userId = v; return this; }
        public Builder userType(Integer v) { userType = v; return this; } public Builder traceId(String v) { traceId = v; return this; }
        public Builder username(String v) { username = v; return this; } public Builder userIp(String v) { userIp = v; return this; }
        public Builder userAgent(String v) { userAgent = v; return this; } public Builder result(Integer v) { result = v; return this; }
        public Builder loginTime(LocalDateTime v) { loginTime = v; return this; }
        public LoginLog build() { return new LoginLog(this); }
    }
}
