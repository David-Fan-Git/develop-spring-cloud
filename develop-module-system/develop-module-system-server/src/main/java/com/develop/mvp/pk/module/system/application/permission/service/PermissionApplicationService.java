package com.develop.mvp.pk.module.system.application.permission.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.develop.mvp.pk.framework.common.biz.system.permission.dto.DeptDataPermissionRespDTO;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.datapermission.core.annotation.DataPermission;
import com.develop.mvp.pk.module.system.application.dept.service.DeptApplicationService;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.application.user.service.AdminUserApplicationService;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.MenuDO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.RoleDO;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.domain.permission.repository.RoleMenuRepository;
import com.develop.mvp.pk.module.system.domain.permission.repository.UserRoleRepository;
import com.develop.mvp.pk.module.system.enums.permission.DataScopeEnum;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Supplier;

import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;
import static com.develop.mvp.pk.framework.common.util.json.JsonUtils.toJsonString;

@Service
@Slf4j
public class PermissionApplicationService implements PermissionUseCase {

    @Resource
    private UserRoleRepository userRoleRepository;
    @Resource
    private RoleMenuRepository roleMenuRepository;
    @Resource
    @Lazy
    private RoleApplicationService roleService;
    @Resource
    @Lazy
    private MenuApplicationService menuService;
    @Resource
    private DeptApplicationService deptService;
    @Resource
    private AdminUserApplicationService userService;

