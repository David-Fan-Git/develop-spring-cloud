package com.develop.mvp.pk.module.system.framework.appconfig;

import com.develop.mvp.pk.module.system.application.auth.port.inbound.AuthUseCase;
import com.develop.mvp.pk.module.system.application.oauth2.service.OAuth2ApplicationService;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2ApproveMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2ClientMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2CodeMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2RefreshTokenMapper;
import com.develop.mvp.pk.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.develop.mvp.pk.module.system.domain.oauth2.repository.OAuth2AccessTokenRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Optional;

@Configuration
public class OAuth2ApplicationServiceConfiguration {

    @Bean
    public OAuth2ApplicationService oAuth2ApplicationService(
            OAuth2ClientMapper oauth2ClientMapper,
            OAuth2AccessTokenMapper oauth2AccessTokenMapper,
            OAuth2RefreshTokenMapper oauth2RefreshTokenMapper,
            OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO,
            OAuth2CodeMapper oauth2CodeMapper,
            OAuth2ApproveMapper oauth2ApproveMapper,
            @Lazy AdminUserUseCase adminUserService,
            @Lazy AuthUseCase adminAuthService,
            Optional<OAuth2AccessTokenRepository> tokenRepoOpt) {
        OAuth2ApplicationService svc = new OAuth2ApplicationService(
                oauth2ClientMapper, oauth2AccessTokenMapper, oauth2RefreshTokenMapper,
                oauth2AccessTokenRedisDAO, oauth2CodeMapper, oauth2ApproveMapper,
                adminUserService, adminAuthService);
        tokenRepoOpt.ifPresent(svc::setTokenRepo);
        return svc;
    }
}
