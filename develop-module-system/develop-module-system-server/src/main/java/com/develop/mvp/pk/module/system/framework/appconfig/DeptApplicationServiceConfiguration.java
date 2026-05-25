package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.dept.service.DeptApplicationService;
import com.develop.mvp.pk.module.system.dal.mysql.dept.DeptMapper;
import com.develop.mvp.pk.module.system.dal.mysql.dept.PostMapper;
import com.develop.mvp.pk.module.system.domain.dept.repository.DeptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeptApplicationServiceConfiguration {

    @Bean
    public DeptApplicationService deptApplicationService(
            @Autowired(required = false) DeptRepository deptRepository,
            @Autowired(required = false) ApplicationEventPublisher eventPublisher,
            DeptMapper deptMapper,
            PostMapper postMapper) {
        return new DeptApplicationService(deptRepository, eventPublisher, deptMapper, postMapper);
    }
}
