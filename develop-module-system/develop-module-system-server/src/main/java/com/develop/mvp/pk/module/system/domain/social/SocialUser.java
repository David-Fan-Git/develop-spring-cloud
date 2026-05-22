package com.develop.mvp.pk.module.system.domain.social;

// DDD 角色：社交用户聚合根 — 封装第三方社交平台用户绑定
import java.util.Objects;

public final class SocialUser {
    private final Long id; private final Integer type; private final String openid;
    private Long userId; private Integer userType; private String token, rawUserInfo, nickname, avatar;
    private SocialUser(Long id, Integer type, String openid) { this.id = Objects.requireNonNull(id); this.type = Objects.requireNonNull(type); this.openid = Objects.requireNonNull(openid); }
    public static SocialUser of(Long id, Integer type, String openid) { return new SocialUser(id, type, openid); }
    public Long id() { return id; } public Integer type() { return type; } public String openid() { return openid; }
    public Long userId() { return userId; } public Integer userType() { return userType; }
    public String token() { return token; } public String rawUserInfo() { return rawUserInfo; }
    public String nickname() { return nickname; } public String avatar() { return avatar; }
    public SocialUser userId(Long v) { this.userId = v; return this; }
    public SocialUser userType(Integer v) { this.userType = v; return this; }
    public SocialUser token(String v) { this.token = v; return this; }
    public SocialUser rawUserInfo(String v) { this.rawUserInfo = v; return this; }
    public SocialUser nickname(String v) { this.nickname = v; return this; }
    public SocialUser avatar(String v) { this.avatar = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof SocialUser s && id.equals(s.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
