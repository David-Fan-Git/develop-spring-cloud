package com.develop.mvp.pk.module.system.domain.dict;

import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeName;

import java.util.Objects;

public final class DictType {

    private final DictTypeId id;
    private final DictTypeName name;
    private final DictTypeKey type;
    private final Integer status;
    private final String remark;

    public DictType(DictTypeId id, DictTypeName name, DictTypeKey type, Integer status, String remark) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.status = status != null ? status : 0;
        this.remark = remark;
    }

    public DictTypeId id() {
        return id;
    }

    public DictTypeName name() {
        return name;
    }

    public DictTypeKey type() {
        return type;
    }

    public Integer status() {
        return status;
    }

    public String remark() {
        return remark;
    }

    public boolean isEnabled() {
        return Integer.valueOf(0).equals(status);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DictType d && Objects.equals(id, d.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
