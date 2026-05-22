package com.develop.mvp.pk.module.system.domain.permission.valueobject;

// Skill: AggregateRoot_Role_Menu_Skill — 值对象 DataScope
// 封装角色的数据权限范围

import com.develop.mvp.pk.module.system.enums.permission.DataScopeEnum;

import java.util.*;

public final class DataScope {
    public static final DataScope ALL = new DataScope(DataScopeEnum.ALL.getScope(), Collections.emptySet());
    private final Integer scope;
    private final Set<Long> deptIds;
    private DataScope(Integer scope, Set<Long> deptIds) {
        this.scope = Objects.requireNonNull(scope);
        this.deptIds = deptIds != null ? Collections.unmodifiableSet(new HashSet<>(deptIds)) : Collections.emptySet();
    }
    public static DataScope all() { return ALL; }
    public static DataScope of(Integer scope, Set<Long> deptIds) { return new DataScope(scope, deptIds); }
    public Integer scope() { return scope; }
    public Set<Long> deptIds() { return deptIds; }
    public boolean isAll() { return scope.equals(DataScopeEnum.ALL.getScope()); }
    public boolean isDeptCustom() { return scope.equals(DataScopeEnum.DEPT_CUSTOM.getScope()); }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DataScope d)) return false;
        return scope.equals(d.scope) && deptIds.equals(d.deptIds);
    }
    @Override public int hashCode() { return Objects.hash(scope, deptIds); }
}
