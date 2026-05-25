package com.develop.mvp.pk.module.system.infrastructure.logger.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.dal.mysql.logger.LoginLogMapper;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;
import com.develop.mvp.pk.module.system.domain.logger.repository.LoginLogRepository;
import org.springframework.stereotype.Repository;

/**
 * Login Log Repository Impl 领域仓储实现。
 */
@Repository
public class LoginLogRepositoryImpl implements LoginLogRepository {

    private final LoginLogMapper loginLogMapper;

    /**
     * 创建 LoginLogRepositoryImpl 实例。
     *
     * @param loginLogMapper loginLogMapper 参数
     */
    public LoginLogRepositoryImpl(LoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    /**
     * 创建 save 对应的数据。
     *
     * @param log log 参数
     */
    @Override
    public void save(LoginLog log) {
        loginLogMapper.insert(toDataObject(log));
    }

    /**
     * 查询 find Do By Id 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    @Override
    public LoginLogDO findDoById(Long id) {
        return loginLogMapper.selectById(id);
    }

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    @Override
    public PageResult<LoginLogDO> findPage(LoginLogPageReqVO pageReqVO) {
        return loginLogMapper.selectPage(pageReqVO);
    }

    /**
     * 执行 to Data Object 对应的业务操作。
     *
     * @param log log 参数
     * @return 处理结果
     */
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
