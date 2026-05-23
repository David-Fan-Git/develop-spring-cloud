package com.develop.mvp.pk.module.member.domain.user;
// DDD 角色：会员用户聚合根
import java.util.Objects;
public final class MemberUser {
    private final Long id; private final String nickname; private String mobile, email, avatar;
    private Integer status; private Long tenantId;
    public MemberUser(Long id, String nickname) { this.id = id; this.nickname = Objects.requireNonNull(nickname); }
    public static MemberUser of(Long id, String nickname) { return new MemberUser(id, nickname); }
    public Long id() { return id; } public String nickname() { return nickname; }
    public String mobile() { return mobile; } public String email() { return email; } public String avatar() { return avatar; }
    public Integer status() { return status; } public Long tenantId() { return tenantId; }
    public MemberUser mobile(String v) { mobile = v; return this; } public MemberUser email(String v) { email = v; return this; }
    public MemberUser avatar(String v) { avatar = v; return this; } public MemberUser status(Integer v) { status = v; return this; }
    public MemberUser tenantId(Long v) { tenantId = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MemberUser u && Objects.equals(id, u.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
