package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.notify.service.NotifyApplicationService;
import com.develop.mvp.pk.module.system.dal.mysql.notify.NotifyMessageMapper;
import com.develop.mvp.pk.module.system.dal.mysql.notify.NotifyTemplateMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotifyApplicationServiceConfiguration {

    @Bean
    public NotifyApplicationService notifyApplicationService(
            NotifyMessageMapper notifyMessageMapper,
            NotifyTemplateMapper notifyTemplateMapper) {
        return new NotifyApplicationService(notifyMessageMapper, notifyTemplateMapper);
    }
}
