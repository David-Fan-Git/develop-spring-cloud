package com.develop.mvp.pk.module.system.domain.oauth2;
// DDD 角色：OAuth2 访问令牌聚合根
import java.time.LocalDateTime; import java.util.*;
public final class OAuth2AccessToken {
    private final Long id; private final String accessToken, refreshToken, clientId;
    private Long userId; private Integer userType; private List<String> scopes; private LocalDateTime expiresTime;
    private OAuth2AccessToken(Long id, String at, String rt, String cid) { this.id = Objects.requireNonNull(id); this.accessToken = Objects.requireNonNull(at); this.refreshToken = rt; this.clientId = Objects.requireNonNull(cid); }
    public static OAuth2AccessToken of(Long id, String at, String rt, String cid) { return new OAuth2AccessToken(id, at, rt, cid); }
    public Long id() { return id; } public String accessToken() { return accessToken; } public String refreshToken() { return refreshToken; } public String clientId() { return clientId; }
    public Long userId() { return userId; } public Integer userType() { return userType; } public List<String> scopes() { return scopes; } public LocalDateTime expiresTime() { return expiresTime; }
    public OAuth2AccessToken userId(Long v) { userId = v; return this; } public OAuth2AccessToken userType(Integer v) { userType = v; return this; }
    public OAuth2AccessToken scopes(List<String> v) { scopes = v; return this; } public OAuth2AccessToken expiresTime(LocalDateTime v) { expiresTime = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof OAuth2AccessToken t && id.equals(t.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
