package com.develop.mvp.pk.module.system.domain.dept.valueobject;

import com.develop.mvp.pk.framework.common.exception.ServiceException;
import java.util.Objects;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.DEPT_NAME_DUPLICATE;

public final class DeptName {
    private final String value;
    private DeptName(String value) {
        if (value == null || value.isBlank()) throw new ServiceException(DEPT_NAME_DUPLICATE.getCode(), "部门名称不能为空");
        this.value = value.trim();
    }
    public static DeptName of(String value) { return new DeptName(value); }
    public String value() { return value; }
    @Override public boolean equals(Object o) { return o instanceof DeptName d && value.equals(d.value); }
    @Override public int hashCode() { return Objects.hash(value); }
}
