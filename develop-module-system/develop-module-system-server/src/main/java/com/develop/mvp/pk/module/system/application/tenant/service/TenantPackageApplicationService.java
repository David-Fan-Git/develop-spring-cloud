package com.develop.mvp.pk.module.system.application.tenant.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantPackageUseCase;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.packages.TenantPackageSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.tenant.TenantPackageDO;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.dal.mysql.tenant.TenantPackageMapper;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantUseCase;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Validated
public class TenantPackageApplicationService implements TenantPackageUseCase {

    private final TenantPackageMapper tenantPackageMapper;
    private final TenantUseCase tenantUseCase;

    public TenantPackageApplicationService(TenantPackageMapper tenantPackageMapper,
                                           @Lazy TenantUseCase tenantUseCase) {
        this.tenantPackageMapper = tenantPackageMapper;
        this.tenantUseCase = tenantUseCase;
    }

    public Long createTenantPackage(TenantPackageSaveReqVO createReqVO) {
        validateTenantPackageNameUnique(null, createReqVO.getName());
        TenantPackageDO tenantPackage = BeanUtils.toBean(createReqVO, TenantPackageDO.class);
        tenantPackageMapper.insert(tenantPackage);
        return tenantPackage.getId();
    }

    @DSTransactional
    public void updateTenantPackage(TenantPackageSaveReqVO updateReqVO) {
        TenantPackageDO tenantPackage = validateTenantPackageExists(updateReqVO.getId());
        validateTenantPackageNameUnique(updateReqVO.getId(), updateReqVO.getName());
        TenantPackageDO updateObj = BeanUtils.toBean(updateReqVO, TenantPackageDO.class);
        tenantPackageMapper.updateById(updateObj);
        if (!CollUtil.isEqualList(tenantPackage.getMenuIds(), updateReqVO.getMenuIds())) {
            List<Tenant> tenants = tenantUseCase.getTenantDomainListByPackageId(updateReqVO.getId());
            tenants.forEach(tenant -> tenantUseCase.updateTenantRoleMenu(tenant.id().value(), updateReqVO.getMenuIds()));
        }
    }

    public void deleteTenantPackage(Long id) {
        validateTenantPackageExists(id);
        validateTenantUsed(id);
        tenantPackageMapper.deleteById(id);
    }

    public void deleteTenantPackageList(List<Long> ids) {
        for (Long id : ids) {
            if (tenantUseCase.getTenantCountByPackageId(id) > 0) {
                throw exception(TENANT_PACKAGE_USED);
            }
        }
        tenantPackageMapper.deleteByIds(ids);
    }

    public TenantPackageDO getTenantPackage(Long id) {
        return tenantPackageMapper.selectById(id);
    }

    public PageResult<TenantPackageDO> getTenantPackagePage(TenantPackagePageReqVO pageReqVO) {
        return tenantPackageMapper.selectPage(pageReqVO);
    }

    public TenantPackageDO validTenantPackage(Long id) {
        TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
        if (tenantPackage.getStatus().equals(CommonStatusEnum.DISABLE.getStatus())) {
            throw exception(TENANT_PACKAGE_DISABLE, tenantPackage.getName());
        }
        return tenantPackage;
    }

    public List<TenantPackageDO> getTenantPackageListByStatus(Integer status) {
        return tenantPackageMapper.selectListByStatus(status);
    }

    private TenantPackageDO validateTenantPackageExists(Long id) {
        TenantPackageDO tenantPackage = tenantPackageMapper.selectById(id);
        if (tenantPackage == null) {
            throw exception(TENANT_PACKAGE_NOT_EXISTS);
        }
        return tenantPackage;
    }

    private void validateTenantUsed(Long id) {
        if (tenantUseCase.getTenantCountByPackageId(id) > 0) {
            throw exception(TENANT_PACKAGE_USED);
        }
    }

    @VisibleForTesting
    public void validateTenantPackageNameUnique(Long id, String name) {
        if (StrUtil.isBlank(name)) {
            return;
        }
        TenantPackageDO tenantPackage = tenantPackageMapper.selectByName(name);
        if (tenantPackage == null) {
            return;
        }
        if (id == null) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
        if (!tenantPackage.getId().equals(id)) {
            throw exception(TENANT_PACKAGE_NAME_DUPLICATE);
        }
    }
}
