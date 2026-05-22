package com.develop.mvp.pk.module.system.domain.mail;

// DDD 角色：邮件模板聚合根
import java.util.Objects;

public final class MailTemplate {
    private final Long id; private final String code, name; private Long accountId;
    private String nickname, title, content; private Integer status; private String remark;
    private MailTemplate(Long id, String code, String name) { this.id = Objects.requireNonNull(id); this.code = Objects.requireNonNull(code); this.name = Objects.requireNonNull(name); }
    public static MailTemplate of(Long id, String code, String name) { return new MailTemplate(id, code, name); }
    public Long id() { return id; } public String code() { return code; } public String name() { return name; }
    public Long accountId() { return accountId; } public String nickname() { return nickname; }
    public String title() { return title; } public String content() { return content; }
    public Integer status() { return status; } public String remark() { return remark; }
    public MailTemplate accountId(Long v) { this.accountId = v; return this; }
    public MailTemplate nickname(String v) { this.nickname = v; return this; }
    public MailTemplate title(String v) { this.title = v; return this; }
    public MailTemplate content(String v) { this.content = v; return this; }
    public MailTemplate status(Integer v) { this.status = v; return this; }
    public MailTemplate remark(String v) { this.remark = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MailTemplate t && id.equals(t.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
