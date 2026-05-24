package com.develop.mvp.pk.module.system.service.tenant;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.framework.datapermission.core.annotation.DataPermission;
import com.develop.mvp.pk.framework.tenant.config.TenantProperties;
import com.develop.mvp.pk.framework.tenant.core.context.TenantContextHolder;
import com.develop.mvp.pk.module.system.application.tenant.service.TenantApplicationService;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.permission.MenuDO;
import com.develop.mvp.pk.module.system.dal.dataobject.tenant.TenantDO;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantPageQuery;
import com.develop.mvp.pk.module.system.service.permission.MenuService;
import com.develop.mvp.pk.module.system.service.tenant.handler.TenantInfoHandler;
import com.develop.mvp.pk.module.system.service.tenant.handler.TenantMenuHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 租户兼容 Service，业务用例委托给 TenantApplicationService。
 */
@Service
@Validated
@Slf4j
public class TenantServiceImpl implements TenantService {

    @SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private TenantProperties tenantProperties;

    @Resource
    private TenantApplicationService tenantApplicationService;
    @Resource
    private TenantPackageService tenantPackageService;
    @Resource
    private MenuService menuService;

    @Override
    public List<Long> getTenantIdList() {
        return tenantApplicationService.getTenantIdList();
    }

    @Override
    public void validTenant(Long id) {
        tenantApplicationService.getAndValidateTenant(id);
    }

    @Override
    @DSTransactional
    @DataPermission(enable = false)
    public Long createTenant(TenantSaveReqVO createReqVO) {
        return tenantApplicationService.createTenant(
                createReqVO.getId(), createReqVO.getName(), createReqVO.getContactName(),
                createReqVO.getContactMobile(), createReqVO.getStatus(),
                createReqVO.getWebsites(), createReqVO.getPackageId(),
                createReqVO.getExpireTime(), createReqVO.getAccountCount(),
                createReqVO.getUsername(), createReqVO.getPassword());
    }

    @Override
    @DSTransactional
    public void updateTenant(TenantSaveReqVO updateReqVO) {
        tenantApplicationService.updateTenant(
                updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getContactName(),
                updateReqVO.getContactMobile(), updateReqVO.getStatus(),
                updateReqVO.getWebsites(), updateReqVO.getPackageId(),
                updateReqVO.getExpireTime(), updateReqVO.getAccountCount());
    }

    @Override
    @DSTransactional
    public void updateTenantRoleMenu(Long tenantId, Set<Long> menuIds) {
        tenantApplicationService.updateTenantRoleMenu(tenantId, menuIds);
    }

    @Override
    public void deleteTenant(Long id) {
        tenantApplicationService.deleteTenant(id);
    }

    @Override
    public void deleteTenantList(List<Long> ids) {
        tenantApplicationService.deleteTenantList(ids);
    }

    @Override
    public TenantDO getTenant(Long id) {
        return toDataObject(tenantApplicationService.getTenant(id));
    }

    @Override
    public PageResult<TenantDO> getTenantPage(TenantPageReqVO pageReqVO) {
        TenantPageQuery query = new TenantPageQuery(
                pageReqVO.getName(), pageReqVO.getContactName(), pageReqVO.getContactMobile(),
                pageReqVO.getStatus(), pageReqVO.getCreateTime(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
        PageResult<Tenant> pageResult = tenantApplicationService.getTenantPage(query);
        List<TenantDO> tenants = pageResult.getList().stream()
                .map(this::toDataObject)
                .collect(Collectors.toList());
        return new PageResult<>(tenants, pageResult.getTotal());
    }

    @Override
    public TenantDO getTenantByName(String name) {
        return toDataObject(tenantApplicationService.getTenantByName(name));
    }

    @Override
    public TenantDO getTenantByWebsite(String website) {
        return toDataObject(tenantApplicationService.getTenantByWebsite(website));
    }

    @Override
    public Long getTenantCountByPackageId(Long packageId) {
        return tenantApplicationService.getTenantCountByPackageId(packageId);
    }

    @Override
    public List<TenantDO> getTenantListByPackageId(Long packageId) {
        return tenantApplicationService.getTenantListByPackageId(packageId).stream()
                .map(this::toDataObject)
                .collect(Collectors.toList());
    }

    @Override
    public List<TenantDO> getTenantListByStatus(Integer status) {
        return tenantApplicationService.getTenantListByStatus(status).stream()
                .map(this::toDataObject)
                .collect(Collectors.toList());
    }

    @Override
    public void handleTenantInfo(TenantInfoHandler handler) {
        if (isTenantDisable()) {
            return;
        }
        handler.handle(getTenant(TenantContextHolder.getRequiredTenantId()));
    }

    @Override
    public void handleTenantMenu(TenantMenuHandler handler) {
        if (isTenantDisable()) {
            return;
        }
        TenantDO tenant = getTenant(TenantContextHolder.getRequiredTenantId());
        Set<Long> menuIds;
        if (isSystemTenant(tenant)) {
            menuIds = CollectionUtils.convertSet(menuService.getMenuList(), MenuDO::getId);
        } else {
            menuIds = tenantPackageService.getTenantPackage(tenant.getPackageId()).getMenuIds();
        }
        handler.handle(menuIds);
    }

    private static boolean isSystemTenant(TenantDO tenant) {
        return Objects.equals(tenant.getPackageId(), TenantDO.PACKAGE_ID_SYSTEM);
    }

    private boolean isTenantDisable() {
        return tenantProperties == null || Boolean.FALSE.equals(tenantProperties.getEnable());
    }

    private TenantDO toDataObject(Tenant tenant) {
        if (tenant == null) {
            return null;
        }
        TenantDO tenantDO = new TenantDO()
                .setId(tenant.id().value())
                .setName(tenant.name().value())
                .setContactUserId(tenant.contactUserId())
                .setContactName(tenant.contactName())
                .setContactMobile(tenant.contactMobile())
                .setStatus(tenant.status().code())
                .setWebsites(tenant.websites())
                .setPackageId(tenant.packageRef().packageId())
                .setExpireTime(tenant.expireTime().value())
                .setAccountCount(tenant.accountCount());
        tenantDO.setCreateTime(tenant.createTime());
        tenantDO.setUpdateTime(tenant.updateTime());
        tenantDO.setCreator(tenant.creator());
        tenantDO.setUpdater(tenant.updater());
        tenantDO.setDeleted(tenant.deleted());
        return tenantDO;
    }
}
