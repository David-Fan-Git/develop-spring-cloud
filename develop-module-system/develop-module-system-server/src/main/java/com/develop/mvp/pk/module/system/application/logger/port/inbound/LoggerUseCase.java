package com.develop.mvp.pk.module.system.application.logger.port.inbound;

import com.develop.mvp.pk.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.LoginLogCreateReqDTO;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;

/**
 * logger use-case boundary for legacy service compatibility and future adapters.
 */
public interface LoggerUseCase {

    LoginLogDO getLoginLog(Long id);

    PageResult<LoginLogDO> getLoginLogPage(LoginLogPageReqVO pageReqVO);

    void createLoginLog(LoginLogCreateReqDTO reqDTO);

    void createOperateLog(OperateLogCreateReqDTO createReqDTO);

    OperateLogDO getOperateLog(Long id);

    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqVO pageReqVO);

    PageResult<OperateLogDO> getOperateLogPage(OperateLogPageReqDTO pageReqVO);
}
