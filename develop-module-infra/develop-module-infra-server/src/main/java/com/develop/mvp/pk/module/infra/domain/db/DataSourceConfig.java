package com.develop.mvp.pk.module.infra.domain.db;

// DDD 角色：数据源配置聚合根
import java.util.Objects;

public final class DataSourceConfig {
    private final Long id; private final String name, url; private String username, password;
    public DataSourceConfig(Long id, String name, String url) { this.id = id; this.name = Objects.requireNonNull(name); this.url = Objects.requireNonNull(url); }
    public static DataSourceConfig of(Long id, String name, String url) { return new DataSourceConfig(id, name, url); }
    public Long id() { return id; } public String name() { return name; } public String url() { return url; }
    public String username() { return username; } public String password() { return password; }
    public DataSourceConfig username(String v) { username = v; return this; }
    public DataSourceConfig password(String v) { password = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof DataSourceConfig c && id.equals(c.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
