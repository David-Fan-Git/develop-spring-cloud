package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;

public interface LoginLogRepository {
    void save(LoginLog log);
    LoginLog findById(Long id);
    PageResult<LoginLog> findPage(String userIp, String username, Integer status, Integer result, Integer pageNo, Integer pageSize);
}
