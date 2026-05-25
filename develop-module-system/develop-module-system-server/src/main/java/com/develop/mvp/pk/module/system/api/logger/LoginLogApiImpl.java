package com.develop.mvp.pk.module.system.api.logger;

import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.develop.mvp.pk.module.system.application.logger.port.inbound.LoggerUseCase;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class LoginLogApiImpl implements LoginLogApi {

    @Resource
    private LoggerUseCase loggerUseCase;

    @Override
    public CommonResult<Boolean> createLoginLog(LoginLogCreateReqDTO reqDTO) {
        loggerUseCase.createLoginLog(reqDTO);
        return success(true);
    }

}
