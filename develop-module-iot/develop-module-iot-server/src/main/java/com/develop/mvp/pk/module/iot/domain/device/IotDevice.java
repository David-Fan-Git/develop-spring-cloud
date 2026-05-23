package com.develop.mvp.pk.module.iot.domain.device;
import java.util.Objects;
public final class IotDevice { private final Long id; private final String name; private String deviceKey; private Long productId; private Integer status;
    public IotDevice(Long id, String name) { this.id = id; this.name = Objects.requireNonNull(name); }
    public static IotDevice of(Long id, String name) { return new IotDevice(id, name); }
    public Long id() { return id; } public String name() { return name; } public String deviceKey() { return deviceKey; }
    public Long productId() { return productId; } public Integer status() { return status; }
    public IotDevice deviceKey(String v) { deviceKey = v; return this; } public IotDevice productId(Long v) { productId = v; return this; }
    public IotDevice status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof IotDevice d && id.equals(d.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
