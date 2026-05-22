package com.develop.mvp.pk.module.member.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.logger.LoginLogApi;
import com.develop.mvp.pk.module.system.api.sms.SmsCodeApi;
import com.develop.mvp.pk.module.system.api.social.SocialClientApi;
import com.develop.mvp.pk.module.system.api.social.SocialUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "memberRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SmsCodeApi.class, LoginLogApi.class, SocialUserApi.class, SocialClientApi.class})
public class RpcConfiguration {
}
