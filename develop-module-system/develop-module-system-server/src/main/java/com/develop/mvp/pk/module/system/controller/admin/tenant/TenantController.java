package com.develop.mvp.pk.module.system.controller.admin.tenant;

// Skill: AggregateRoot_Tenant_Validation_Skill — 适配 Controller 使用 TenantApplicationService
// DDD 角色：表示层，仅负责参数校验和 VO 转换，不包含业务逻辑

import com.develop.mvp.pk.framework.apilog.core.annotation.ApiAccessLog;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.pojo.PageParam;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.framework.excel.core.util.ExcelUtils;
import com.develop.mvp.pk.framework.tenant.core.aop.TenantIgnore;
import com.develop.mvp.pk.module.system.application.tenant.TenantApplicationService;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantRespVO;
import com.develop.mvp.pk.module.system.controller.admin.tenant.vo.tenant.TenantSaveReqVO;
import com.develop.mvp.pk.module.system.domain.tenant.Tenant;
import com.develop.mvp.pk.module.system.domain.tenant.repository.TenantPageQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.develop.mvp.pk.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 租户")
@RestController
@RequestMapping("/system/tenant")
@Validated
public class TenantController {

    @Resource
    private TenantApplicationService tenantApplicationService;

    @GetMapping("/get-id-by-name")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用租户名，获得租户编号", description = "登录界面，根据用户的租户名，获得租户编号")
    @Parameter(name = "name", description = "租户名", required = true, example = "1024")
    public CommonResult<Long> getTenantIdByName(@RequestParam("name") String name) {
        Tenant tenant = tenantApplicationService.getTenantByName(name);
        return success(tenant != null ? tenant.id().value() : null);
    }

    @GetMapping("simple-list")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "获取租户精简信息列表", description = "只包含被开启的租户，用于【首页】功能的选择租户选项")
    public CommonResult<List<TenantRespVO>> getTenantSimpleList() {
        List<Tenant> list = tenantApplicationService.getTenantListByStatus(
                CommonStatusEnum.ENABLE.getStatus());
        return success(list.stream()
                .map(t -> new TenantRespVO().setId(t.id().value()).setName(t.name().value()))
                .collect(Collectors.toList()));
    }

    @GetMapping("/get-by-website")
    @PermitAll
    @TenantIgnore
    @Operation(summary = "使用域名，获得租户信息", description = "登录界面，根据用户的域名，获得租户信息")
    @Parameter(name = "website", description = "域名", required = true, example = "www.iocoder.cn")
    public CommonResult<TenantRespVO> getTenantByWebsite(
            @RequestParam("website") @Pattern(regexp = "^[a-zA-Z0-9.-]+(:\\d{1,5})?$", message = "网站域名格式不正确") String website) {
        Tenant tenant = tenantApplicationService.getTenantByWebsite(website);
        if (tenant == null || tenant.isDisabled()) {
            return success(null);
        }
        return success(new TenantRespVO().setId(tenant.id().value()).setName(tenant.name().value()));
    }

    @PostMapping("/create")
    @Operation(summary = "创建租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:create')")
    public CommonResult<Long> createTenant(@Valid @RequestBody TenantSaveReqVO createReqVO) {
        Long nextId = createReqVO.getId(); // 由数据库自增或指定
        return success(tenantApplicationService.createTenant(
                nextId, createReqVO.getName(), createReqVO.getContactName(),
                createReqVO.getContactMobile(), createReqVO.getStatus(),
                createReqVO.getWebsites(), createReqVO.getPackageId(),
                createReqVO.getExpireTime(), createReqVO.getAccountCount(),
                createReqVO.getUsername(), createReqVO.getPassword()));
    }

    @PutMapping("/update")
    @Operation(summary = "更新租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:update')")
    public CommonResult<Boolean> updateTenant(@Valid @RequestBody TenantSaveReqVO updateReqVO) {
        tenantApplicationService.updateTenant(
                updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getContactName(),
                updateReqVO.getContactMobile(), updateReqVO.getStatus(),
                updateReqVO.getWebsites(), updateReqVO.getPackageId(),
                updateReqVO.getExpireTime(), updateReqVO.getAccountCount());
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenant(@RequestParam("id") Long id) {
        tenantApplicationService.deleteTenant(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号列表", required = true)
    @Operation(summary = "批量删除租户")
    @PreAuthorize("@ss.hasPermission('system:tenant:delete')")
    public CommonResult<Boolean> deleteTenantList(@RequestParam("ids") List<Long> ids) {
        tenantApplicationService.deleteTenantList(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得租户")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<TenantRespVO> getTenant(@RequestParam("id") Long id) {
        Tenant tenant = tenantApplicationService.getTenant(id);
        return success(tenant != null ? toRespVO(tenant) : null);
    }

    @GetMapping("/page")
    @Operation(summary = "获得租户分页")
    @PreAuthorize("@ss.hasPermission('system:tenant:query')")
    public CommonResult<PageResult<TenantRespVO>> getTenantPage(@Valid TenantPageReqVO pageVO) {
        TenantPageQuery query = new TenantPageQuery(
                pageVO.getName(), pageVO.getContactName(), pageVO.getContactMobile(),
                pageVO.getStatus(), pageVO.getCreateTime(),
                pageVO.getPageNo(), pageVO.getPageSize());
        PageResult<Tenant> pageResult = tenantApplicationService.getTenantPage(query);
        List<TenantRespVO> voList = pageResult.getList().stream()
                .map(this::toRespVO).collect(Collectors.toList());
        return success(new PageResult<>(voList, pageResult.getTotal()));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出租户 Excel")
    @PreAuthorize("@ss.hasPermission('system:tenant:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportTenantExcel(@Valid TenantPageReqVO exportReqVO, HttpServletResponse response) throws IOException {
        exportReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        TenantPageQuery query = new TenantPageQuery(
                exportReqVO.getName(), exportReqVO.getContactName(), exportReqVO.getContactMobile(),
                exportReqVO.getStatus(), exportReqVO.getCreateTime(), null, null);
        List<Tenant> list = tenantApplicationService.getTenantPage(query).getList();
        List<TenantRespVO> voList = list.stream().map(this::toRespVO).collect(Collectors.toList());
        ExcelUtils.write(response, "租户.xls", "数据", TenantRespVO.class, voList);
    }

    private TenantRespVO toRespVO(Tenant tenant) {
        TenantRespVO vo = new TenantRespVO();
        vo.setId(tenant.id().value());
        vo.setName(tenant.name().value());
        vo.setContactName(tenant.contactName());
        vo.setContactMobile(tenant.contactMobile());
        vo.setStatus(tenant.status().code());
        vo.setWebsites(tenant.websites());
        vo.setPackageId(tenant.packageRef().packageId());
        vo.setExpireTime(tenant.expireTime().value());
        vo.setAccountCount(tenant.accountCount());
        return vo;
    }
}
