package com.develop.mvp.pk.module.system.domain.dept.repository;
import com.develop.mvp.pk.module.system.domain.dept.Dept;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.DeptId;
import java.util.*;
public interface DeptRepository {
    void save(Dept dept);
    void delete(DeptId id);
    Dept findById(DeptId id);
    List<Dept> findByIds(Collection<DeptId> ids);
    List<Dept> findByParentId(Long parentId);
    List<Dept> findByLeaderUserId(Long userId);
    List<Dept> findAll();
    Set<Long> findChildIdsFromCache(Long parentId);
    boolean existsByName(String name, Long excludeId);
}
