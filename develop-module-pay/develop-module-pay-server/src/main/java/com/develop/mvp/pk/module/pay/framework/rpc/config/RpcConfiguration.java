package com.develop.mvp.pk.module.pay.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.social.remote.SocialClientRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "payRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SocialClientRemoteClient.class})
public class RpcConfiguration {
}
