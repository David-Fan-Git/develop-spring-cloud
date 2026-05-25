package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.dict.service.DictApplicationService;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictDataRepository;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictTypeRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DictApplicationServiceConfiguration {

    @Bean
    public DictApplicationService dictApplicationService(
            DictTypeRepository dictTypeRepository,
            DictDataRepository dictDataRepository) {
        return new DictApplicationService(dictTypeRepository, dictDataRepository);
    }
}