    public boolean hasAnyPermissions(Long userId, String... permissions) {
        if (ArrayUtil.isEmpty(permissions)) {
            return true;
        }
        List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roles)) {
            return false;
        }
        for (String permission : permissions) {
            if (hasAnyPermission(roles, permission)) {
                return true;
            }
        }
        return roleService.hasAnySuperAdmin(convertSet(roles, RoleDO::getId));
    }

    private boolean hasAnyPermission(List<RoleDO> roles, String permission) {
        List<Long> menuIds = menuService.getMenuIdListByPermissionFromCache(permission);
        if (CollUtil.isEmpty(menuIds)) {
            return false;
        }
        Set<Long> roleIds = convertSet(roles, RoleDO::getId);
        for (Long menuId : menuIds) {
            Set<Long> menuRoleIds = getSelf().getMenuRoleIdListByMenuIdFromCache(menuId);
            if (CollUtil.containsAny(menuRoleIds, roleIds)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyRoles(Long userId, String... roles) {
        if (ArrayUtil.isEmpty(roles)) {
            return true;
        }
        List<RoleDO> roleList = getEnableUserRoleListByUserIdFromCache(userId);
        if (CollUtil.isEmpty(roleList)) {
            return false;
        }
        Set<String> userRoles = convertSet(roleList, RoleDO::getCode);
        return CollUtil.containsAny(userRoles, Sets.newHashSet(roles));
    }

    @Override
    @DSTransactional
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, allEntries = true),
            @CacheEvict(value = RedisKeyConstants.PERMISSION_MENU_ID_LIST, allEntries = true)
    })
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        roleMenuRepository.assign(roleId, menuIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, allEntries = true),
            @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, allEntries = true)
    })
    public void processRoleDeleted(Long roleId) {
        userRoleRepository.deleteByRoleId(roleId);
        roleMenuRepository.deleteByRoleId(roleId);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public void processMenuDeleted(Long menuId) {
        roleMenuRepository.deleteByMenuId(menuId);
    }

    public Set<Long> getRoleMenuListByRoleId(Long roleId) {
        return getRoleMenuListByRoleId(Collections.singleton(roleId));
    }

    public Set<Long> getRoleMenuListByRoleId(Collection<Long> roleIds) {
        if (CollUtil.isEmpty(roleIds)) {
            return Collections.emptySet();
        }
        if (roleService.hasAnySuperAdmin(roleIds)) {
            return convertSet(menuService.getMenuList(), MenuDO::getId);
        }
        return getRoleMenuIds(roleIds);
    }

    @Override
    public Set<Long> getRoleMenuIds(Long roleId) {
        return roleMenuRepository.findByRoleId(roleId);
    }

    @Override
    public Set<Long> getRoleMenuIds(Collection<Long> roleIds) {
        return roleMenuRepository.findByRoleIds(roleIds);
    }

    @Cacheable(value = RedisKeyConstants.MENU_ROLE_ID_LIST, key = "#menuId")
    public Set<Long> getMenuRoleIdListByMenuIdFromCache(Long menuId) {
        return getMenuRoleIds(menuId);
    }

    @Override
    public Set<Long> getMenuRoleIds(Long menuId) {
        return roleMenuRepository.findByMenuId(menuId);
    }

    @Override
    @DSTransactional
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void assignUserRole(Long userId, Set<Long> roleIds) {
        userRoleRepository.assign(userId, roleIds);
    }

    @Override
    @CacheEvict(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public void processUserDeleted(Long userId) {
        userRoleRepository.deleteByUserId(userId);
    }

    public Set<Long> getUserRoleIdListByUserId(Long userId) {
        return getUserRoleIds(userId);
    }

    @Cacheable(value = RedisKeyConstants.USER_ROLE_ID_LIST, key = "#userId")
    public Set<Long> getUserRoleIdListByUserIdFromCache(Long userId) {
        return getUserRoleIdListByUserId(userId);
    }

    @Override
    public Set<Long> getUserRoleIds(Long userId) {
        return userRoleRepository.findByUserId(userId);
    }

    public Set<Long> getUserRoleIdListByRoleId(Collection<Long> roleIds) {
        return getUserIdsByRoleIds(roleIds);
    }

    @Override
    public Set<Long> getUserIdsByRoleIds(Collection<Long> roleIds) {
        return userRoleRepository.findByRoleIds(roleIds);
    }

    @VisibleForTesting
    List<RoleDO> getEnableUserRoleListByUserIdFromCache(Long userId) {
        Set<Long> roleIds = getSelf().getUserRoleIdListByUserIdFromCache(userId);
        List<RoleDO> roles = roleService.getRoleListFromCache(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus()));
        return roles;
    }

    public void assignRoleDataScope(Long roleId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        roleService.updateRoleDataScope(roleId, dataScope, dataScopeDeptIds);
    }

    @DataPermission(enable = false)
    public DeptDataPermissionRespDTO getDeptDataPermission(Long userId) {
        List<RoleDO> roles = getEnableUserRoleListByUserIdFromCache(userId);
        DeptDataPermissionRespDTO result = new DeptDataPermissionRespDTO();
        if (CollUtil.isEmpty(roles)) {
            result.setSelf(true);
            return result;
        }
        Supplier<Long> userDeptId = Suppliers.memoize(() -> userService.getUser(userId).getDeptId());
        for (RoleDO role : roles) {
            if (role.getDataScope() == null) {
                continue;
            }
            if (Objects.equals(role.getDataScope(), DataScopeEnum.ALL.getScope())) {
                result.setAll(true);
                continue;
            }
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_CUSTOM.getScope())) {
                CollUtil.addAll(result.getDeptIds(), role.getDataScopeDeptIds());
                CollectionUtils.addIfNotNull(result.getDeptIds(), userDeptId.get());
                continue;
            }
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_ONLY.getScope())) {
                CollectionUtils.addIfNotNull(result.getDeptIds(), userDeptId.get());
                continue;
            }
            if (Objects.equals(role.getDataScope(), DataScopeEnum.DEPT_AND_CHILD.getScope())) {
                Long deptId = userDeptId.get();
                if (deptId == null) {
                    continue;
                }
                CollUtil.addAll(result.getDeptIds(), deptService.getChildDeptIdListFromCache(deptId));
                result.getDeptIds().add(deptId);
                continue;
            }
            if (Objects.equals(role.getDataScope(), DataScopeEnum.SELF.getScope())) {
                result.setSelf(true);
                continue;
            }
            log.error("[getDeptDataPermission][LoginUser({}) role({}) 无法处理]", userId, toJsonString(result));
        }
        return result;
    }

    private PermissionApplicationService getSelf() {
        return SpringUtil.getBean(getClass());
    }
}
