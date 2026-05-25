package com.develop.mvp.pk.module.system.application.oauth2.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.enums.UserTypeEnum;
import com.develop.mvp.pk.framework.common.exception.ServiceException;
import com.develop.mvp.pk.framework.common.exception.enums.GlobalErrorCodeConstants;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.date.DateUtils;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.framework.common.util.string.StrUtils;
import com.develop.mvp.pk.framework.security.core.LoginUser;
import com.develop.mvp.pk.framework.tenant.core.context.TenantContextHolder;
import com.develop.mvp.pk.framework.tenant.core.util.TenantUtils;
import com.develop.mvp.pk.module.system.application.auth.port.inbound.AuthUseCase;
import com.develop.mvp.pk.module.system.application.oauth2.port.inbound.OAuth2UseCase;
import com.develop.mvp.pk.module.system.application.user.port.inbound.AdminUserUseCase;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.client.OAuth2ClientPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.client.OAuth2ClientSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.oauth2.vo.token.OAuth2AccessTokenPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2ApproveDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2ClientDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2CodeDO;
import com.develop.mvp.pk.module.system.dal.dataobject.oauth2.OAuth2RefreshTokenDO;
import com.develop.mvp.pk.module.system.dal.dataobject.user.AdminUserDO;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2AccessTokenMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2ApproveMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2ClientMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2CodeMapper;
import com.develop.mvp.pk.module.system.dal.mysql.oauth2.OAuth2RefreshTokenMapper;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.dal.redis.oauth2.OAuth2AccessTokenRedisDAO;
import com.develop.mvp.pk.module.system.domain.oauth2.OAuth2AccessToken;
import com.develop.mvp.pk.module.system.domain.oauth2.repository.OAuth2AccessTokenRepository;
import com.develop.mvp.pk.module.system.enums.ErrorCodeConstants;
import com.google.common.annotations.VisibleForTesting;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception0;
import static com.develop.mvp.pk.framework.common.util.collection.CollectionUtils.convertSet;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

public class OAuth2ApplicationService implements OAuth2UseCase {

    private static final Integer CODE_TIMEOUT = 5 * 60;
    private static final Integer APPROVE_TIMEOUT = 30 * 24 * 60 * 60;

    private OAuth2AccessTokenRepository tokenRepo;
    private final OAuth2ClientMapper oauth2ClientMapper;
    private final OAuth2AccessTokenMapper oauth2AccessTokenMapper;
    private final OAuth2RefreshTokenMapper oauth2RefreshTokenMapper;
    private final OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO;
    private final OAuth2CodeMapper oauth2CodeMapper;
    private final OAuth2ApproveMapper oauth2ApproveMapper;
    private final AdminUserUseCase adminUserService;
    private final AuthUseCase adminAuthService;

    public OAuth2ApplicationService() {
        this.oauth2ClientMapper = null;
        this.oauth2AccessTokenMapper = null;
        this.oauth2RefreshTokenMapper = null;
        this.oauth2AccessTokenRedisDAO = null;
        this.oauth2CodeMapper = null;
        this.oauth2ApproveMapper = null;
        this.adminUserService = null;
        this.adminAuthService = null;
    }

    @Autowired
    public OAuth2ApplicationService(
            OAuth2ClientMapper oauth2ClientMapper,
            OAuth2AccessTokenMapper oauth2AccessTokenMapper,
            OAuth2RefreshTokenMapper oauth2RefreshTokenMapper,
            OAuth2AccessTokenRedisDAO oauth2AccessTokenRedisDAO,
            OAuth2CodeMapper oauth2CodeMapper,
            OAuth2ApproveMapper oauth2ApproveMapper,
            AdminUserUseCase adminUserService,
            AuthUseCase adminAuthService) {
        this.oauth2ClientMapper = oauth2ClientMapper;
        this.oauth2AccessTokenMapper = oauth2AccessTokenMapper;
        this.oauth2RefreshTokenMapper = oauth2RefreshTokenMapper;
        this.oauth2AccessTokenRedisDAO = oauth2AccessTokenRedisDAO;
        this.oauth2CodeMapper = oauth2CodeMapper;
        this.oauth2ApproveMapper = oauth2ApproveMapper;
        this.adminUserService = adminUserService;
        this.adminAuthService = adminAuthService;
    }

    public void setTokenRepo(OAuth2AccessTokenRepository tokenRepo) {
        this.tokenRepo = tokenRepo;
    }

