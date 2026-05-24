package com.develop.mvp.pk.module.system.infrastructure.oauth2.persistence;

import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import com.develop.mvp.pk.module.system.domain.oauth2.OAuth2AccessToken;
import com.develop.mvp.pk.module.system.domain.oauth2.repository.OAuth2AccessTokenRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class OAuth2AccessTokenRepositoryImpl implements OAuth2AccessTokenRepository {
    private final OAuth2AccessTokenMapper mapper;
    public OAuth2AccessTokenRepositoryImpl(OAuth2AccessTokenMapper mapper) { this.mapper = mapper; }

    @Override
    public OAuth2AccessToken save(OAuth2AccessToken t) {
        OAuth2AccessTokenDO d = new OAuth2AccessTokenDO(); d.setId(t.id()); d.setAccessToken(t.accessToken());
        d.setRefreshToken(t.refreshToken()); d.setUserId(t.userId()); d.setUserType(t.userType());
        d.setClientId(t.clientId()); d.setScopes(t.scopes()); d.setExpiresTime(t.expiresTime());
        if (mapper.selectById(t.id()) == null) mapper.insert(d); else mapper.updateById(d);
        return t;
    }

    @Override public void delete(Long id) { mapper.deleteById(id); }
    @Override public OAuth2AccessToken findById(Long id) { OAuth2AccessTokenDO d = mapper.selectById(id); return d != null ? toDomain(d) : null; }
    @Override public OAuth2AccessToken findByAccessToken(String at) { OAuth2AccessTokenDO d = mapper.selectByAccessToken(at); return d != null ? toDomain(d) : null; }
    @Override public OAuth2AccessToken findByRefreshToken(String rt) {
        List<OAuth2AccessTokenDO> list = mapper.selectListByRefreshToken(rt);
        return list.isEmpty() ? null : toDomain(list.get(0));
    }

    @Override public void deleteByUserId(Long userId) { mapper.delete(OAuth2AccessTokenDO::getUserId, userId); }
    @Override public void deleteByUserTypeAndUserId(Integer userType, Long userId) {
        mapper.selectListByUserIdAndUserType(userId, userType).forEach(t -> mapper.deleteById(t.getId()));
    }

    private OAuth2AccessToken toDomain(OAuth2AccessTokenDO d) {
        return OAuth2AccessToken.of(d.getId(), d.getAccessToken(), d.getRefreshToken(), d.getClientId())
                .userId(d.getUserId()).userType(d.getUserType()).scopes(d.getScopes()).expiresTime(d.getExpiresTime());
    }
}
