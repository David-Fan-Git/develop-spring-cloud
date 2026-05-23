package com.develop.mvp.pk.module.mes.domain.dv;
import java.util.Objects;
public final class MesMachinery { private final Long id; private final String name; private String code, type; private Integer status;
    public MesMachinery(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static MesMachinery of(Long id, String name) { return new MesMachinery(id, name); }
    public Long id() { return id; } public String name() { return name; } public String code() { return code; }
    public String type() { return type; } public Integer status() { return status; }
    public MesMachinery code(String v) { code = v; return this; } public MesMachinery type(String v) { type = v; return this; }
    public MesMachinery status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof MesMachinery m && id.equals(m.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
