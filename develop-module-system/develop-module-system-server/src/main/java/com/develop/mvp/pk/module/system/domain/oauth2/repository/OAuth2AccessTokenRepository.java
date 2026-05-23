package com.develop.mvp.pk.module.system.domain.oauth2.repository;

import com.develop.mvp.pk.module.system.domain.oauth2.OAuth2AccessToken;
import java.util.List;

public interface OAuth2AccessTokenRepository {
    OAuth2AccessToken save(OAuth2AccessToken t);
    void delete(Long id);
    OAuth2AccessToken findById(Long id);
    OAuth2AccessToken findByAccessToken(String accessToken);
    OAuth2AccessToken findByRefreshToken(String refreshToken);
    void deleteByUserId(Long userId);
    void deleteByUserTypeAndUserId(Integer userType, Long userId);
}
