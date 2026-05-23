package com.develop.mvp.pk.module.infra.domain.codegen.valueobject;

// DDD 角色：代码生成表定义标识值对象
import java.util.Objects;

public final class CodegenTableId {
    private final Long value;

    private CodegenTableId(Long value) {
        this.value = Objects.requireNonNull(value, "代码生成表ID不能为空");
    }

    public static CodegenTableId of(Long value) { return new CodegenTableId(value); }

    public Long value() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CodegenTableId that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() { return Objects.hash(value); }

    @Override
    public String toString() { return "CodegenTableId{" + value + '}'; }
}
