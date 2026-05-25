package com.develop.mvp.pk.module.system.infrastructure.dept.persistence;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.DeptDO;
import com.develop.mvp.pk.module.system.dal.mysql.dept.DeptMapper;
import com.develop.mvp.pk.module.system.domain.dept.Dept;
import com.develop.mvp.pk.module.system.domain.dept.DeptFactory;
import com.develop.mvp.pk.module.system.domain.dept.repository.DeptRepository;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.DeptId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DeptRepositoryImpl implements DeptRepository {
    private final DeptMapper deptMapper;

    public DeptRepositoryImpl(DeptMapper deptMapper) {
        this.deptMapper = deptMapper;
    }

    @Override @Transactional
    public void save(Dept dept) {
        DeptDO d = toDO(dept);
        if (deptMapper.selectById(dept.id().value()) == null) deptMapper.insert(d);
        else deptMapper.updateById(d);
    }

    @Override @Transactional
    public void delete(DeptId id) { deptMapper.deleteById(id.value()); }

    @Override
    public Dept findById(DeptId id) { return fromDO(deptMapper.selectById(id.value())); }

    @Override
    public List<Dept> findByIds(Collection<DeptId> ids) {
        if (CollUtil.isEmpty(ids)) return Collections.emptyList();
        return deptMapper.selectBatchIds(ids.stream().map(DeptId::value).toList())
                .stream().map(this::fromDO).collect(Collectors.toList());
    }

    @Override
    public List<Dept> findByParentId(Long parentId) {
        return deptMapper.selectListByParentId(Collections.singleton(parentId))
                .stream().map(this::fromDO).collect(Collectors.toList());
    }

    @Override
    public List<Dept> findByLeaderUserId(Long userId) {
        return deptMapper.selectListByLeaderUserId(userId).stream().map(this::fromDO).collect(Collectors.toList());
    }

    @Override
    public List<Dept> findAll() {
        return deptMapper.selectList(new DeptListReqVO()).stream().map(this::fromDO).collect(Collectors.toList());
    }

    @Override
    public Set<Long> findChildIdsFromCache(Long parentId) {
        return deptMapper.selectListByParentId(Collections.singleton(parentId)).stream()
                .map(DeptDO::getId).collect(Collectors.toSet());
    }

    @Override
    public boolean existsByName(String name, Long excludeId) {
        DeptDO existing = deptMapper.selectByParentIdAndName(null, name);
        if (existing == null) return false;
        return excludeId == null || !existing.getId().equals(excludeId);
    }

    private DeptDO toDO(Dept dept) {
        DeptDO d = new DeptDO();
        d.setId(dept.id().value()); d.setName(dept.name().value());
        d.setParentId(dept.parentId()); d.setSort(dept.sort());
        d.setLeaderUserId(dept.leaderUserId()); d.setPhone(dept.phone());
        d.setEmail(dept.email()); d.setStatus(dept.status().code());
        return d;
    }

    private Dept fromDO(DeptDO d) {
        if (d == null) return null;
        return DeptFactory.reconstitute(d.getId(), d.getName(), d.getParentId(),
                d.getSort(), d.getLeaderUserId(), d.getPhone(), d.getEmail(), d.getStatus());
    }
}
