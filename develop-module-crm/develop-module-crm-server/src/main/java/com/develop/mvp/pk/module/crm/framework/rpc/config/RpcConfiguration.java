package com.develop.mvp.pk.module.crm.framework.rpc.config;

import com.develop.mvp.pk.module.bpm.api.task.BpmProcessInstanceApi;
import com.develop.mvp.pk.module.system.api.dept.remote.DeptRemoteClient;
import com.develop.mvp.pk.module.system.api.dept.remote.PostRemoteClient;
import com.develop.mvp.pk.module.system.api.logger.remote.OperateLogRemoteClient;
import com.develop.mvp.pk.module.system.api.user.remote.AdminUserRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "crmRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {AdminUserRemoteClient.class, DeptRemoteClient.class, PostRemoteClient.class,
        OperateLogRemoteClient.class,
        BpmProcessInstanceApi.class})
public class RpcConfiguration {
}
