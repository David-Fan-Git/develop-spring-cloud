package com.develop.mvp.pk.module.system.domain.dict.valueobject;

import java.util.Objects;

public final class DictDataId {

    private final Long v;

    private DictDataId(Long v) {
        this.v = v;
    }

    public static DictDataId of(Long v) {
        return new DictDataId(v);
    }

    public Long value() {
        return v;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DictDataId d && Objects.equals(v, d.v);
    }

    @Override
    public int hashCode() {
        return Objects.hash(v);
    }

}
