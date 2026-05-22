package com.develop.mvp.pk.module.system.domain.dept;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.*;
public final class DeptFactory {
    private DeptFactory() {}
    public static Dept create(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        return new Dept(DeptId.of(id), DeptName.of(name), parentId, sort, leaderUserId, phone, email, DeptStatus.ENABLED);
    }
    public static Dept reconstitute(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email, Integer status) {
        return new Dept(DeptId.of(id), DeptName.of(name), parentId, sort, leaderUserId, phone, email, DeptStatus.of(status));
    }
}
