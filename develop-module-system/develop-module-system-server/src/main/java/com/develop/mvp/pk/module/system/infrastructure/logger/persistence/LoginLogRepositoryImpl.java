package com.develop.mvp.pk.module.system.infrastructure.logger.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.dal.mysql.logger.LoginLogMapper;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;
import com.develop.mvp.pk.module.system.domain.logger.repository.LoginLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LoginLogRepositoryImpl implements LoginLogRepository {

    private final LoginLogMapper loginLogMapper;

    public LoginLogRepositoryImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public void save(LoginLog log) {
        loginLogMapper.insert(toDataObject(log));
    }

    @Override
    public LoginLogDO findDoById(Long id) {
        return loginLogMapper.selectById(id);
    }

    @Override
    public PageResult<LoginLogDO> findPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    private LoginLogDO toDataObject(LoginLog log) {
        LoginLogDO loginLogDO = new LoginLogDO();
        loginLogDO.setId(log.id());
        loginLogDO.setLogType(log.logType());
        loginLogDO.setTraceId(log.traceId());
        loginLogDO.setUserId(log.userId());
        loginLogDO.setUserType(log.userType());
        loginLogDO.setUsername(log.username());
        loginLogDO.setResult(log.result());
        loginLogDO.setUserIp(log.userIp());
        loginLogDO.setUserAgent(log.userAgent());
        return loginLogDO;
    }
}
