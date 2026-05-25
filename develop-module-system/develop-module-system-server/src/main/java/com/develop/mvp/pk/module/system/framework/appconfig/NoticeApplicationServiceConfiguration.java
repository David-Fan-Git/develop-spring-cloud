package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.notice.service.NoticeApplicationService;
import com.develop.mvp.pk.module.system.domain.notice.repository.NoticeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NoticeApplicationServiceConfiguration {

    @Bean
    public NoticeApplicationService noticeApplicationService(NoticeRepository noticeRepository) {
        return new NoticeApplicationService(noticeRepository);
    }
}
