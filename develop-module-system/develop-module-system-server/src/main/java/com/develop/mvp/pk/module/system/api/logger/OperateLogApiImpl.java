package com.develop.mvp.pk.module.system.api.logger;

import com.develop.mvp.pk.framework.common.pojo.CommonResult;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogRespDTO;
import com.develop.mvp.pk.module.system.application.logger.port.inbound.LoggerUseCase;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static com.develop.mvp.pk.framework.common.pojo.CommonResult.success;

@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
@Primary // 由于 OperateLogCommonApi 的存在，必须声明为 @Primary Bean
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private LoggerUseCase loggerUseCase;

    @Override
    public CommonResult<Boolean> createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        loggerUseCase.createOperateLog(createReqDTO);
        return success(true);
    }

    @Override
    public CommonResult<PageResult<OperateLogRespDTO>> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        PageResult<OperateLogDO> operateLogPage = loggerUseCase.getOperateLogPage(pageReqDTO);
        return success(BeanUtils.toBean(operateLogPage, OperateLogRespDTO.class));
    }

}
