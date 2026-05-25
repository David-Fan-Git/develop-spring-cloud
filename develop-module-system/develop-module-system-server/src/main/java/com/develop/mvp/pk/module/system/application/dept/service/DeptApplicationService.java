package com.develop.mvp.pk.module.system.application.dept.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.framework.datapermission.core.annotation.DataPermission;
import com.develop.mvp.pk.module.system.application.dept.port.inbound.DeptUseCase;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.dept.DeptListReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.dept.DeptSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.DeptDO;
import com.develop.mvp.pk.module.system.dal.dataobject.dept.PostDO;
import com.develop.mvp.pk.module.system.dal.mysql.dept.DeptMapper;
import com.develop.mvp.pk.module.system.dal.mysql.dept.PostMapper;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.domain.dept.Dept;
import com.develop.mvp.pk.module.system.domain.dept.DeptFactory;
import com.develop.mvp.pk.module.system.domain.dept.event.DeptDomainEvent;
import com.develop.mvp.pk.module.system.domain.dept.repository.DeptRepository;
import com.develop.mvp.pk.module.system.domain.dept.valueobject.DeptId;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertMap;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Validated
public class DeptApplicationService implements DeptUseCase {

    private final DeptRepository deptRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final DeptMapper deptMapper;
    private final PostMapper postMapper;

