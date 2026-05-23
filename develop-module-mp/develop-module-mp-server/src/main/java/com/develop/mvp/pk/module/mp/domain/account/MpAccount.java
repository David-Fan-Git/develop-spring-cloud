package com.develop.mvp.pk.module.mp.domain.account;
import java.util.Objects;
public final class MpAccount { private final Long id; private final String name; private String appId, appSecret, token; private Integer type, status;
    public MpAccount(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static MpAccount of(Long id, String name) { return new MpAccount(id, name); }
    public Long id() { return id; } public String name() { return name; } public String appId() { return appId; }
    public String appSecret() { return appSecret; } public String token() { return token; }
    public Integer type() { return type; } public Integer status() { return status; }
    public MpAccount appId(String v) { appId = v; return this; } public MpAccount appSecret(String v) { appSecret = v; return this; }
    public MpAccount token(String v) { token = v; return this; } public MpAccount type(Integer v) { type = v; return this; }
    public MpAccount status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MpAccount a && id.equals(a.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
