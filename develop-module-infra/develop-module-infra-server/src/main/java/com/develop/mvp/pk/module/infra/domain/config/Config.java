package com.develop.mvp.pk.module.infra.domain.config;

// DDD 角色：系统配置聚合根
import java.util.Objects;

public final class Config {
    private final Long id; private final String key; private String value, name, category;
    private Integer type, visible;
    public Config(Long id, String key) { this.id = id; this.key = Objects.requireNonNull(key); }
    public static Config of(Long id, String key) { return new Config(id, key); }
    public Long id() { return id; } public String key() { return key; }
    public String value() { return value; } public String name() { return name; } public String category() { return category; }
    public Integer type() { return type; } public Integer visible() { return visible; }
    public Config value(String v) { value = v; return this; } public Config name(String v) { name = v; return this; }
    public Config category(String v) { category = v; return this; } public Config type(Integer v) { type = v; return this; }
    public Config visible(Integer v) { visible = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof Config c && Objects.equals(id, c.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
