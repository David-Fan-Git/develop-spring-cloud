package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.dept.port.inbound.DeptUseCase;
import com.develop.mvp.pk.module.system.application.user.service.UserApplicationService;
import com.develop.mvp.pk.module.system.domain.user.event.DomainEventPublisher;
import com.develop.mvp.pk.module.system.domain.user.repository.UserRepository;
import com.develop.mvp.pk.module.system.domain.user.service.PasswordEncoder;
import com.develop.mvp.pk.module.system.domain.user.service.UserUniquenessChecker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserApplicationServiceConfiguration {

    @Bean
    public UserApplicationService userApplicationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserUniquenessChecker uniquenessChecker,
            DomainEventPublisher eventPublisher,
            DeptUseCase deptUseCase,
            DeptUseCase postUseCase) {
        return new UserApplicationService(userRepository, passwordEncoder, uniquenessChecker,
                eventPublisher, deptUseCase, postUseCase);
    }
}
