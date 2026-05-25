package com.develop.mvp.pk.module.system.application.oauth2.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2CodeDO;
import com.develop.mvp.pk.module.system.domain.oauth2.OAuth2AccessToken;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * oauth2 aggregate use-case boundary for legacy entries and new adapters.
 */
public interface OAuth2UseCase {

    // ========== OAuth2 客户端 CRUD ==========

    Long createOAuth2Client(OAuth2ClientSaveReqVO createReqVO);

    void updateOAuth2Client(OAuth2ClientSaveReqVO updateReqVO);

    void deleteOAuth2Client(Long id);

    void deleteOAuth2ClientList(List<Long> ids);

    OAuth2ClientDO getOAuth2Client(Long id);

    OAuth2ClientDO getOAuth2ClientFromCache(String clientId);

    PageResult<OAuth2ClientDO> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO);

    OAuth2ClientDO validOAuthClientFromCache(String clientId);

    OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                              Collection<String> scopes, String redirectUri);

    // ========== 访问令牌 ==========

    OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes);

    OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId);

    OAuth2AccessTokenDO getAccessToken(String accessToken);

    OAuth2AccessTokenDO checkAccessToken(String accessToken);

    OAuth2AccessTokenDO removeAccessToken(String accessToken);

    void removeAccessToken(Long userId, Integer userType);

    PageResult<OAuth2AccessTokenDO> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO);

    Integer cleanRefreshToken(Integer exceedDay, Integer deleteLimit);

    Integer cleanAccessToken(Integer exceedDay, Integer deleteLimit);

    // ========== 授权码 ==========

    OAuth2CodeDO createAuthorizationCode(Long userId, Integer userType, String clientId,
                                          List<String> scopes, String redirectUri, String state);

    OAuth2CodeDO consumeAuthorizationCode(String code);

    // ========== 审批 ==========

    boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes);

    boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes);

    List<OAuth2ApproveDO> getApproveList(Long userId, Integer userType, String clientId);

    // ========== 授权模式 ==========

    OAuth2AccessTokenDO grantImplicit(Long userId, Integer userType, String clientId, List<String> scopes);

    String grantAuthorizationCodeForCode(Long userId, Integer userType, String clientId, List<String> scopes,
                                          String redirectUri, String state);

    OAuth2AccessTokenDO grantAuthorizationCodeForAccessToken(String clientId, String code, String redirectUri, String state);

    OAuth2AccessTokenDO grantPassword(String username, String password, String clientId, List<String> scopes);

    OAuth2AccessTokenDO grantRefreshToken(String refreshToken, String clientId);

    OAuth2AccessTokenDO grantClientCredentials(String clientId, List<String> scopes);

    boolean revokeToken(String clientId, String accessToken);

    // ========== Token 领域对象操作 ==========

    OAuth2AccessToken createToken(Long id, String accessToken, String refreshToken, Long userId, Integer userType,
                                   String clientId, List<String> scopes, LocalDateTime expiresTime);

    void deleteToken(Long id);

    OAuth2AccessToken getToken(Long id);

    OAuth2AccessToken findByAccessToken(String accessToken);

    OAuth2AccessToken findByRefreshToken(String refreshToken);

    void deleteByUserTypeAndUserId(Integer userType, Long userId);
}
