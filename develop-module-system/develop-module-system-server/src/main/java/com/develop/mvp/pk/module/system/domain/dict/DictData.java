package com.develop.mvp.pk.module.system.domain.dict;

import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataValue;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;

import java.util.Objects;

public final class DictData {

    private final DictDataId id;
    private final DictTypeKey dictType;
    private final DictDataValue value;
    private final String label;
    private final Integer sort;
    private final Integer status;
    private final String colorType;
    private final String cssClass;
    private final String remark;

    public DictData(DictDataId id, DictTypeKey dictType, DictDataValue value, String label,
                    Integer sort, Integer status, String colorType, String cssClass, String remark) {
        this.id = id;
        this.dictType = Objects.requireNonNull(dictType);
        this.value = Objects.requireNonNull(value);
        this.label = label;
        this.sort = sort != null ? sort : 0;
        this.status = status != null ? status : 0;
        this.colorType = colorType;
        this.cssClass = cssClass;
        this.remark = remark;
    }

    public DictDataId id() {
        return id;
    }

    public DictTypeKey dictType() {
        return dictType;
    }

    public DictDataValue value() {
        return value;
    }

    public String label() {
        return label;
    }

    public Integer sort() {
        return sort;
    }

    public Integer status() {
        return status;
    }

    public String colorType() {
        return colorType;
    }

    public String cssClass() {
        return cssClass;
    }

    public String remark() {
        return remark;
    }

    public boolean isEnabled() {
        return Integer.valueOf(0).equals(status);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DictData d && Objects.equals(id, d.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
