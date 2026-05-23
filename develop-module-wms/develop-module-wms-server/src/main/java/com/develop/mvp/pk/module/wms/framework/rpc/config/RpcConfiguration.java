package com.develop.mvp.pk.module.wms.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.user.remote.AdminUserRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "wmsRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = AdminUserRemoteClient.class)
public class RpcConfiguration {
}
