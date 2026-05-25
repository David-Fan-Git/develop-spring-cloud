package com.develop.mvp.pk.module.system.framework.rpc.config;

import com.develop.mvp.pk.module.infra.api.config.ConfigApi;
import com.develop.mvp.pk.module.infra.api.file.FileApi;
import com.develop.mvp.pk.module.infra.api.websocket.WebSocketSenderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Rpc Configuration 配置类。
 */
@Configuration(value = "systemRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {FileApi.class, WebSocketSenderApi.class, ConfigApi.class})
public class RpcConfiguration {
}
