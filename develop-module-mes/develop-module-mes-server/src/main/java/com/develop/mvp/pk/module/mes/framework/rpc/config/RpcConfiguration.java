package com.develop.mvp.pk.module.mes.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.dept.PostApi;
import com.develop.mvp.pk.module.system.api.permission.RoleApi;
import com.develop.mvp.pk.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "mesRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {AdminUserApi.class, PostApi.class, RoleApi.class})
public class RpcConfiguration {
}
