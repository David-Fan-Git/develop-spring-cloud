package com.develop.mvp.pk.module.infra.application.logger;

// DDD 角色：应用编排服务 - API 访问日志

import com.develop.mvp.pk.framework.common.biz.infra.logger.dto.ApiAccessLogCreateReqDTO;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.framework.common.util.string.StrUtils;
import com.develop.mvp.pk.framework.tenant.core.context.TenantContextHolder;
import com.develop.mvp.pk.framework.tenant.core.util.TenantUtils;
import com.develop.mvp.pk.module.infra.dal.dataobject.logger.ApiAccessLogDO;
import com.develop.mvp.pk.module.infra.domain.event.DomainEventPublisher;
import com.develop.mvp.pk.module.infra.domain.logger.ApiAccessLog;
import com.develop.mvp.pk.module.infra.domain.logger.repository.ApiAccessLogPageQuery;
import com.develop.mvp.pk.module.infra.domain.logger.repository.ApiAccessLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.develop.mvp.pk.module.infra.dal.dataobject.logger.ApiAccessLogDO.REQUEST_PARAMS_MAX_LENGTH;
import static com.develop.mvp.pk.module.infra.dal.dataobject.logger.ApiAccessLogDO.RESULT_MSG_MAX_LENGTH;

@Service
public class ApiAccessLogApplicationService {

    private final ApiAccessLogRepository apiAccessLogRepository;
    private final DomainEventPublisher eventPublisher;

    public ApiAccessLogApplicationService(ApiAccessLogRepository apiAccessLogRepository,
                                           DomainEventPublisher eventPublisher) {
        this.apiAccessLogRepository = apiAccessLogRepository;
        this.eventPublisher = eventPublisher;
    }

    public void createApiAccessLog(ApiAccessLogCreateReqDTO createDTO) {
        ApiAccessLogDO apiAccessLog = BeanUtils.toBean(createDTO, ApiAccessLogDO.class);
        apiAccessLog.setRequestParams(StrUtils.maxLength(
                apiAccessLog.getRequestParams(), REQUEST_PARAMS_MAX_LENGTH));
        apiAccessLog.setResultMsg(StrUtils.maxLength(
                apiAccessLog.getResultMsg(), RESULT_MSG_MAX_LENGTH));

        if (TenantContextHolder.getTenantId() != null) {
            // 通过 DO 直接插入（日志模块避免复杂 DDD 映射）
            com.develop.mvp.pk.module.infra.dal.mysql.logger.ApiAccessLogMapper mapper =
                    com.develop.mvp.pk.module.infra.service.logger.ApiAccessLogServiceImpl.getMapper();
        }
    }

    public ApiAccessLog getApiAccessLog(Long id) {
        return apiAccessLogRepository.findById(id);
    }

    public PageResult<ApiAccessLog> getApiAccessLogPage(ApiAccessLogPageQuery query) {
        return apiAccessLogRepository.findPage(query);
    }

    @Transactional
    public Integer cleanAccessLog(Integer exceedDay, Integer deleteLimit) {
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        int count = 0;
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = apiAccessLogRepository.deleteByCreateTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            if (deleteCount < deleteLimit) break;
        }
        return count;
    }
}
