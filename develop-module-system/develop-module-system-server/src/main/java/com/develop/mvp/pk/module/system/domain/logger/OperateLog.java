package com.develop.mvp.pk.module.system.domain.logger;
// DDD 角色：操作日志（值对象性质的聚合根 — 只追加写入）
import java.time.LocalDateTime;
public final class OperateLog {
    private final Long id, userId, duration; private final Integer userType, type; private final String module, name, requestUrl, requestMethod, javaMethod, javaMethodArgs, userIp, userAgent, resultMsg, resultData; private final Integer resultCode; private final LocalDateTime startTime, endTime;
    private OperateLog(Builder b) { this.id = b.id; this.userId = b.userId; this.userType = b.userType; this.type = b.type; this.module = b.module; this.name = b.name; this.requestUrl = b.requestUrl; this.requestMethod = b.requestMethod; this.javaMethod = b.javaMethod; this.javaMethodArgs = b.javaMethodArgs; this.userIp = b.userIp; this.userAgent = b.userAgent; this.duration = b.duration; this.resultCode = b.resultCode; this.resultMsg = b.resultMsg; this.resultData = b.resultData; this.startTime = b.startTime; this.endTime = b.endTime; }
    public static Builder builder() { return new Builder(); }
    public Long id() { return id; } public Long userId() { return userId; } public Integer userType() { return userType; } public Integer type() { return type; } public String module() { return module; } public String name() { return name; } public String requestUrl() { return requestUrl; } public String javaMethod() { return javaMethod; } public String userIp() { return userIp; } public Integer resultCode() { return resultCode; } public LocalDateTime startTime() { return startTime; }
    public static class Builder {
        private Long id, userId, duration; private Integer userType, type; private String module, name, requestUrl, requestMethod, javaMethod, javaMethodArgs, userIp, userAgent, resultMsg, resultData; private Integer resultCode; private LocalDateTime startTime, endTime;
        public Builder id(Long v) { id = v; return this; } public Builder userId(Long v) { userId = v; return this; }
        public Builder userType(Integer v) { userType = v; return this; } public Builder type(Integer v) { type = v; return this; }
        public Builder module(String v) { module = v; return this; } public Builder name(String v) { name = v; return this; }
        public Builder requestUrl(String v) { requestUrl = v; return this; } public Builder requestMethod(String v) { requestMethod = v; return this; }
        public Builder javaMethod(String v) { javaMethod = v; return this; } public Builder javaMethodArgs(String v) { javaMethodArgs = v; return this; }
        public Builder userIp(String v) { userIp = v; return this; } public Builder userAgent(String v) { userAgent = v; return this; }
        public Builder duration(Long v) { duration = v; return this; } public Builder resultCode(Integer v) { resultCode = v; return this; }
        public Builder resultMsg(String v) { resultMsg = v; return this; } public Builder resultData(String v) { resultData = v; return this; }
        public Builder startTime(LocalDateTime v) { startTime = v; return this; } public Builder endTime(LocalDateTime v) { endTime = v; return this; }
        public OperateLog build() { return new OperateLog(this); }
    }
}
