package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.member.port.outbound.MemberUserGateway;
import com.develop.mvp.pk.module.system.application.member.service.MemberApplicationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberApplicationServiceConfiguration {

    @Bean
    public MemberApplicationService memberApplicationService(MemberUserGateway memberUserGateway) {
        return new MemberApplicationService(memberUserGateway);
    }
}
