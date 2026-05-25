package com.develop.mvp.pk.module.system.application.tenant.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.packages.TenantPackagePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.packages.TenantPackageSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.tenant.TenantPackageDO;

import java.util.List;

/**
 * tenant package use-case boundary for legacy service compatibility and future adapters.
 */
public interface TenantPackageUseCase {

    Long createTenantPackage(TenantPackageSaveReqVO createReqVO);

    void updateTenantPackage(TenantPackageSaveReqVO updateReqVO);

    void deleteTenantPackage(Long id);

    void deleteTenantPackageList(List<Long> ids);

    TenantPackageDO getTenantPackage(Long id);

    PageResult<TenantPackageDO> getTenantPackagePage(TenantPackagePageReqVO pageReqVO);

    TenantPackageDO validTenantPackage(Long id);

    List<TenantPackageDO> getTenantPackageListByStatus(Integer status);

    void validateTenantPackageNameUnique(Long id, String name);
}
