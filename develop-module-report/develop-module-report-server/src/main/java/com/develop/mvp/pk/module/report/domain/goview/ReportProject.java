package com.develop.mvp.pk.module.report.domain.goview;
import java.util.Objects;
public final class ReportProject { private final Long id; private final String name; private Integer status; private String remark;
    public ReportProject(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static ReportProject of(Long id, String name) { return new ReportProject(id, name); }
    public Long id() { return id; } public String name() { return name; } public Integer status() { return status; } public String remark() { return remark; }
    public ReportProject status(Integer v) { status = v; return this; } public ReportProject remark(String v) { remark = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof ReportProject p && id.equals(p.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
