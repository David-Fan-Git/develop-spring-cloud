package com.develop.mvp.pk.module.system.application.permission.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.RoleUseCase;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RolePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;
import com.develop.mvp.pk.module.system.dal.mysql.permission.RoleMapper;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.domain.permission.Role;
import com.develop.mvp.pk.module.system.domain.permission.RoleFactory;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleRepository;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.DataScope;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.RoleId;
import com.develop.mvp.pk.module.system.enums.permission.DataScopeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleTypeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.mzt.logapi.context.LogRecordContext;
import com.mzt.logapi.service.impl.DiffParseFunction;
import com.mzt.logapi.starter.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertMap;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;
import static com.develop.mvp.pk.module.system.enums.LogRecordConstants.*;

@Slf4j
public class RoleApplicationService implements RoleUseCase {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionUseCase permissionService;

    public RoleApplicationService(RoleRepository roleRepository,
                                  RoleMapper roleMapper,
                                  @Lazy PermissionUseCase permissionService) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
        this.permissionService = permissionService;
    }

    // ========== RoleUseCase domain-returning methods ==========

    @Override
    public Role createRole(String name, String code, Integer sort, Integer status, String remark, Integer type) {
        return createRoleDomain(name, code, sort, status, remark, type);
    }

    @Override
    public Role updateRole(Long id, String name, String code, Integer sort, Integer status, String remark) {
        return updateRoleDomain(id, name, code, sort, status, remark);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    public Role updateRoleDataScope(Long id, Integer dataScope, Set<Long> dataScopeDeptIds) {
        Role role = validateRoleForUpdate(id);
        role.changeDataScope(DataScope.of(dataScope, dataScopeDeptIds));
        return roleRepository.save(role);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_DELETE_SUB_TYPE, bizNo = "{{#id}}",
            success = SYSTEM_ROLE_DELETE_SUCCESS)
    public Role deleteRole(Long id) {
        Role role = deleteRoleDomain(id);
        permissionService.processRoleDeleted(id);
        LogRecordContext.putVariable("role", toDataObject(role));
        return role;
    }

    @Override
    public Role getRole(Long id) {
        Role role = roleRepository.findById(RoleId.of(id));
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        return role;
    }

    // ========== Existing public API (VO/DO based) ==========

    @Transactional(rollbackFor = Exception.class)
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_CREATE_SUB_TYPE, bizNo = "{{#role.id}}",
            success = SYSTEM_ROLE_CREATE_SUCCESS)
    public Long createRole(RoleSaveReqVO createReqVO, Integer type) {
        Role role = createRoleDomain(createReqVO.getName(), createReqVO.getCode(), createReqVO.getSort(),
                createReqVO.getStatus(), createReqVO.getRemark(), type);
        RoleDO roleDO = toDataObject(role);
        LogRecordContext.putVariable("role", roleDO);
        return role.id().value();
    }

    @CacheEvict(value = RedisKeyConstants.ROLE, key = "#updateReqVO.id")
    @LogRecord(type = SYSTEM_ROLE_TYPE, subType = SYSTEM_ROLE_UPDATE_SUB_TYPE, bizNo = "{{#updateReqVO.id}}",
            success = SYSTEM_ROLE_UPDATE_SUCCESS)
    public void updateRole(RoleSaveReqVO updateReqVO) {
        RoleDO oldRole = toDataObject(validateRoleForUpdate(updateReqVO.getId()));
        Role role = updateRoleDomain(updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getCode(),
                updateReqVO.getSort(), updateReqVO.getStatus(), updateReqVO.getRemark());
        LogRecordContext.putVariable(DiffParseFunction.OLD_OBJECT, BeanUtils.toBean(oldRole, RoleSaveReqVO.class));
        LogRecordContext.putVariable("role", toDataObject(role));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteRoleList(List<Long> ids) {
        ids.forEach(id -> {
            deleteRoleDomain(id);
            permissionService.processRoleDeleted(id);
        });
    }

    @Override
    public RoleDO getRoleDO(Long id) {
        return roleMapper.selectById(id);
    }

    @Cacheable(value = RedisKeyConstants.ROLE, key = "#id", unless = "#result == null")
    public RoleDO getRoleFromCache(Long id) {
        return roleMapper.selectById(id);
    }

    public List<RoleDO> getRoleListByStatus(Collection<Integer> statuses) {
        return roleMapper.selectListByStatus(statuses);
    }

    public List<RoleDO> getRoleList() {
        return roleMapper.selectList();
    }

    public List<RoleDO> getRoleList(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return roleMapper.selectByIds(ids);
    }

    public List<RoleDO> getRoleListFromCache(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        RoleApplicationService self = getSelf();
        return CollectionUtils.convertList(ids, self::getRoleFromCache);
    }

    public PageResult<RoleDO> getRolePage(RolePageReqVO reqVO) {
        return roleMapper.selectPage(reqVO);
    }

    public boolean hasAnySuperAdmin(Collection<Long> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return false;
        }
        RoleApplicationService self = getSelf();
        return ids.stream().anyMatch(id -> {
            RoleDO role = self.getRoleFromCache(id);
            return role != null && RoleCodeEnum.isSuperAdmin(role.getCode());
        });
    }

    public void validateRoleList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<RoleDO> roles = roleMapper.selectByIds(ids);
        Map<Long, RoleDO> roleMap = convertMap(roles, RoleDO::getId);
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

    // ========== Domain helpers ==========

    private Role createRoleDomain(String name, String code, Integer sort, Integer status, String remark, Integer type) {
        validateRoleDuplicate(name, code, null);
        Role role = RoleFactory.create(null, name, code, sort,
                status != null ? status : CommonStatusEnum.ENABLE.getStatus(),
                type != null ? type : RoleTypeEnum.CUSTOM.getType(),
                remark, null, DataScopeEnum.ALL.getScope(), null);
        return roleRepository.save(role);
    }

    private Role updateRoleDomain(Long id, String name, String code, Integer sort, Integer status, String remark) {
        Role role = validateRoleForUpdate(id);
        validateRoleDuplicate(name, code, id);
        role.changeBaseInfo(name, code, sort, status, remark);
        return roleRepository.save(role);
    }

    private Role deleteRoleDomain(Long id) {
        Role role = validateRoleForUpdate(id);
        role.markDeleted();
        roleRepository.delete(RoleId.of(id));
        return role;
    }

    @VisibleForTesting
    public void validateRoleDuplicate(String name, String code, Long id) {
        if (RoleCodeEnum.isSuperAdmin(code)) {
            throw exception(ROLE_ADMIN_CODE_ERROR, code);
        }
        roleRepository.findByName(name).ifPresent(role -> {
            if (!role.hasId(id)) {
                throw exception(ROLE_NAME_DUPLICATE, name);
            }
        });
        if (!StringUtils.hasText(code)) {
            return;
        }
        roleRepository.findByCode(code).ifPresent(role -> {
            if (!role.hasId(id)) {
                throw exception(ROLE_CODE_DUPLICATE, code);
            }
        });
    }

    @VisibleForTesting
    public Role validateRoleForUpdate(Long id) {
        Role role = roleRepository.findById(RoleId.of(id));
        if (role == null) {
            throw exception(ROLE_NOT_EXISTS);
        }
        if (role.isSystem()) {
            throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
        }
        return role;
    }

    // ========== Private helpers ==========

    private RoleApplicationService getSelf() {
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
