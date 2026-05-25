package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.member.service.MemberApplicationService;
import com.develop.mvp.pk.module.system.application.sms.service.SmsApplicationService;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.dal.mysql.sms.SmsChannelMapper;
import com.develop.mvp.pk.module.system.dal.mysql.sms.SmsCodeMapper;
import com.develop.mvp.pk.module.system.dal.mysql.sms.SmsLogMapper;
import com.develop.mvp.pk.module.system.dal.mysql.sms.SmsTemplateMapper;
import com.develop.mvp.pk.module.system.domain.sms.repository.SmsChannelRepository;
import com.develop.mvp.pk.module.system.framework.sms.config.SmsCodeProperties;
import com.develop.mvp.pk.module.system.framework.sms.core.client.SmsClientFactory;
import com.develop.mvp.pk.module.system.mq.producer.sms.SmsProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SmsApplicationServiceConfiguration {

    @Bean
    public SmsApplicationService smsApplicationService(
            SmsChannelRepository channelRepo,
            SmsClientFactory smsClientFactory,
            SmsChannelMapper smsChannelMapper,
            SmsTemplateMapper smsTemplateMapper,
            SmsLogMapper smsLogMapper,
            SmsCodeMapper smsCodeMapper,
            SmsCodeProperties smsCodeProperties,
            AdminUserUseCase adminUserService,
            MemberApplicationService memberApplicationService,
            SmsProducer smsProducer) {
        return new SmsApplicationService(channelRepo, smsClientFactory, smsChannelMapper,
                smsTemplateMapper, smsLogMapper, smsCodeMapper, smsCodeProperties,
                adminUserService, memberApplicationService, smsProducer);
    }
}
