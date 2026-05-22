package com.develop.mvp.pk.module.ai.framework.rpc.config;

import com.develop.mvp.pk.module.infra.api.file.FileApi;
import com.develop.mvp.pk.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "aiRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, AdminUserApi.class})
public class RpcConfiguration {
}
