package com.develop.mvp.pk.module.system.infrastructure.logger.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.logger.dto.OperateLogPageReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.logger.vo.operatelog.OperateLogPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.logger.OperateLogDO;
import com.develop.mvp.pk.module.system.dal.mysql.logger.OperateLogMapper;
import com.develop.mvp.pk.module.system.domain.logger.OperateLog;
import com.develop.mvp.pk.module.system.domain.logger.repository.OperateLogRepository;
import org.springframework.stereotype.Repository;

@Repository
public class OperateLogRepositoryImpl implements OperateLogRepository {

    private final OperateLogMapper operateLogMapper;

    public OperateLogRepositoryImpl(OperateLogMapper operateLogMapper) {
        this.operateLogMapper = operateLogMapper;
    }

    @Override
    public void save(OperateLog log) {
        operateLogMapper.insert(toDataObject(log));
    }

    @Override
    public OperateLogDO findDoById(Long id) {
        return operateLogMapper.selectById(id);
    }

    @Override
    public PageResult<OperateLogDO> findPage(OperateLogPageReqVO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<OperateLogDO> findPage(OperateLogPageReqDTO pageReqVO) {
        return operateLogMapper.selectPage(pageReqVO);
    }

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
