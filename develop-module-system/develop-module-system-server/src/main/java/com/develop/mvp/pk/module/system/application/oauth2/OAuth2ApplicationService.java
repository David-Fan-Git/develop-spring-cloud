package com.develop.mvp.pk.module.system.application.oauth2;

import com.develop.mvp.pk.module.system.domain.oauth2.OAuth2AccessToken;
import com.develop.mvp.pk.module.system.domain.oauth2.repository.OAuth2AccessTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OAuth2ApplicationService {
    private final OAuth2AccessTokenRepository tokenRepo;

    @Transactional public OAuth2AccessToken createToken(Long id, String accessToken, String refreshToken, Long userId, Integer userType, String clientId, List<String> scopes, LocalDateTime expiresTime) {
        var t = OAuth2AccessToken.of(id, accessToken, refreshToken, clientId).userId(userId).userType(userType).scopes(scopes).expiresTime(expiresTime);
        return tokenRepo.save(t);
    }
    @Transactional public void deleteToken(Long id) { tokenRepo.delete(id); }
    public OAuth2AccessToken getToken(Long id) { return tokenRepo.findById(id); }
    public OAuth2AccessToken findByAccessToken(String at) { return tokenRepo.findByAccessToken(at); }
    public OAuth2AccessToken findByRefreshToken(String rt) { return tokenRepo.findByRefreshToken(rt); }
    @Transactional public void deleteByUserTypeAndUserId(Integer userType, Long userId) { tokenRepo.deleteByUserTypeAndUserId(userType, userId); }
}
