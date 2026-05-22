package com.develop.mvp.pk.module.system.domain.mail;

// DDD 角色：邮箱账号聚合根 — 封装邮箱配置属性
import java.util.Objects;

public final class MailAccount {
    private final Long id;
    private final String mail;
    private String username, password, host, port, nickname;
    private Boolean sslEnable, starttlsEnable;
    private MailAccount(Long id, String mail) { this.id = Objects.requireNonNull(id); this.mail = Objects.requireNonNull(mail); }
    public static MailAccount of(Long id, String mail) { return new MailAccount(id, mail); }
    public Long id() { return id; } public String mail() { return mail; }
    public String username() { return username; } public String password() { return password; }
    public String host() { return host; } public String port() { return port; }
    public String nickname() { return nickname; }
    public Boolean sslEnable() { return sslEnable; } public Boolean starttlsEnable() { return starttlsEnable; }
    // Builder-style setters for reconstitution
    public MailAccount username(String v) { this.username = v; return this; }
    public MailAccount password(String v) { this.password = v; return this; }
    public MailAccount host(String v) { this.host = v; return this; }
    public MailAccount port(String v) { this.port = v; return this; }
    public MailAccount nickname(String v) { this.nickname = v; return this; }
    public MailAccount sslEnable(Boolean v) { this.sslEnable = v; return this; }
    public MailAccount starttlsEnable(Boolean v) { this.starttlsEnable = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MailAccount a && id.equals(a.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
