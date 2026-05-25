package com.develop.mvp.pk.module.system.infrastructure.logger.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import com.develop.mvp.pk.module.system.dal.mysql.logger.OperateLogMapper;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;
import com.develop.mvp.pk.module.system.domain.logger.repository.OperateLogRepository;
import org.springframework.stereotype.Repository;

/**
 * Operate Log Repository Impl 领域仓储实现。
 */
@Repository
public class OperateLogRepositoryImpl implements OperateLogRepository {

    private final OperateLogMapper operateLogMapper;

    /**
     * 创建 OperateLogRepositoryImpl 实例。
     *
     * @param operateLogMapper operateLogMapper 参数
     */
    public OperateLogRepositoryImpl(OperateLogMapper operateLogMapper) {
        this.operateLogMapper = operateLogMapper;
    }

    /**
     * 创建 save 对应的数据。
     *
     * @param log log 参数
     */
    @Override
    public void save(OperateLog log) {
        operateLogMapper.insert(toDataObject(log));
    }

    /**
     * 查询 find Do By Id 对应的数据。
     *
     * @param id id 参数
     * @return 处理结果
     */
    @Override
    public OperateLogDO findDoById(Long id) {
        return operateLogMapper.selectById(id);
    }

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    @Override
    public PageResult<OperateLogDO> findPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    /**
     * 查询 find Page 对应的数据。
     *
     * @param pageReqVO pageReqVO 参数
     * @return 处理结果
     */
    @Override
    public PageResult<OperateLogDO> findPage(OperateLogPageReqDTO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    /**
     * 执行 to Data Object 对应的业务操作。
     *
     * @param log log 参数
     * @return 处理结果
     */
    private OperateLogDO toDataObject(OperateLog log) {
        OperateLogDO operateLogDO = new OperateLogDO();
        operateLogDO.setId(log.id());
        operateLogDO.setTraceId(log.traceId());
        operateLogDO.setUserId(log.userId());
        operateLogDO.setUserType(log.userType());
        operateLogDO.setType(log.type());
        operateLogDO.setSubType(log.subType());
        operateLogDO.setBizId(log.bizId());
        operateLogDO.setAction(log.action());
        operateLogDO.setExtra(log.extra());
        operateLogDO.setRequestMethod(log.requestMethod());
        operateLogDO.setRequestUrl(log.requestUrl());
        operateLogDO.setUserIp(log.userIp());
        operateLogDO.setUserAgent(log.userAgent());
        return operateLogDO;
    }
}
