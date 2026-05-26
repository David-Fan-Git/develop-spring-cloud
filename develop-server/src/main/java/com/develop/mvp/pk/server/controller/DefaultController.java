package com.develop.mvp.pk.server.controller;

import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.util.servlet.ServletUtils;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.develop.mvp.pk.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_IMPLEMENTED;

/**
 * 默认 Controller，负责在可选模块未启用时返回明确提示，避免调用方只看到 404。
 *
 * @author David
 */
@RestController
@Slf4j
public class DefaultController {

    @RequestMapping("/admin-api/bpm/**")
    public CommonResult<Boolean> bpm404() {
        return disabledModule("[工作流模块 develop-module-bpm - 已禁用][参考 https://doc.iocoder.cn/bpm/ 开启]");
    }

    @RequestMapping("/admin-api/mp/**")
    public CommonResult<Boolean> mp404() {
        return disabledModule("[微信公众号 develop-module-mp - 已禁用][参考 https://doc.iocoder.cn/mp/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/product/**", // 商品中心
            "/admin-api/trade/**", // 交易中心
            "/admin-api/promotion/**" }) // 营销中心
    public CommonResult<Boolean> mall404() {
        return disabledModule("[商城系统 develop-module-mall - 已禁用][参考 https://doc.iocoder.cn/mall/build/ 开启]");
    }

    @RequestMapping("/admin-api/erp/**")
    public CommonResult<Boolean> erp404() {
        return disabledModule("[ERP 模块 develop-module-erp - 已禁用][参考 https://doc.iocoder.cn/erp/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/wms/**" })
    public CommonResult<Boolean> wms404() {
        return disabledModule("[WMS 仓库管理系统 develop-module-wms - 已禁用][参考 https://doc.iocoder.cn/wms/build/ 开启]");
    }

    @RequestMapping("/admin-api/crm/**")
    public CommonResult<Boolean> crm404() {
        return disabledModule("[CRM 模块 develop-module-crm - 已禁用][参考 https://doc.iocoder.cn/crm/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/mes/**" })
    public CommonResult<Boolean> mes404() {
        return disabledModule("[MES 系统 develop-module-mes - 已禁用][参考 https://doc.iocoder.cn/mes/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/im/**" })
    public CommonResult<Boolean> im404() {
        return disabledModule("[IM 即时通讯 develop-module-im - 已禁用][参考 https://doc.iocoder.cn/im/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/report/**" })
    public CommonResult<Boolean> report404() {
        return disabledModule("[报表模块 develop-module-report - 已禁用][参考 https://doc.iocoder.cn/report/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/pay/**" })
    public CommonResult<Boolean> pay404() {
        return disabledModule("[支付模块 develop-module-pay - 已禁用][参考 https://doc.iocoder.cn/pay/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/ai/**" })
    public CommonResult<Boolean> ai404() {
        return disabledModule("[AI 大模型 develop-module-ai - 已禁用][参考 https://doc.iocoder.cn/ai/build/ 开启]");
    }

    @RequestMapping(value = { "/admin-api/iot/**" })
    public CommonResult<Boolean> iot404() {
        return disabledModule("[IoT 物联网 develop-module-iot - 已禁用][参考 https://doc.iocoder.cn/iot/build/ 开启]");
    }

    /**
     * 本地联调辅助接口：打印请求参数、请求头和请求体；本轮保持原行为不变。
     */
    @RequestMapping(value = { "/test" })
    @PermitAll
    public CommonResult<Boolean> test(HttpServletRequest request) {
        log.info("Query: {}", ServletUtils.getParamMap(request));
        log.info("Header: {}", ServletUtils.getHeaderMap(request));
        log.info("Body: {}", ServletUtils.getBody(request));
        return CommonResult.success(true);
    }

    private CommonResult<Boolean> disabledModule(String message) {
        return CommonResult.error(NOT_IMPLEMENTED.getCode(), message);
    }

}
