package com.develop.mvp.pk.module.bpm.framework.rpc.config;

import com.develop.mvp.pk.module.bpm.api.event.CrmContractStatusListener;
import com.develop.mvp.pk.module.bpm.api.event.CrmReceivableStatusListener;
import com.develop.mvp.pk.module.system.api.dept.DeptApi;
import com.develop.mvp.pk.module.system.api.dept.PostApi;
import com.develop.mvp.pk.module.system.api.dict.DictDataApi;
import com.develop.mvp.pk.module.system.api.permission.PermissionApi;
import com.develop.mvp.pk.module.system.api.permission.RoleApi;
import com.develop.mvp.pk.module.system.api.sms.SmsSendApi;
import com.develop.mvp.pk.module.system.api.user.AdminUserApi;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "bpmRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {RoleApi.class, DeptApi.class, PostApi.class, AdminUserApi.class, SmsSendApi.class, DictDataApi.class,
        PermissionApi.class})
public class RpcConfiguration {

    // ========== 特殊：解决微 develop-cloud 微服务场景下，跨服务（进程）无法 Listener 的问题 ==========

    @Bean
    @ConditionalOnMissingBean(name = "crmReceivableStatusListener")
    public CrmReceivableStatusListener crmReceivableStatusListener() {
        return new CrmReceivableStatusListener();
    }

    @Bean
    @ConditionalOnMissingBean(name = "crmContractStatusListener")
    public CrmContractStatusListener crmContractStatusListener() {
        return new CrmContractStatusListener();
    }

}
