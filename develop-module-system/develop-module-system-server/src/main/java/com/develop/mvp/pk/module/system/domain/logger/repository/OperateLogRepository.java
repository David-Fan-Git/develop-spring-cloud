package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;

public interface OperateLogRepository {
    void save(OperateLog log);
    OperateLog findById(Long id);
    PageResult<OperateLog> findPage(String module, String name, Integer type, Long userId, Integer resultCode, Integer pageNo, Integer pageSize);
}
