package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.logger.service.LoggerApplicationService;
import com.develop.mvp.pk.module.system.domain.logger.repository.LoginLogRepository;
import com.develop.mvp.pk.module.system.domain.logger.repository.OperateLogRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerApplicationServiceConfiguration {

    @Bean
    public LoggerApplicationService loggerApplicationService(
            LoginLogRepository loginLogRepository,
            OperateLogRepository operateLogRepository) {
        return new LoggerApplicationService(loginLogRepository, operateLogRepository);
    }
}
