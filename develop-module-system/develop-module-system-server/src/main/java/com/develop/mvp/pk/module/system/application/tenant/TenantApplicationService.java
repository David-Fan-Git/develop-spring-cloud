package com.develop.mvp.pk.module.system.application.tenant;

// Skill: AggregateRoot_Tenant_Validation_Skill — 应用服务 TenantApplicationService
// DDD 角色：应用编排服务，不包含业务规则，仅编排领域对象和基础设施
// 规则 R04/不变式 I04：系统租户不可修改/删除，由本层校验
// 验收标准 AC09：创建租户时的角色+用户创建编排逻辑在 ApplicationService 中

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.tenant.core.util.TenantUtils;
import com.develop.mvp.pk.module.system.controller.admin.permission.vo.role.RoleSaveReqVO;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.TenantFactory;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEvent;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantPageQuery;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantRepository;
import com.develop.mvp.pk.module.system.domain.tenant.service.TenantUniquenessChecker;
import com.develop.mvp.pk.module.system.domain.tenant.valueobject.*;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import com.develop.mvp.pk.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.develop.mvp.pk.module.system.enums.permission.RoleCodeEnum;
import com.develop.mvp.pk.module.system.enums.permission.RoleTypeEnum;
import com.develop.mvp.pk.module.system.service.permission.PermissionService;
import com.develop.mvp.pk.module.system.service.permission.RoleService;
import com.develop.mvp.pk.module.system.service.tenant.TenantPackageService;
import com.develop.mvp.pk.module.system.service.user.AdminUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;
import static java.util.Collections.singleton;

@Service
public class TenantApplicationService {

    private final TenantRepository tenantRepository;
    private final TenantUniquenessChecker uniquenessChecker;
    private final DomainEventPublisher eventPublisher;
    private final TenantPackageService tenantPackageService;
    private final AdminUserService adminUserService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    public TenantApplicationService(TenantRepository tenantRepository,
                                     TenantUniquenessChecker uniquenessChecker,
                                     DomainEventPublisher eventPublisher,
                                     TenantPackageService tenantPackageService,
                                     AdminUserService adminUserService,
                                     RoleService roleService,
                                     PermissionService permissionService) {
        this.tenantRepository = tenantRepository;
        this.uniquenessChecker = uniquenessChecker;
        this.eventPublisher = eventPublisher;
        this.tenantPackageService = tenantPackageService;
        this.adminUserService = adminUserService;
        this.roleService = roleService;
        this.permissionService = permissionService;
    }

    /**
     * 创建租户（含管理员角色+用户创建的编排逻辑）
     * 规则 R02/R03：名称/域名唯一性校验
     * 不变式 I06：套餐必须存在且启用
     */
    @Transactional
    public Long createTenant(Long id, String name, String contactName, String contactMobile,
                              Integer status, List<String> websites, Long packageId,
                              java.time.LocalDateTime expireTime, Integer accountCount,
                              String username, String password) {
        // 校验唯一性（规则 R02/R03）
        assertNameUnique(TenantName.of(name), null);
        if (CollUtil.isNotEmpty(websites)) {
            for (String website : websites) {
                assertWebsiteUnique(website, null);
            }
        }
        // 校验套餐（不变式 I06）
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(packageId);

        // 创建租户聚合（规则 R01：默认 ENABLED）
        Tenant tenant = TenantFactory.create(id, name, null, contactName, contactMobile,
                websites, packageId, expireTime, accountCount);
        tenantRepository.save(tenant);

        // 跨聚合编排：创建管理员角色和用户
        TenantUtils.execute(tenant.id().value(), () -> {
            Long roleId = createTenantAdminRole(tenantPackage);
            Long userId = createAdminUser(roleId, username, password, contactName, contactMobile);
            tenant.setContactUser(userId);
            tenantRepository.save(tenant);
        });

        publishEvents(tenant);
        return tenant.id().value();
    }

    /**
     * 更新租户
     * 规则 R09：套餐变更时同步更新
     * 规则 R04/不变式 I04：系统租户不可修改
     */
    @Transactional
    public void updateTenant(Long id, String name, String contactName, String contactMobile,
                              Integer status, List<String> websites, Long packageId,
                              java.time.LocalDateTime expireTime, Integer accountCount) {
        Tenant tenant = findExistingTenant(TenantId.of(id));
        // 不变式 I04：系统租户不可修改
        assertNotSystemTenant(tenant);

        // 校验唯一性
        assertNameUnique(TenantName.of(name), tenant.id());
        if (CollUtil.isNotEmpty(websites)) {
            for (String website : websites) {
                assertWebsiteUnique(website, tenant.id());
            }
        }
        // 校验套餐
        TenantPackageDO tenantPackage = tenantPackageService.validTenantPackage(packageId);

        // 更新租户属性
        tenant.updateProfile(TenantName.of(name), contactName, contactMobile, websites, uniquenessChecker);
        if (status != null) {
            if (TenantStatus.of(status).isEnabled()) tenant.enable();
            else tenant.disable();
        }
        tenant.updateExpiration(TenantExpireTime.of(expireTime), accountCount);

        // 规则 R09：套餐变更时同步角色权限
        if (!tenant.packageRef().packageId().equals(packageId)) {
            tenant.changePackage(TenantPackageRef.of(packageId));
            updateTenantRoleMenu(tenant.id().value(), tenantPackage.getMenuIds());
        }

        tenantRepository.save(tenant);
        publishEvents(tenant);
    }

