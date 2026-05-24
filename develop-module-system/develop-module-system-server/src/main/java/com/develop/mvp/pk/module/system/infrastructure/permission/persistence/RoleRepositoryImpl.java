package com.develop.mvp.pk.module.system.infrastructure.permission.persistence;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.pojo.PageParam;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMapper;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMenuMapper;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleMenuDO;
import com.develop.mvp.pk.module.system.domain.permission.Role;
import com.develop.mvp.pk.module.system.domain.permission.RoleFactory;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleRepository;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.RoleId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;

@Repository
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleMapper roleMapper;
    private final RoleMenuMapper roleMenuMapper;

    public RoleRepositoryImpl(RoleMapper roleMapper, RoleMenuMapper roleMenuMapper) {
        this.roleMapper = roleMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    @Transactional
    public Role save(Role role) {
        RoleDO roleDO = toDataObject(role);
        if (role.id() == null) {
            roleMapper.insert(roleDO);
            return toDomain(roleDO);
        }
        roleMapper.updateById(roleDO);
        return toDomain(roleDO);
    }

    @Override
    @Transactional
    public void delete(RoleId id) {
        roleMapper.deleteById(id.value());
    }

    @Override
    public Role findById(RoleId id) {
        RoleDO d = roleMapper.selectById(id.value());
        return d != null ? toDomain(d) : null;
    }

    @Override
    public List<Role> findByIds(Collection<RoleId> ids) {
        if (CollUtil.isEmpty(ids)) return Collections.emptyList();
        List<Long> rawIds = ids.stream().map(RoleId::value).collect(Collectors.toList());
        return roleMapper.selectByIds(rawIds).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Role> findByStatus(Collection<Integer> statuses) {
        return roleMapper.selectListByStatus(statuses).stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.selectList().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<Role> findPage(String name, String code, Integer status,
                                      LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo);
        pageParam.setPageSize(pageSize);
        PageResult<RoleDO> doPage = roleMapper.selectPage(pageParam, new LambdaQueryWrapperX<RoleDO>()
                .likeIfPresent(RoleDO::getName, name)
                .likeIfPresent(RoleDO::getCode, code)
                .eqIfPresent(RoleDO::getStatus, status)
                .betweenIfPresent(RoleDO::getCreateTime, createTime)
                .orderByAsc(RoleDO::getSort));
        return new PageResult<>(
                doPage.getList().stream().map(this::toDomain).collect(Collectors.toList()),
                doPage.getTotal());
    }

    @Override
    public Optional<Role> findByName(String name) {
        return Optional.ofNullable(roleMapper.selectByName(name)).map(this::toDomain);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return Optional.ofNullable(roleMapper.selectByCode(code)).map(this::toDomain);
    }

    private RoleDO toDataObject(Role role) {
        RoleDO d = new RoleDO();
        d.setId(role.id() != null ? role.id().value() : null); d.setName(role.name().value()); d.setCode(role.code().value());
        d.setSort(role.sort()); d.setStatus(role.status().code()); d.setType(role.type().code());
        d.setRemark(role.remark()); d.setTenantId(role.tenantId());
        d.setDataScope(role.dataScope().scope()); d.setDataScopeDeptIds(role.dataScope().deptIds());
        return d;
    }

    private Role toDomain(RoleDO d) {
        Set<Long> menuIds = convertSet(roleMenuMapper.selectListByRoleId(d.getId()), RoleMenuDO::getMenuId);
        return RoleFactory.reconstitute(d.getId(), d.getName(), d.getCode(), d.getSort(),
                d.getStatus(), d.getType(), d.getRemark(), d.getTenantId(),
                d.getDataScope(), d.getDataScopeDeptIds(), menuIds);
    }
}
