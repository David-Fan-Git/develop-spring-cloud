package com.develop.mvp.pk.module.mes.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.dept.remote.PostRemoteClient;
import com.develop.mvp.pk.module.system.api.permission.remote.RoleRemoteClient;
import com.develop.mvp.pk.module.system.api.user.remote.AdminUserRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "mesRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {AdminUserRemoteClient.class, PostRemoteClient.class, RoleRemoteClient.class})
public class RpcConfiguration {
}
