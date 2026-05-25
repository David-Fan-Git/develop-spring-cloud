package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;

public interface OperateLogRepository {

    void save(OperateLog log);

    OperateLogDO findDoById(Long id);

    PageResult<OperateLogDO> findPage(OperateLogPageReqVO pageReqVO);

    PageResult<OperateLogDO> findPage(OperateLogPageReqDTO pageReqVO);

}
