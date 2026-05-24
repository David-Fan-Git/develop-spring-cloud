package com.develop.mvp.pk.module.system.application.permission.service;

// Skill: AggregateRoot_Role_Menu_Skill — 应用服务 PermissionApplicationService
// DDD 角色：RBAC 权限编排服务，负责 Role/Menu CRUD 及关联操作

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.MenuUseCase;
import com.develop.mvp.pk.module.system.application.permission.port.inbound.PermissionUseCase;
import com.develop.mvp.pk.module.system.domain.permission.*;
import com.develop.mvp.pk.module.system.domain.permission.repository.*;
import com.develop.mvp.pk.module.system.domain.permission.valueobject.*;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import com.develop.mvp.pk.module.system.enums.permission.DataScopeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleTypeEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
public class PermissionApplicationService implements MenuUseCase, PermissionUseCase {

    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final DomainEventPublisher eventPublisher;

    public PermissionApplicationService(RoleRepository roleRepository, MenuRepository menuRepository,
                                         UserRoleRepository userRoleRepository, RoleMenuRepository roleMenuRepository,
                                         DomainEventPublisher eventPublisher) {
        this.roleRepository = roleRepository;
        this.menuRepository = menuRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleMenuRepository = roleMenuRepository;
        this.eventPublisher = eventPublisher;
    }

    // ========== Role CRUD ==========

    @Transactional
    public Long createRole(String name, String code, Integer sort, Integer status, String remark,
                            Long tenantId, Integer dataScope, Set<Long> dataScopeDeptIds) {
        // 规则 RR04：禁止使用 SUPER_ADMIN 编码
        if (RoleCodeEnum.isSuperAdmin(code)) throw exception(ROLE_ADMIN_CODE_ERROR, code);
        assertRoleNameUnique(name, null);
        assertRoleCodeUnique(code, null);
        Role role = RoleFactory.create(null, name, code, sort, status,
                RoleTypeEnum.CUSTOM.getType(), remark, tenantId,
                dataScope != null ? dataScope : DataScopeEnum.ALL.getScope(), dataScopeDeptIds);
        roleRepository.save(role);
        publishEvents(role);
        return role.id().value();
    }

    @Transactional
    public void updateRole(Long id, String name, String code, Integer sort, Integer status, String remark) {
        Role role = findRole(id);
        assertNotSystemRole(role);
        assertRoleNameUnique(name, id);
        assertRoleCodeUnique(code, id);
        // Update via save (Role doesn't expose mutable setters by design)
        roleRepository.save(RoleFactory.reconstitute(id, name, code, sort, status,
                role.type().code(), remark, role.tenantId(),
                role.dataScope().scope(), role.dataScope().deptIds(), role.menuIds()));
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = findRole(id);
        assertNotSystemRole(role);
        role.markDeleted();
        userRoleRepository.deleteByRoleId(id);
        roleMenuRepository.deleteByRoleId(id);
        roleRepository.delete(RoleId.of(id));
        publishEvents(role);
    }

    @Transactional
    public void deleteRoleList(List<Long> ids) { ids.forEach(this::deleteRole); }

    public Role getRole(Long id) { return roleRepository.findById(RoleId.of(id)); }

    public List<Role> getRoleList(Collection<Long> ids) {
        return roleRepository.findByIds(ids.stream().map(RoleId::of).collect(Collectors.toList()));
    }

    public List<Role> getRoleList() { return roleRepository.findAll(); }

    public PageResult<Role> getRolePage(String name, String code, Integer status,
                                         LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        return roleRepository.findPage(name, code, status, createTime, pageNo, pageSize);
    }

    // ========== Menu CRUD ==========