    /** 删除租户（规则 R04：系统租户不可删除） */
    @Transactional
    public void deleteTenant(Long id) {
        Tenant tenant = findExistingTenant(TenantId.of(id));
        assertNotSystemTenant(tenant);
        tenant.markDeleted();
        tenantRepository.delete(tenant.id());
        publishEvents(tenant);
    }

    /** 批量删除租户 */
    @Transactional
    public void deleteTenantList(List<Long> ids) {
        for (Long id : ids) {
            deleteTenant(id);
        }
    }

    // ── 查询 ──

    public Tenant getTenant(Long id) {
        return tenantRepository.findById(TenantId.of(id));
    }

    /** 规则 R07：校验租户有效性（存在+启用+未过期） */
    public Tenant getAndValidateTenant(Long id) {
        Tenant tenant = getTenant(id);
        if (tenant == null) throw exception(TENANT_NOT_EXISTS);
        String error = tenant.validateActive();
        if (error != null) {
            if (tenant.isDisabled()) throw exception(TENANT_DISABLE, tenant.name().value());
            if (tenant.isExpired()) throw exception(TENANT_EXPIRE, tenant.name().value());
        }
        return tenant;
    }

    public Tenant getTenantByName(String name) {
        return tenantRepository.findByName(TenantName.of(name)).orElse(null);
    }

    public Tenant getTenantByWebsite(String website) {
        List<Tenant> tenants = tenantRepository.findByWebsite(website);
        return tenants.isEmpty() ? null : tenants.get(0);
    }

    public PageResult<Tenant> getTenantPage(TenantPageQuery query) {
        return tenantRepository.findPage(query);
    }

    public List<Tenant> getTenantListByStatus(Integer statusCode) {
        return tenantRepository.findByStatus(TenantStatus.of(statusCode));
    }

    public Long getTenantCountByPackageId(Long packageId) {
        return tenantRepository.countByPackageId(TenantPackageRef.of(packageId));
    }

    public List<Tenant> getTenantListByPackageId(Long packageId) {
        return tenantRepository.findByPackageId(TenantPackageRef.of(packageId));
    }

    public List<Long> getTenantIdList() {
        return tenantRepository.findAll().stream()
                .map(t -> t.id().value()).collect(Collectors.toList());
    }

    // ── 私有方法 ──

    private Tenant findExistingTenant(TenantId id) {
        Tenant tenant = tenantRepository.findById(id);
        if (tenant == null) throw exception(TENANT_NOT_EXISTS);
        return tenant;
    }

    private void assertNotSystemTenant(Tenant tenant) {
        if (tenant.isSystem()) throw exception(TENANT_CAN_NOT_UPDATE_SYSTEM);
    }

    private void assertNameUnique(TenantName name, TenantId excludeId) {
        if (!uniquenessChecker.isNameUnique(name, excludeId)) {
            throw exception(TENANT_NAME_DUPLICATE, name.value());
        }
    }

    private void assertWebsiteUnique(String website, TenantId excludeId) {
        if (!uniquenessChecker.isWebsiteUnique(website, excludeId)) {
            throw exception(TENANT_WEBSITE_DUPLICATE, website);
        }
    }

    // ── 跨聚合编排：角色+用户创建（后续 Role/User 完成 DDD 重构后可替换为 ApplicationService 调用） ──

    private Long createTenantAdminRole(TenantPackageDO tenantPackage) {
        RoleSaveReqVO reqVO = new RoleSaveReqVO();
        reqVO.setName(RoleCodeEnum.TENANT_ADMIN.getName())
                .setCode(RoleCodeEnum.TENANT_ADMIN.getCode())
                .setSort(0).setRemark("系统自动生成");
        Long roleId = roleService.createRole(reqVO, RoleTypeEnum.SYSTEM.getType());
        permissionService.assignRoleMenu(roleId, tenantPackage.getMenuIds());
        return roleId;
    }

    private Long createAdminUser(Long roleId, String username, String password,
                                  String contactName, String contactMobile) {
        Long userId = adminUserService.createUser(
                convertToUserSaveReqVO(username, password, contactName, contactMobile));
        permissionService.assignUserRole(userId, singleton(roleId));
        return userId;
    }

    private static com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserSaveReqVO
            convertToUserSaveReqVO(String username, String password, String contactName, String contactMobile) {
        var reqVO = new com.develop.mvp.pk.module.system.controller.admin.user.vo.user.UserSaveReqVO();
        reqVO.setUsername(username);
        reqVO.setPassword(password);
        reqVO.setNickname(contactName);
        reqVO.setMobile(contactMobile);
        return reqVO;
    }

    private void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        TenantUtils.execute(tenantId, () -> {
            roleService.getRoleList().forEach(role -> {
                if (Objects.equals(role.getCode(), RoleCodeEnum.TENANT_ADMIN.getCode())) {
                    permissionService.assignRoleMenu(role.getId(), menuIds);
                } else {
                    Set<Long> roleMenuIds = permissionService.getRoleMenuListByRoleId(role.getId());
                    permissionService.assignRoleMenu(role.getId(),
                            CollUtil.intersectionDistinct(roleMenuIds, menuIds));
                }
            });
        });
    }

    private void publishEvents(Tenant tenant) {
        for (DomainEvent event : tenant.pullEvents()) {
            eventPublisher.publish(event);
        }
    }
}
