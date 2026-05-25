package com.develop.mvp.pk.module.system.domain.logger;

public final class LoginLog {

    private final Long id;
    private final Integer logType;
    private final String traceId;
    private final Long userId;
    private final Integer userType;
    private final String username;
    private final Integer result;
    private final String userIp;
    private final String userAgent;

    private LoginLog(Builder builder) {
        this.id = builder.id;
        this.logType = builder.logType;
        this.traceId = builder.traceId;
        this.userId = builder.userId;
        this.userType = builder.userType;
        this.username = builder.username;
        this.result = builder.result;
        this.userIp = builder.userIp;
        this.userAgent = builder.userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long id() {
        return id;
    }

    public Integer logType() {
        return logType;
    }

    public String traceId() {
        return traceId;
    }

    public Long userId() {
        return userId;
    }

    public Integer userType() {
        return userType;
    }

    public String username() {
        return username;
    }

    public Integer result() {
        return result;
    }

    public String userIp() {
        return userIp;
    }

    public String userAgent() {
        return userAgent;
    }

    public static class Builder {
        private Long id;
        private Integer logType;
        private String traceId;
        private Long userId;
        private Integer userType;
        private String username;
        private Integer result;
        private String userIp;
        private String userAgent;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder logType(Integer logType) {
            this.logType = logType;
            return this;
        }

        public Builder traceId(String traceId) {
            this.traceId = traceId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder userType(Integer userType) {
            this.userType = userType;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder result(Integer result) {
            this.result = result;
            return this;
        }

        public Builder userIp(String userIp) {
            this.userIp = userIp;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public LoginLog build() {
            return new LoginLog(this);
        }
    }
}
