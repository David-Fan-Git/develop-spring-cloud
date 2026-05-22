package com.develop.mvp.pk.module.pay.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.social.SocialClientApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "payRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {SocialClientApi.class})
public class RpcConfiguration {
}
