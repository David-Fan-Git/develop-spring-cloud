package com.develop.mvp.pk.module.system.domain.dict.valueobject;
import java.util.Objects;
public final class DictTypeId { private final Long v; private DictTypeId(Long v) { this.v = Objects.requireNonNull(v); } public static DictTypeId of(Long v) { return new DictTypeId(v); } public Long value() { return v; } @Override public boolean equals(Object o) { return o instanceof DictTypeId d && v.equals(d.v); } @Override public int hashCode() { return Objects.hash(v); } }
