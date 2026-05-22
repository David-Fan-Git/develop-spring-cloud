package com.develop.mvp.pk.module.system.domain.notice;

// DDD 角色：通知公告聚合根
import java.util.Objects;

public final class Notice {
    private final Long id; private final String title; private String content, type; private Integer status;
    private Notice(Long id, String title) { this.id = Objects.requireNonNull(id); this.title = Objects.requireNonNull(title); }
    public static Notice of(Long id, String title) { return new Notice(id, title); }
    public Long id() { return id; } public String title() { return title; }
    public String content() { return content; } public String type() { return type; }
    public Integer status() { return status; }
    public Notice content(String v) { this.content = v; return this; }
    public Notice type(String v) { this.type = v; return this; }
    public Notice status(Integer v) { this.status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof Notice n && id.equals(n.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
