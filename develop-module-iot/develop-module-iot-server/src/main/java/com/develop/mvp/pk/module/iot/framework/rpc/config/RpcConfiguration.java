package com.develop.mvp.pk.module.iot.framework.rpc.config;

import com.develop.mvp.pk.module.system.api.mail.MailSendApi;
import com.develop.mvp.pk.module.system.api.notify.NotifyMessageSendApi;
import com.develop.mvp.pk.module.system.api.sms.SmsSendApi;
import com.develop.mvp.pk.module.system.api.user.AdminUserApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "iotRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {
        AdminUserApi.class, SmsSendApi.class, MailSendApi.class, NotifyMessageSendApi.class
})
public class RpcConfiguration {
}
