package com.develop.mvp.pk.module.bpm.domain.definition;
// DDD 角色：BPM流程分类聚合根
import java.util.Objects;
public final class BpmCategory {
    private final Long id; private final String name; private String code; private Integer status, sort;
    public BpmCategory(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static BpmCategory of(Long id, String name) { return new BpmCategory(id, name); }
    public Long id() { return id; } public String name() { return name; } public String code() { return code; }
    public Integer status() { return status; } public Integer sort() { return sort; }
    public BpmCategory code(String v) { code = v; return this; } public BpmCategory status(Integer v) { status = v; return this; }
    public BpmCategory sort(Integer v) { sort = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof BpmCategory c && id.equals(c.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
