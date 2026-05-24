package com.develop.mvp.pk.module.system.service.permission;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.application.permission.service.RoleApplicationService;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMapper;
import com.develop.mvp.pk.module.system.domain.permission.Role;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertMap;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;
import static com.develop.mvp.pk.module.system.enums.LogRecordConstants.*;

/**
 * 角色 Service 实现类
 *
 * @author David
 */
@Service
@Slf4j
public class RoleServiceImpl implements RoleService {

    @Resource
    private PermissionService permissionService;

    @Resource
    private RoleApplicationService roleApplicationService;

    @Resource
    private RoleMapper roleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_CREATE_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_ROLE_CREATE_SUCCESS)
    public Long createRole(RoleSaveReqVO createReqVO, Integer type) {
        Role role = roleApplicationService.createRole(createReqVO.getName(), createReqVO.getCode(),
                createReqVO.getSort(), createReqVO.getStatus(), createReqVO.getRemark(), type);
        RoleDO roleDO = toDataObject(role);
        LogRecordContext.putVariable("role", roleDO);
        return role.id().value();
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateReqVO.id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateRole(RoleSaveReqVO updateReqVO) {
        RoleDO oldRole = toDataObject(roleApplicationService.validateRoleForUpdate(updateReqVO.getId()));
        Role role = roleApplicationService.updateRole(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getCode(),
                updateReqVO.getSort(), updateReqVO.getStatus(), updateReqVO.getRemark());
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldRole, RoleSaveReqVO.class));
        LogRecordContext.putVariable("role", toDataObject(role));
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public void updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleApplicationService.updateRoleDataScope(id, dataScope, dataScopeDeptIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_ROLE_DELETE_SUCCESS)
    public void deleteRole(Long id) {
        Role role = roleApplicationService.deleteRole(id);
        permissionService.processRoleDeleted(id);
        LogRecordContext.putVariable("role", toDataObject(role));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleList(List<Long> ids) {
        ids.forEach(id -> {
            roleApplicationService.deleteRole(id);
            permissionService.processRoleDeleted(id);
        });
    }

    /**
     * 校验角色的唯一字段是否重复
     *
     * 1. 是否存在相同名字的角色
     * 2. 是否存在相同编码的角色
     *
     * @param name 角色名字
     * @param code 角色额编码
     * @param id 角色编号
     */
    @VisibleForTesting
    void validateRoleDuplicate(String name, String code, Long id) {
        roleApplicationService.validateRoleDuplicate(name, code, id);
    }

    /**
     * 校验角色是否可以被更新
     *
     * @param id 角色编号
     */
    @VisibleForTesting
    RoleDO validateRoleForUpdate(Long id) {
        return toDataObject(roleApplicationService.validateRoleForUpdate(id));
    }

    @Override
    public RoleDO getRole(Long id) {
        return roleMapper.selectById(id);
    }

    @Override
    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id",
            unless = "#result == null")
    public RoleDO getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }


    @Override
    public List<RoleDO> getRoleListByStatus(Collection<Integer> statuses) {
        return roleMapper.selectListByStatus(statuses);
    }

    @Override
    public List<RoleDO> getRoleList() {
        return roleMapper.selectList();
    }

    @Override
    public List<RoleDO> getRoleList(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleMapper.selectByIds(ids);
    }

    @Override
    public List<RoleDO> getRoleListFromCache(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        // 这里采用 for 循环从缓存中获取，主要考虑 Spring CacheManager 无法批量操作的问题
        RoleServiceImpl self = getSelf();
        return CollectionUtils.convertList(ids, self::getRoleFromCache);
    }

    @Override
    public PageResult<RoleDO> getRolePage(RolePageReqVO reqVO) {
        return roleMapper.selectPage(reqVO);
    }

    @Override
    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        RoleServiceImpl self = getSelf();
        return ids.stream().anyMatch(id -> {
            RoleDO role = self.getRoleFromCache(id);
            return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
        });
    }

    @Override
    public void validateRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得角色信息
        List<RoleDO> roles = roleMapper.selectByIds(ids);
        Map<Long, RoleDO> roleMap = convertMap(roles, RoleDO::getId);
        // 校验
        ids.forEach(id -> {
            RoleDO role = roleMap.get(id);
            if (role == null) {
                throw exception(ROLE_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())) {
                throw exception(ROLE_IS_DISABLE, role.getName());
            }
        });
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private RoleServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

    private RoleDO toDataObject(Role role) {
        RoleDO roleDO = new RoleDO();
        roleDO.setId(role.id() != null ? role.id().value() : null);
        roleDO.setName(role.name().value());
        roleDO.setCode(role.code().value());
        roleDO.setSort(role.sort());
        roleDO.setStatus(role.status().code());
        roleDO.setType(role.type().code());
        roleDO.setRemark(role.remark());
        roleDO.setTenantId(role.tenantId());
        roleDO.setDataScope(role.dataScope().scope());
        roleDO.setDataScopeDeptIds(role.dataScope().deptIds());
        return roleDO;
    }

}
