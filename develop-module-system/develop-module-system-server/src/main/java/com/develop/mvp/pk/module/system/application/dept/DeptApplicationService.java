package com.develop.mvp.pk.module.system.application.dept;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.module.system.domain.dept.Dept;
import com.develop.mvp.pk.module.system.domain.dept.DeptFactory;
import com.develop.mvp.pk.module.system.domain.dept.event.DeptDomainEvent;
import com.develop.mvp.pk.module.system.domain.dept.repository.DeptRepository;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.DeptId;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
public class DeptApplicationService {
    private final DeptRepository deptRepository;
    private final ApplicationEventPublisher eventPublisher;

    public DeptApplicationService(DeptRepository deptRepository, ApplicationEventPublisher eventPublisher) {
        this.deptRepository = deptRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Long createDept(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        if (deptRepository.existsByName(name, null)) throw exception(DEPT_NAME_DUPLICATE);
        if (parentId != null && parentId != 0L) {
            Dept parent = deptRepository.findById(DeptId.of(parentId));
            if (parent == null) throw exception(DEPT_PARENT_NOT_EXITS);
            if (!parent.isEnabled()) throw exception(DEPT_NOT_ENABLE, parent.name().value());
        }
        Dept dept = DeptFactory.create(id, name, parentId, sort, leaderUserId, phone, email);
        deptRepository.save(dept);
        publishEvents(dept);
        return id;
    }

    @Transactional
    public void updateDept(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        Dept dept = findExisting(id);
        // 不能设置自己为父部门
        if (parentId != null && parentId.equals(id)) throw exception(DEPT_PARENT_ERROR);
        Dept saved = DeptFactory.create(id, name, parentId, sort, leaderUserId, phone, email);
        deptRepository.save(saved);
    }

    @Transactional
    public void deleteDept(Long id) {
        Dept dept = findExisting(id);
        if (!deptRepository.findByParentId(id).isEmpty()) throw exception(DEPT_EXITS_CHILDREN);
        dept.markDeleted();
        deptRepository.delete(dept.id());
        publishEvents(dept);
    }

    public Dept getDept(Long id) { return deptRepository.findById(DeptId.of(id)); }
    public List<Dept> getDeptList(Collection<Long> ids) { return deptRepository.findByIds(ids.stream().map(DeptId::of).collect(Collectors.toList())); }
    public List<Dept> getAllDepts() { return deptRepository.findAll(); }
    public List<Dept> getChildDeptList(Long id) { return deptRepository.findByParentId(id); }
    public List<Dept> getDeptListByLeaderUserId(Long id) { return deptRepository.findByLeaderUserId(id); }
    public Set<Long> getChildDeptIdsFromCache(Long id) { return deptRepository.findChildIdsFromCache(id); }

    public Map<Long, Dept> getDeptMap(Collection<Long> ids) {
        List<Dept> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, d -> d.id().value());
    }

    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) return;
        List<Dept> depts = getDeptList(ids);
        Map<Long, Dept> deptMap = CollectionUtils.convertMap(depts, d -> d.id().value());
        ids.forEach(id -> {
            Dept dept = deptMap.get(id);
            if (dept == null) throw exception(DEPT_NOT_FOUND);
            if (!dept.isEnabled()) throw exception(DEPT_NOT_ENABLE, dept.name().value());
        });
    }

    private Dept findExisting(Long id) {
        Dept dept = deptRepository.findById(DeptId.of(id));
        if (dept == null) throw exception(DEPT_NOT_FOUND);
        return dept;
    }

    private void publishEvents(Dept dept) {
        for (DeptDomainEvent event : dept.pullEvents()) eventPublisher.publishEvent(event);
    }
}