    public DeptApplicationService(
            @Autowired(required = false) DeptRepository deptRepository,
            @Autowired(required = false) ApplicationEventPublisher eventPublisher,
            DeptMapper deptMapper,
            PostMapper postMapper) {
        this.deptRepository = deptRepository;
        this.eventPublisher = eventPublisher;
        this.deptMapper = deptMapper;
        this.postMapper = postMapper;
    }

    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public Long createDept(DeptSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        validateParentDept(null, createReqVO.getParentId());
        validateDeptNameUnique(null, createReqVO.getParentId(), createReqVO.getName());
        DeptDO dept = BeanUtils.toBean(createReqVO, DeptDO.class);
        deptMapper.insert(dept);
        return dept.getId();
    }

    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public void updateDept(DeptSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(DeptDO.PARENT_ID_ROOT);
        }
        validateDeptExists(updateReqVO.getId());
        validateParentDept(updateReqVO.getId(), updateReqVO.getParentId());
        validateDeptNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());
        DeptDO updateObj = BeanUtils.toBean(updateReqVO, DeptDO.class);
        deptMapper.updateById(updateObj);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public void deleteDept(Long id) {
        validateDeptExists(id);
        if (deptMapper.selectCountByParentId(id) > 0) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        deptMapper.deleteById(id);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, allEntries = true)
    public void deleteDeptList(List<Long> ids) {
        for (Long id : ids) {
            if (deptMapper.selectCountByParentId(id) > 0) {
                throw exception(DEPT_EXITS_CHILDREN);
            }
        }
        deptMapper.deleteByIds(ids);
    }

    @VisibleForTesting
    void validateDeptExists(Long id) {
        if (id == null) {
            return;
        }
        DeptDO dept = deptMapper.selectById(id);
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
    }

    @VisibleForTesting
    void validateParentDept(Long id, Long parentId) {
        if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        if (Objects.equals(id, parentId)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        DeptDO parentDept = deptMapper.selectById(parentId);
        if (parentDept == null) {
            throw exception(DEPT_PARENT_NOT_EXITS);
        }
        if (id == null) {
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            parentId = parentDept.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(DEPT_PARENT_IS_CHILD);
            }
            if (parentId == null || DeptDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentDept = deptMapper.selectById(parentId);
            if (parentDept == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateDeptNameUnique(Long id, Long parentId, String name) {
        DeptDO dept = deptMapper.selectByParentIdAndName(parentId, name);
        if (dept == null) {
            return;
        }
        if (id == null || ObjectUtil.notEqual(dept.getId(), id)) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
    }

    public DeptDO getDept(Long id) {
        return deptMapper.selectById(id);
    }

    public List<DeptDO> getDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return deptMapper.selectByIds(ids);
    }

    public List<DeptDO> getDeptList(DeptListReqVO reqVO) {
        List<DeptDO> list = deptMapper.selectList(reqVO);
        list.sort(Comparator.comparing(DeptDO::getSort));
        return list;
    }

    public Map<Long, DeptDO> getDeptMap(Collection<Long> ids) {
        List<DeptDO> list = getDeptList(ids);
        return CollectionUtils.convertMap(list, DeptDO::getId);
    }

    public List<DeptDO> getChildDeptList(Long id) {
        return getChildDeptList(Collections.singleton(id));
    }

    public List<DeptDO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> children = new LinkedList<>();
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            List<DeptDO> depts = deptMapper.selectListByParentId(parentIds);
            if (CollUtil.isEmpty(depts)) {
                break;
            }
            children.addAll(depts);
            parentIds = convertSet(depts, DeptDO::getId);
        }
        return children;
    }

    public List<DeptDO> getDeptListByLeaderUserId(Long id) {
        return deptMapper.selectListByLeaderUserId(id);
    }

    @DataPermission(enable = false)
    @Cacheable(cacheNames = RedisKeyConstants.DEPT_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildDeptIdListFromCache(Long id) {
        List<DeptDO> children = getChildDeptList(id);
        return convertSet(children, DeptDO::getId);
    }

    public void validateDeptList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        Map<Long, DeptDO> deptMap = getDeptMap(ids);
        ids.forEach(id -> {
            DeptDO dept = deptMap.get(id);
            if (dept == null) {
                throw exception(DEPT_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dept.getStatus())) {
                throw exception(DEPT_NOT_ENABLE, dept.getName());
            }
        });
    }

    public Long createPost(PostSaveReqVO createReqVO) {
        validatePostForCreateOrUpdate(null, createReqVO.getName(), createReqVO.getCode());
        PostDO post = BeanUtils.toBean(createReqVO, PostDO.class);
        postMapper.insert(post);
        return post.getId();
    }

    public void updatePost(PostSaveReqVO updateReqVO) {
        validatePostForCreateOrUpdate(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getCode());
        PostDO updateObj = BeanUtils.toBean(updateReqVO, PostDO.class);
        postMapper.updateById(updateObj);
    }

    public void deletePost(Long id) {
        validatePostExists(id);
        postMapper.deleteById(id);
    }

    public void deletePostList(List<Long> ids) {
        postMapper.deleteByIds(ids);
    }

    public List<PostDO> getPostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return postMapper.selectByIds(ids);
    }

    public List<PostDO> getPostList(Collection<Long> ids, Collection<Integer> statuses) {
        return postMapper.selectList(ids, statuses);
    }

    public PageResult<PostDO> getPostPage(PostPageReqVO reqVO) {
        return postMapper.selectPage(reqVO);
    }

    public PostDO getPost(Long id) {
        return postMapper.selectById(id);
    }

    public void validatePostList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<PostDO> posts = postMapper.selectByIds(ids);
        Map<Long, PostDO> postMap = convertMap(posts, PostDO::getId);
        ids.forEach(id -> {
            PostDO post = postMap.get(id);
            if (post == null) {
                throw exception(POST_NOT_FOUND);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(post.getStatus())) {
                throw exception(POST_NOT_ENABLE, post.getName());
            }
        });
    }

    private void validatePostForCreateOrUpdate(Long id, String name, String code) {
        validatePostExists(id);
        validatePostNameUnique(id, name);
        validatePostCodeUnique(id, code);
    }

    private void validatePostNameUnique(Long id, String name) {
        PostDO post = postMapper.selectByName(name);
        if (post == null) {
            return;
        }
        if (id == null || !post.getId().equals(id)) {
            throw exception(POST_NAME_DUPLICATE);
        }
    }

    private void validatePostCodeUnique(Long id, String code) {
        PostDO post = postMapper.selectByCode(code);
        if (post == null) {
            return;
        }
        if (id == null || !post.getId().equals(id)) {
            throw exception(POST_CODE_DUPLICATE);
        }
    }

    private void validatePostExists(Long id) {
        if (id == null) {
            return;
        }
        if (postMapper.selectById(id) == null) {
            throw exception(POST_NOT_FOUND);
        }
    }

    @Transactional
    public Long createDeptDomain(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        if (deptRepository.existsByName(name, null)) {
            throw exception(DEPT_NAME_DUPLICATE);
        }
        if (parentId != null && parentId != 0L) {
            Dept parent = deptRepository.findById(DeptId.of(parentId));
            if (parent == null) {
                throw exception(DEPT_PARENT_NOT_EXITS);
            }
            if (!parent.isEnabled()) {
                throw exception(DEPT_NOT_ENABLE, parent.name().value());
            }
        }
        Dept dept = DeptFactory.create(id, name, parentId, sort, leaderUserId, phone, email);
        deptRepository.save(dept);
        publishEvents(dept);
        return id;
    }

    @Transactional
    public void updateDeptDomain(Long id, String name, Long parentId, Integer sort, Long leaderUserId, String phone, String email) {
        findExistingDomain(id);
        if (parentId != null && parentId.equals(id)) {
            throw exception(DEPT_PARENT_ERROR);
        }
        Dept saved = DeptFactory.create(id, name, parentId, sort, leaderUserId, phone, email);
        deptRepository.save(saved);
    }

    @Transactional
    public void deleteDeptDomain(Long id) {
        Dept dept = findExistingDomain(id);
        if (!deptRepository.findByParentId(id).isEmpty()) {
            throw exception(DEPT_EXITS_CHILDREN);
        }
        dept.markDeleted();
        deptRepository.delete(dept.id());
        publishEvents(dept);
    }

    public Dept getDeptDomain(Long id) {
        return deptRepository.findById(DeptId.of(id));
    }

    public List<Dept> getDeptDomainList(Collection<Long> ids) {
        return deptRepository.findByIds(ids.stream().map(DeptId::of).collect(Collectors.toList()));
    }

    public List<Dept> getAllDeptDomains() {
        return deptRepository.findAll();
    }

    public List<Dept> getChildDeptDomainList(Long id) {
        return deptRepository.findByParentId(id);
    }

    public Set<Long> getChildDeptDomainIdsFromCache(Long id) {
        return deptRepository.findChildIdsFromCache(id);
    }

    private Dept findExistingDomain(Long id) {
        Dept dept = deptRepository.findById(DeptId.of(id));
        if (dept == null) {
            throw exception(DEPT_NOT_FOUND);
        }
        return dept;
    }

    private void publishEvents(Dept dept) {
        if (eventPublisher == null) {
            return;
        }
        for (DeptDomainEvent event : dept.pullEvents()) {
            eventPublisher.publishEvent(event);
        }
    }
}
