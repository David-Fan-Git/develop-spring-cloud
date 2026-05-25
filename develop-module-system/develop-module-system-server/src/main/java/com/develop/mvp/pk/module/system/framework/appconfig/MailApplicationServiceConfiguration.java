package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.mail.service.MailApplicationService;
import com.develop.mvp.pk.module.system.application.member.service.MemberApplicationService;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailAccountMapper;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailLogMapper;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailTemplateMapper;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailAccountRepository;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailTemplateRepository;
import com.develop.mvp.pk.module.system.mq.producer.mail.MailProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailApplicationServiceConfiguration {

    @Bean
    public MailApplicationService mailApplicationService(
            MailAccountMapper mailAccountMapper,
            MailTemplateMapper mailTemplateMapper,
            MailLogMapper mailLogMapper,
            AdminUserUseCase adminUserService,
            MemberApplicationService memberApplicationService,
            MailProducer mailProducer,
            @Autowired(required = false) MailAccountRepository accountRepo,
            @Autowired(required = false) MailTemplateRepository templateRepo) {
        MailApplicationService svc = new MailApplicationService(
                mailAccountMapper, mailTemplateMapper, mailLogMapper,
                adminUserService, memberApplicationService, mailProducer);
        if (accountRepo != null) {
            svc.setAccountRepo(accountRepo);
        }
        if (templateRepo != null) {
            svc.setTemplateRepo(templateRepo);
        }
        return svc;
    }
}
