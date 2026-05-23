package com.develop.mvp.pk.module.ai.domain.model;
import java.util.Objects;
public final class AiModel { private final Long id; private final String name; private String platform, apiKey, type; private Integer status;
    public AiModel(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static AiModel of(Long id, String name) { return new AiModel(id, name); }
    public Long id() { return id; } public String name() { return name; } public String platform() { return platform; }
    public String apiKey() { return apiKey; } public String type() { return type; } public Integer status() { return status; }
    public AiModel platform(String v) { platform = v; return this; } public AiModel apiKey(String v) { apiKey = v; return this; }
    public AiModel type(String v) { type = v; return this; } public AiModel status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof AiModel m && id.equals(m.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
