package com.develop.mvp.pk.module.system.domain.notify;

// DDD 角色：站内信模板聚合根
import java.util.Objects;

public final class NotifyTemplate {
    private final Long id; private final String code, name; private String nickname, content;
    private Integer type, status; private String remark;
    private NotifyTemplate(Long id, String code, String name) { this.id = Objects.requireNonNull(id); this.code = Objects.requireNonNull(code); this.name = Objects.requireNonNull(name); }
    public static NotifyTemplate of(Long id, String code, String name) { return new NotifyTemplate(id, code, name); }
    public Long id() { return id; } public String code() { return code; } public String name() { return name; }
    public String nickname() { return nickname; } public String content() { return content; }
    public Integer type() { return type; } public Integer status() { return status; } public String remark() { return remark; }
    public NotifyTemplate nickname(String v) { this.nickname = v; return this; }
    public NotifyTemplate content(String v) { this.content = v; return this; }
    public NotifyTemplate type(Integer v) { this.type = v; return this; }
    public NotifyTemplate status(Integer v) { this.status = v; return this; }
    public NotifyTemplate remark(String v) { this.remark = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof NotifyTemplate t && id.equals(t.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
