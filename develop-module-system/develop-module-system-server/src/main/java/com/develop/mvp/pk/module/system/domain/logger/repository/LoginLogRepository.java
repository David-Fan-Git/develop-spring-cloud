package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.loginlog.LoginLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.LoginLogDO;
import com.develop.mvp.pk.module.system.domain.logger.LoginLog;

/**
 * Login Log Repository 领域仓储接口。
 */
public interface LoginLogRepository {

    /**
     * 创建 save 对应的数据。
     *
     * @param log log 参数
     */
    void save(LoginLog log);

    /**
     * 查询 find Do By Id 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    LoginLogDO findDoById(Long id);

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<LoginLogDO> findPage(LoginLogPageReqVO pageReqVO);

}
