package com.develop.mvp.pk.module.crm.domain.customer;
import java.util.Objects;
public final class CrmCustomer { private final Long id; private final String name; private Long ownerUserId; private Integer level, industry, status;
    public CrmCustomer(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static CrmCustomer of(Long id, String name) { return new CrmCustomer(id, name); }
    public Long id() { return id; } public String name() { return name; } public Long ownerUserId() { return ownerUserId; } public Integer status() { return status; }
    public CrmCustomer ownerUserId(Long v) { ownerUserId = v; return this; } public CrmCustomer status(Integer v) { status = v; return this; }
    public CrmCustomer level(Integer v) { level = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof CrmCustomer c && id.equals(c.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
