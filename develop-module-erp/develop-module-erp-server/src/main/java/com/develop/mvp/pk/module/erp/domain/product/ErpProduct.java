package com.develop.mvp.pk.module.erp.domain.product;
import java.util.Objects;
public final class ErpProduct { private final Long id; private final String name; private String no, unit; private Integer status, price;
    public ErpProduct(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static ErpProduct of(Long id, String name) { return new ErpProduct(id, name); }
    public Long id() { return id; } public String name() { return name; } public String no() { return no; }
    public Integer status() { return status; } public Integer price() { return price; }
    public ErpProduct no(String v) { no = v; return this; } public ErpProduct unit(String v) { unit = v; return this; }
    public ErpProduct status(Integer v) { status = v; return this; } public ErpProduct price(Integer v) { price = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof ErpProduct p && id.equals(p.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
