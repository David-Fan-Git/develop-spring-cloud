package com.develop.mvp.pk.module.system.domain.logger;

public final class OperateLog {

    private final Long id;
    private final String traceId;
    private final Long userId;
    private final Integer userType;
    private final String type;
    private final String subType;
    private final Long bizId;
    private final String action;
    private final String extra;
    private final String requestMethod;
    private final String requestUrl;
    private final String userIp;
    private final String userAgent;

    private OperateLog(Builder builder) {
        this.id = builder.id;
        this.traceId = builder.traceId;
        this.userId = builder.userId;
        this.userType = builder.userType;
        this.type = builder.type;
        this.subType = builder.subType;
        this.bizId = builder.bizId;
        this.action = builder.action;
        this.extra = builder.extra;
        this.requestMethod = builder.requestMethod;
        this.requestUrl = builder.requestUrl;
        this.userIp = builder.userIp;
        this.userAgent = builder.userAgent;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Long id() {
        return id;
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

    public String type() {
        return type;
    }

    public String subType() {
        return subType;
    }

    public Long bizId() {
        return bizId;
    }

    public String action() {
        return action;
    }

    public String extra() {
        return extra;
    }

    public String requestMethod() {
        return requestMethod;
    }

    public String requestUrl() {
        return requestUrl;
    }

    public String userIp() {
        return userIp;
    }

    public String userAgent() {
        return userAgent;
    }

    public static class Builder {
        private Long id;
        private String traceId;
        private Long userId;
        private Integer userType;
        private String type;
        private String subType;
        private Long bizId;
        private String action;
        private String extra;
        private String requestMethod;
        private String requestUrl;
        private String userIp;
        private String userAgent;

        public Builder id(Long id) {
            this.id = id;
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

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder subType(String subType) {
            this.subType = subType;
            return this;
        }

        public Builder bizId(Long bizId) {
            this.bizId = bizId;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder requestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
            return this;
        }

        public Builder requestUrl(String requestUrl) {
            this.requestUrl = requestUrl;
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

        public OperateLog build() {
            return new OperateLog(this);
        }
    }
}
