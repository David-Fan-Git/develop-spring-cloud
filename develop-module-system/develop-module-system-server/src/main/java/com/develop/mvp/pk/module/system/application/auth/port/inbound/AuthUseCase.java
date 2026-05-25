package com.develop.mvp.pk.module.system.application.auth.port.inbound;

import com.develop.mvp.pk.module.system.controller.admin.auth.vo.*;
import com.develop.mvp.pk.module.system.dal.dataobject.user.AdminUserDO;

/**
 * auth use-case boundary for legacy service compatibility and future adapters.
 */
public interface AuthUseCase {

    AdminUserDO authenticate(String username, String password);

    AuthLoginRespVO login(AuthLoginReqVO reqVO);

    void sendSmsCode(AuthSmsSendReqVO reqVO);

    AuthLoginRespVO smsLogin(AuthSmsLoginReqVO reqVO);

    AuthLoginRespVO socialLogin(AuthSocialLoginReqVO reqVO);

    AuthLoginRespVO refreshToken(String refreshToken);

    void logout(String token, Integer logType);

    AuthLoginRespVO register(AuthRegisterReqVO registerReqVO);

    void resetPassword(AuthResetPasswordReqVO reqVO);
}
