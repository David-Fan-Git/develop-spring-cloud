package com.develop.mvp.pk.module.system.api.tenant;

// Skill: AggregateRoot_Tenant_Validation_Skill — 适配 TenantApiImpl 使用 TenantUseCase 入站端口

import com.develop.mvp.pk.framework.common.biz.system.tenant.TenantCommonApi;
import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.tenant.core.aop.TenantIgnore;
import com.develop.mvp.pk.module.system.application.tenant.port.inbound.TenantUseCase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class TenantApiImpl implements TenantCommonApi {

    @Resource
    private TenantUseCase tenantUseCase;

    @Override
    @TenantIgnore
    public CommonResult<List<Long>> getTenantIdList() {
        return success(tenantUseCase.getTenantIdList());
    }

    @Override
    @TenantIgnore
    public CommonResult<Boolean> validTenant(Long id) {
        tenantUseCase.getAndValidateTenant(id);
        return success(true);
    }

}
