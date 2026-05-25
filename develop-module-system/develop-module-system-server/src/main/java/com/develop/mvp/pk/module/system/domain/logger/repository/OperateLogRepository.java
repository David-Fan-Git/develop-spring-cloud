package com.develop.mvp.pk.module.system.domain.logger.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;

/**
 * Operate Log Repository 领域仓储接口。
 */
public interface OperateLogRepository {

    /**
     * 创建 save 对应的数据。
     *
     * @param log log 参数
     */
    void save(OperateLog log);

    /**
     * 查询 find Do By Id 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    OperateLogDO findDoById(Long id);

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<OperateLogDO> findPage(OperateLogPageReqVO pageReqVO);

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    PageResult<OperateLogDO> findPage(OperateLogPageReqDTO pageReqVO);

}