    @Transactional
    public Long createMenu(String name, String permission, Integer type, Integer sort, Long parentId,
                            String path, String icon, String component, String componentName,
                            Integer status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        Menu parent = parentId != null && parentId > 0 ? menuRepository.findById(MenuId.of(parentId)) : null;
        // 规则 MR01/MR02: validate parent
        Menu temp = MenuFactory.create(null, name, permission, type, sort, parentId,
                path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
        if (!temp.parentId().isRoot() && (parent == null || !parent.type().isDirOrMenu()))
            throw exception(MENU_PARENT_NOT_DIR_OR_MENU);
        assertMenuNameUnique(parentId, name, null);
        assertComponentNameUnique(componentName, null);
        menuRepository.save(temp);
        return temp.id().value();
    }

    @Transactional
    public void updateMenu(Long id, String name, String permission, Integer type, Integer sort, Long parentId,
                            String path, String icon, String component, String componentName,
                            Integer status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        if (menuRepository.findById(MenuId.of(id)) == null) throw exception(MENU_NOT_EXISTS);
        assertMenuNameUnique(parentId, name, id);
        assertComponentNameUnique(componentName, id);
        Menu updated = MenuFactory.reconstitute(id, name, permission, type, sort, parentId,
                path, icon, component, componentName, status, visible, keepAlive, alwaysShow);
        menuRepository.save(updated);
    }

    @Transactional
    public void deleteMenu(Long id) {
        if (menuRepository.countByParentId(MenuId.of(id)) > 0) throw exception(MENU_EXISTS_CHILDREN);
        if (menuRepository.findById(MenuId.of(id)) == null) throw exception(MENU_NOT_EXISTS);
        Menu menu = menuRepository.findById(MenuId.of(id));
        menu.markDeleted();
        roleMenuRepository.deleteByMenuId(id);
        menuRepository.delete(MenuId.of(id));
        if (menu != null) publishEvents(menu);
    }

    @Transactional
    public void deleteMenuList(List<Long> ids) { ids.forEach(this::deleteMenu); }

    public Menu getMenu(Long id) { return menuRepository.findById(MenuId.of(id)); }

    public List<Menu> getMenuList() { return menuRepository.findAll(); }

    public List<Menu> getMenuList(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptyList();
        return menuRepository.findByIds(ids.stream().map(MenuId::of).collect(Collectors.toList()));
    }

    // ========== User-Role Assignment ==========

    @Transactional
    public void assignUserRole(Long userId, Set<Long> roleIds) {
        userRoleRepository.assign(userId, roleIds);
    }

    @Transactional
    public void processUserDeleted(Long userId) {
        userRoleRepository.deleteByUserId(userId);
    }

    public Set<Long> getUserRoleIds(Long userId) { return userRoleRepository.findByUserId(userId); }

    public Set<Long> getUserIdsByRoleIds(Collection<Long> roleIds) { return userRoleRepository.findByRoleIds(roleIds); }

    // ========== Role-Menu Assignment ==========

    @Transactional
    public void assignRoleMenu(Long roleId, Set<Long> menuIds) {
        roleMenuRepository.assign(roleId, menuIds);
    }

    @Transactional
    public void processRoleDeleted(Long roleId) {
        userRoleRepository.deleteByRoleId(roleId);
        roleMenuRepository.deleteByRoleId(roleId);
    }

    @Transactional
    public void processMenuDeleted(Long menuId) {
        roleMenuRepository.deleteByMenuId(menuId);
    }

    public Set<Long> getRoleMenuIds(Long roleId) { return roleMenuRepository.findByRoleId(roleId); }

    public Set<Long> getRoleMenuIds(Collection<Long> roleIds) { return roleMenuRepository.findByRoleIds(roleIds); }

    public Set<Long> getMenuRoleIds(Long menuId) { return roleMenuRepository.findByMenuId(menuId); }

    // ========== helpers ==========

    private Role findRole(Long id) {
        Role r = roleRepository.findById(RoleId.of(id));
        if (r == null) throw exception(ROLE_NOT_EXISTS);
        return r;
    }

    private void assertNotSystemRole(Role role) {
        if (role.isSystem()) throw exception(ROLE_CAN_NOT_UPDATE_SYSTEM_TYPE_ROLE);
    }

    private void assertRoleNameUnique(String name, Long excludeId) {
        if (name == null || name.isBlank()) return;
        roleRepository.findByName(name).ifPresent(r -> {
            if (excludeId == null || !r.id().value().equals(excludeId))
                throw exception(ROLE_NAME_DUPLICATE, name);
        });
    }

    private void assertRoleCodeUnique(String code, Long excludeId) {
        if (code == null || code.isBlank()) return;
        roleRepository.findByCode(code).ifPresent(r -> {
            if (excludeId == null || !r.id().value().equals(excludeId))
                throw exception(ROLE_CODE_DUPLICATE, code);
        });
    }

    private void assertMenuNameUnique(Long parentId, String name, Long excludeId) {
        if (name == null || name.isBlank()) return;
        menuRepository.findByParentIdAndName(parentId != null ? parentId : 0, name).ifPresent(m -> {
            if (excludeId == null || !m.id().value().equals(excludeId))
                throw exception(MENU_NAME_DUPLICATE);
        });
    }

    private void assertComponentNameUnique(String componentName, Long excludeId) {
        if (componentName == null || componentName.isBlank()) return;
        menuRepository.findByComponentName(componentName).ifPresent(m -> {
            if (excludeId == null || !m.id().value().equals(excludeId))
                throw exception(MENU_COMPONENT_NAME_DUPLICATE);
        });
    }

    private void publishEvents(Object aggregate) {
        List<DomainEvent> events = aggregate instanceof Role r ? r.pullEvents()
                : aggregate instanceof Menu m ? m.pullEvents() : Collections.emptyList();
        events.forEach(eventPublisher::publish);
    }
}
