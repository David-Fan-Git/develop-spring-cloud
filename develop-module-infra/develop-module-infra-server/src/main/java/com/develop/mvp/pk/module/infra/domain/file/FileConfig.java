package com.develop.mvp.pk.module.infra.domain.file;

// DDD 角色：文件存储配置聚合根
import java.util.Objects;

public final class FileConfig {
    private final Long id; private final String name; private Integer storage, master;
    private String remark;
    public FileConfig(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static FileConfig of(Long id, String name) { return new FileConfig(id, name); }
    public Long id() { return id; } public String name() { return name; }
    public Integer storage() { return storage; } public Integer master() { return master; } public String remark() { return remark; }
    public FileConfig storage(Integer v) { storage = v; return this; } public FileConfig master(Integer v) { master = v; return this; }
    public FileConfig remark(String v) { remark = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof FileConfig fc && id.equals(fc.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
