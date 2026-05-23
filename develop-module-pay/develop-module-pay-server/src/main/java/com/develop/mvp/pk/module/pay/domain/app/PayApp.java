package com.develop.mvp.pk.module.pay.domain.app;
// DDD 角色：支付应用聚合根
import java.util.Objects;
public final class PayApp {
    private final Long id; private final String name; private Integer status; private String remark;
    public PayApp(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static PayApp of(Long id, String name) { return new PayApp(id, name); }
    public Long id() { return id; } public String name() { return name; }
    public Integer status() { return status; } public String remark() { return remark; }
    public PayApp status(Integer v) { status = v; return this; } public PayApp remark(String v) { remark = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof PayApp a && id.equals(a.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
