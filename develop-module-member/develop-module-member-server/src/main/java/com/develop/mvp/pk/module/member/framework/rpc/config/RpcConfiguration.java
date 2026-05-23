package com.develop.mvp.pk.module.member.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.logger.remote.LoginLogRemoteClient;
import com.develop.mvp.pk.module.system.api.sms.remote.SmsCodeRemoteClient;
import com.develop.mvp.pk.module.system.api.social.remote.SocialClientRemoteClient;
import com.develop.mvp.pk.module.system.api.social.remote.SocialUserRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "memberRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SmsCodeRemoteClient.class, LoginLogRemoteClient.class, SocialUserRemoteClient.class, SocialClientRemoteClient.class})
public class RpcConfiguration {
}