    public Long createOAuth2Client(@Valid OAuth2ClientSaveReqVO createReqVO) {
        validateClientIdExists(null, createReqVO.getClientId());
        OAuth2ClientDO client = BeanUtils.toBean(createReqVO, OAuth2ClientDO.class);
        oauth2ClientMapper.insert(client);
        return client.getId();
    }

    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT, allEntries = true)
    public void updateOAuth2Client(@Valid OAuth2ClientSaveReqVO updateReqVO) {
        validateOAuth2ClientExists(updateReqVO.getId());
        validateClientIdExists(updateReqVO.getId(), updateReqVO.getClientId());
        OAuth2ClientDO updateObj = BeanUtils.toBean(updateReqVO, OAuth2ClientDO.class);
        oauth2ClientMapper.updateById(updateObj);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT, allEntries = true)
    public void deleteOAuth2Client(Long id) {
        validateOAuth2ClientExists(id);
        oauth2ClientMapper.deleteById(id);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.OAUTH_CLIENT, allEntries = true)
    public void deleteOAuth2ClientList(List<Long> ids) {
        oauth2ClientMapper.deleteByIds(ids);
    }

    private void validateOAuth2ClientExists(Long id) {
        if (oauth2ClientMapper.selectById(id) == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
    }

    @VisibleForTesting
    void validateClientIdExists(Long id, String clientId) {
        OAuth2ClientDO client = oauth2ClientMapper.selectByClientId(clientId);
        if (client == null) {
            return;
        }
        if (id == null || !client.getId().equals(id)) {
            throw exception(OAUTH2_CLIENT_EXISTS);
        }
    }

    public OAuth2ClientDO getOAuth2Client(Long id) {
        return oauth2ClientMapper.selectById(id);
    }

    @Cacheable(cacheNames = RedisKeyConstants.OAUTH_CLIENT, key = "#clientId", unless = "#result == null")
    public OAuth2ClientDO getOAuth2ClientFromCache(String clientId) {
        return oauth2ClientMapper.selectByClientId(clientId);
    }

    public PageResult<OAuth2ClientDO> getOAuth2ClientPage(OAuth2ClientPageReqVO pageReqVO) {
        return oauth2ClientMapper.selectPage(pageReqVO);
    }

    public OAuth2ClientDO validOAuthClientFromCache(String clientId) {
        return validOAuthClientFromCache(clientId, null, null, null, null);
    }

    public OAuth2ClientDO validOAuthClientFromCache(String clientId, String clientSecret, String authorizedGrantType,
                                                    Collection<String> scopes, String redirectUri) {
        OAuth2ClientDO client = getSelf().getOAuth2ClientFromCache(clientId);
        if (client == null) {
            throw exception(OAUTH2_CLIENT_NOT_EXISTS);
        }
        if (CommonStatusEnum.isDisable(client.getStatus())) {
            throw exception(OAUTH2_CLIENT_DISABLE);
        }
        if (StrUtil.isNotEmpty(clientSecret) && ObjectUtil.notEqual(client.getSecret(), clientSecret)) {
            throw exception(OAUTH2_CLIENT_CLIENT_SECRET_ERROR);
        }
        if (StrUtil.isNotEmpty(authorizedGrantType) && !CollUtil.contains(client.getAuthorizedGrantTypes(), authorizedGrantType)) {
            throw exception(OAUTH2_CLIENT_AUTHORIZED_GRANT_TYPE_NOT_EXISTS);
        }
        if (CollUtil.isNotEmpty(scopes) && !CollUtil.containsAll(client.getScopes(), scopes)) {
            throw exception(OAUTH2_CLIENT_SCOPE_OVER);
        }
        if (StrUtil.isNotEmpty(redirectUri) && !StrUtils.startWithAny(redirectUri, client.getRedirectUris())) {
            throw exception(OAUTH2_CLIENT_REDIRECT_URI_NOT_MATCH, redirectUri);
        }
        return client;
    }

    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO createAccessToken(Long userId, Integer userType, String clientId, List<String> scopes) {
        OAuth2ClientDO clientDO = validOAuthClientFromCache(clientId);
        OAuth2RefreshTokenDO refreshTokenDO = createOAuth2RefreshToken(userId, userType, clientDO, scopes);
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    @Transactional(noRollbackFor = ServiceException.class)
    public OAuth2AccessTokenDO refreshAccessToken(String refreshToken, String clientId) {
        OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(refreshToken);
        if (refreshTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "无效的刷新令牌");
        }
        OAuth2ClientDO clientDO = validOAuthClientFromCache(clientId);
        if (ObjectUtil.notEqual(clientId, refreshTokenDO.getClientId())) {
            throw exception0(GlobalErrorCodeConstants.BAD_REQUEST.getCode(), "刷新令牌的客户端编号不正确");
        }
        List<OAuth2AccessTokenDO> accessTokenDOs = oauth2AccessTokenMapper.selectListByRefreshToken(refreshToken);
        if (CollUtil.isNotEmpty(accessTokenDOs)) {
            oauth2AccessTokenMapper.deleteByIds(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getId));
            oauth2AccessTokenRedisDAO.deleteList(convertSet(accessTokenDOs, OAuth2AccessTokenDO::getAccessToken));
        }
        if (DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
            oauth2RefreshTokenMapper.deleteById(refreshTokenDO.getId());
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "刷新令牌已过期");
        }
        return createOAuth2AccessToken(refreshTokenDO, clientDO);
    }

    public OAuth2AccessTokenDO getAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenRedisDAO.get(accessToken);
        if (accessTokenDO != null) {
            return accessTokenDO;
        }
        accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            OAuth2RefreshTokenDO refreshTokenDO = oauth2RefreshTokenMapper.selectByRefreshToken(accessToken);
            if (refreshTokenDO != null && !DateUtils.isExpired(refreshTokenDO.getExpiresTime())) {
                accessTokenDO = convertToAccessToken(refreshTokenDO);
            }
        }
        if (accessTokenDO != null && !DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            oauth2AccessTokenRedisDAO.set(accessTokenDO);
        }
        return accessTokenDO;
    }

    public OAuth2AccessTokenDO checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌不存在");
        }
        if (DateUtils.isExpired(accessTokenDO.getExpiresTime())) {
            throw exception0(GlobalErrorCodeConstants.UNAUTHORIZED.getCode(), "访问令牌已过期");
        }
        return accessTokenDO;
    }

    @Transactional(rollbackFor = Exception.class)
    public OAuth2AccessTokenDO removeAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2AccessTokenMapper.selectByAccessToken(accessToken);
        if (accessTokenDO == null) {
            return null;
        }
        oauth2AccessTokenMapper.deleteById(accessTokenDO.getId());
        oauth2AccessTokenRedisDAO.delete(accessToken);
        oauth2RefreshTokenMapper.deleteByRefreshToken(accessTokenDO.getRefreshToken());
        oauth2AccessTokenRedisDAO.delete(accessTokenDO.getRefreshToken());
        return accessTokenDO;
    }

    public void removeAccessToken(Long userId, Integer userType) {
        List<OAuth2AccessTokenDO> accessTokens = oauth2AccessTokenMapper.selectListByUserIdAndUserType(userId, userType);
        if (CollUtil.isEmpty(accessTokens)) {
            return;
        }
        accessTokens.forEach(accessToken -> {
            oauth2AccessTokenMapper.deleteById(accessToken.getId());
            oauth2AccessTokenRedisDAO.delete(accessToken.getAccessToken());
            oauth2RefreshTokenMapper.deleteByRefreshToken(accessToken.getRefreshToken());
            oauth2AccessTokenRedisDAO.delete(accessToken.getRefreshToken());
        });
    }

    public PageResult<OAuth2AccessTokenDO> getAccessTokenPage(OAuth2AccessTokenPageReqVO reqVO) {
        return oauth2AccessTokenMapper.selectPage(reqVO);
    }

    private OAuth2AccessTokenDO createOAuth2AccessToken(OAuth2RefreshTokenDO refreshTokenDO, OAuth2ClientDO clientDO) {
        OAuth2AccessTokenDO accessTokenDO = new OAuth2AccessTokenDO().setAccessToken(generateAccessToken())
                .setUserId(refreshTokenDO.getUserId()).setUserType(refreshTokenDO.getUserType())
                .setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType()))
                .setClientId(clientDO.getClientId()).setScopes(refreshTokenDO.getScopes())
                .setRefreshToken(refreshTokenDO.getRefreshToken())
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getAccessTokenValiditySeconds()));
        Long tenantId = refreshTokenDO.getTenantId();
        if (tenantId == null) {
            tenantId = TenantContextHolder.getTenantId();
        }
        accessTokenDO.setTenantId(tenantId);
        oauth2AccessTokenMapper.insert(accessTokenDO);
        oauth2AccessTokenRedisDAO.set(accessTokenDO);
        return accessTokenDO;
    }

    private OAuth2RefreshTokenDO createOAuth2RefreshToken(Long userId, Integer userType, OAuth2ClientDO clientDO, List<String> scopes) {
        OAuth2RefreshTokenDO refreshToken = new OAuth2RefreshTokenDO().setRefreshToken(generateRefreshToken())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientDO.getClientId()).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(clientDO.getRefreshTokenValiditySeconds()));
        oauth2RefreshTokenMapper.insert(refreshToken);
        return refreshToken;
    }

    private OAuth2AccessTokenDO convertToAccessToken(OAuth2RefreshTokenDO refreshTokenDO) {
        OAuth2AccessTokenDO accessTokenDO = BeanUtils.toBean(refreshTokenDO, OAuth2AccessTokenDO.class)
                .setAccessToken(refreshTokenDO.getRefreshToken());
        TenantUtils.execute(refreshTokenDO.getTenantId(),
                () -> accessTokenDO.setUserInfo(buildUserInfo(refreshTokenDO.getUserId(), refreshTokenDO.getUserType())));
        return accessTokenDO;
    }

    private Map<String, String> buildUserInfo(Long userId, Integer userType) {
        if (userId == null || userId <= 0) {
            return Collections.emptyMap();
        }
        if (userType.equals(UserTypeEnum.ADMIN.getValue())) {
            AdminUserDO user = adminUserService.getUser(userId);
            return MapUtil.builder(LoginUser.INFO_KEY_NICKNAME, user.getNickname())
                    .put(LoginUser.INFO_KEY_DEPT_ID, StrUtil.toStringOrNull(user.getDeptId())).build();
        }
        if (userType.equals(UserTypeEnum.MEMBER.getValue())) {
            return Collections.emptyMap();
        }
        throw new IllegalArgumentException("未知用户类型：" + userType);
    }

    public Integer cleanRefreshToken(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = oauth2RefreshTokenMapper.deleteByExpiresTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

    public Integer cleanAccessToken(Integer exceedDay, Integer deleteLimit) {
        int count = 0;
        LocalDateTime expireDate = LocalDateTime.now().minusDays(exceedDay);
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            int deleteCount = oauth2AccessTokenMapper.deleteByExpiresTimeLt(expireDate, deleteLimit);
            count += deleteCount;
            if (deleteCount < deleteLimit) {
                break;
            }
        }
        return count;
    }

    public OAuth2CodeDO createAuthorizationCode(Long userId, Integer userType, String clientId,
                                                List<String> scopes, String redirectUri, String state) {
        OAuth2CodeDO codeDO = new OAuth2CodeDO().setCode(generateCode())
                .setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScopes(scopes)
                .setExpiresTime(LocalDateTime.now().plusSeconds(CODE_TIMEOUT))
                .setRedirectUri(redirectUri).setState(state);
        oauth2CodeMapper.insert(codeDO);
        return codeDO;
    }

    public OAuth2CodeDO consumeAuthorizationCode(String code) {
        OAuth2CodeDO codeDO = oauth2CodeMapper.selectByCode(code);
        if (codeDO == null) {
            throw exception(OAUTH2_CODE_NOT_EXISTS);
        }
        if (DateUtils.isExpired(codeDO.getExpiresTime())) {
            throw exception(OAUTH2_CODE_EXPIRE);
        }
        oauth2CodeMapper.deleteById(codeDO.getId());
        return codeDO;
    }

    @Transactional
    public boolean checkForPreApproval(Long userId, Integer userType, String clientId, Collection<String> requestedScopes) {
        OAuth2ClientDO clientDO = validOAuthClientFromCache(clientId);
        Assert.notNull(clientDO, "客户端不能为空");
        if (CollUtil.containsAll(clientDO.getAutoApproveScopes(), requestedScopes)) {
            LocalDateTime expireTime = LocalDateTime.now().plusSeconds(APPROVE_TIMEOUT);
            for (String scope : requestedScopes) {
                saveApprove(userId, userType, clientId, scope, true, expireTime);
            }
            return true;
        }
        List<OAuth2ApproveDO> approveDOs = getApproveList(userId, userType, clientId);
        Set<String> scopes = convertSet(approveDOs, OAuth2ApproveDO::getScope, OAuth2ApproveDO::getApproved);
        return CollUtil.containsAll(scopes, requestedScopes);
    }

    @Transactional
    public boolean updateAfterApproval(Long userId, Integer userType, String clientId, Map<String, Boolean> requestedScopes) {
        if (CollUtil.isEmpty(requestedScopes)) {
            return true;
        }
        boolean success = false;
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(APPROVE_TIMEOUT);
        for (Map.Entry<String, Boolean> entry : requestedScopes.entrySet()) {
            if (entry.getValue()) {
                success = true;
            }
            saveApprove(userId, userType, clientId, entry.getKey(), entry.getValue(), expireTime);
        }
        return success;
    }

    public List<OAuth2ApproveDO> getApproveList(Long userId, Integer userType, String clientId) {
        List<OAuth2ApproveDO> approveDOs = oauth2ApproveMapper.selectListByUserIdAndUserTypeAndClientId(userId, userType, clientId);
        approveDOs.removeIf(o -> DateUtils.isExpired(o.getExpiresTime()));
        return approveDOs;
    }

    @VisibleForTesting
    void saveApprove(Long userId, Integer userType, String clientId, String scope, Boolean approved, LocalDateTime expireTime) {
        OAuth2ApproveDO approveDO = new OAuth2ApproveDO().setUserId(userId).setUserType(userType)
                .setClientId(clientId).setScope(scope).setApproved(approved).setExpiresTime(expireTime);
        if (oauth2ApproveMapper.update(approveDO) == 1) {
            return;
        }
        oauth2ApproveMapper.insert(approveDO);
    }

    public OAuth2AccessTokenDO grantImplicit(Long userId, Integer userType, String clientId, List<String> scopes) {
        return createAccessToken(userId, userType, clientId, scopes);
    }

    public String grantAuthorizationCodeForCode(Long userId, Integer userType, String clientId, List<String> scopes,
                                                String redirectUri, String state) {
        return createAuthorizationCode(userId, userType, clientId, scopes, redirectUri, state).getCode();
    }

    public OAuth2AccessTokenDO grantAuthorizationCodeForAccessToken(String clientId, String code, String redirectUri, String state) {
        OAuth2CodeDO codeDO = consumeAuthorizationCode(code);
        Assert.notNull(codeDO, "授权码不能为空");
        if (!StrUtil.equals(clientId, codeDO.getClientId())) {
            throw exception(ErrorCodeConstants.OAUTH2_GRANT_CLIENT_ID_MISMATCH);
        }
        if (!StrUtil.equals(redirectUri, codeDO.getRedirectUri())) {
            throw exception(ErrorCodeConstants.OAUTH2_GRANT_REDIRECT_URI_MISMATCH);
        }
        state = StrUtil.nullToDefault(state, "");
        if (!StrUtil.equals(state, codeDO.getState())) {
            throw exception(ErrorCodeConstants.OAUTH2_GRANT_STATE_MISMATCH);
        }
        return createAccessToken(codeDO.getUserId(), codeDO.getUserType(), codeDO.getClientId(), codeDO.getScopes());
    }

    public OAuth2AccessTokenDO grantPassword(String username, String password, String clientId, List<String> scopes) {
        AdminUserDO user = adminAuthService.authenticate(username, password);
        Assert.notNull(user, "用户不能为空！");
        return createAccessToken(user.getId(), UserTypeEnum.ADMIN.getValue(), clientId, scopes);
    }

    public OAuth2AccessTokenDO grantRefreshToken(String refreshToken, String clientId) {
        return refreshAccessToken(refreshToken, clientId);
    }

    public OAuth2AccessTokenDO grantClientCredentials(String clientId, List<String> scopes) {
        return createAccessToken(0L, UserTypeEnum.ADMIN.getValue(), clientId, scopes);
    }

    public boolean revokeToken(String clientId, String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = getAccessToken(accessToken);
        if (accessTokenDO == null || ObjectUtil.notEqual(clientId, accessTokenDO.getClientId())) {
            return false;
        }
        return removeAccessToken(accessToken) != null;
    }

    @Transactional
    public OAuth2AccessToken createToken(Long id, String accessToken, String refreshToken, Long userId, Integer userType,
                                         String clientId, List<String> scopes, LocalDateTime expiresTime) {
        OAuth2AccessToken token = OAuth2AccessToken.of(id, accessToken, refreshToken, clientId)
                .userId(userId).userType(userType).scopes(scopes).expiresTime(expiresTime);
        return tokenRepo.save(token);
    }

    @Transactional
    public void deleteToken(Long id) {
        tokenRepo.delete(id);
    }

    public OAuth2AccessToken getToken(Long id) {
        return tokenRepo.findById(id);
    }

    public OAuth2AccessToken findByAccessToken(String accessToken) {
        return tokenRepo.findByAccessToken(accessToken);
    }

    public OAuth2AccessToken findByRefreshToken(String refreshToken) {
        return tokenRepo.findByRefreshToken(refreshToken);
    }

    @Transactional
    public void deleteByUserTypeAndUserId(Integer userType, Long userId) {
        tokenRepo.deleteByUserTypeAndUserId(userType, userId);
    }

    private OAuth2ApplicationService getSelf() {
        return SpringUtil.getBean(getClass());
    }

    private static String generateAccessToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateRefreshToken() {
        return IdUtil.fastSimpleUUID();
    }

    private static String generateCode() {
        return IdUtil.fastSimpleUUID();
    }
}
