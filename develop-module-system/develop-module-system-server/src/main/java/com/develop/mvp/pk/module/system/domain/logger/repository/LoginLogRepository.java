package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;

public interface LoginLogRepository {

    void save(LoginLog log);

    LoginLogDO findDoById(Long id);

    PageResult<LoginLogDO> findPage(LoginLogPageReqVO pageReqVO);

}
