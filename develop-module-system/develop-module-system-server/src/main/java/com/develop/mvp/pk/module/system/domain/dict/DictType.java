package com.develop.mvp.pk.module.system.domain.dict;

// Skill: AggregateRoot_Dict_Skill — 聚合根 DictType
// DDD 角色：字典类型聚合根，封装字典类型的唯一性约束

import com.develop.mvp.pk.module.system.domain.dict.valueobject.*;
import java.util.Objects;

public final class DictType {
    private final DictTypeId id;
    private final DictTypeName name;
    private final DictTypeKey type;
    private final Integer status;
    private final String remark;

    DictType(DictTypeId id, DictTypeName name, DictTypeKey type, Integer status, String remark) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.type = Objects.requireNonNull(type);
        this.status = status != null ? status : 0;
        this.remark = remark;
    }

    public DictTypeId id() { return id; }
    public DictTypeName name() { return name; }
    public DictTypeKey type() { return type; }
    public Integer status() { return status; }
    public String remark() { return remark; }
    public boolean isEnabled() { return Integer.valueOf(0).equals(status); }

    @Override public boolean equals(Object o) { return o instanceof DictType d && id.equals(d.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
