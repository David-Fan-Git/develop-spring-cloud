package com.develop.mvp.pk.module.promotion.framework.rpc.config;

import com.develop.mvp.pk.module.infra.api.websocket.WebSocketSenderApi;
import com.develop.mvp.pk.module.member.api.user.MemberUserApi;
import com.develop.mvp.pk.module.product.api.category.ProductCategoryApi;
import com.develop.mvp.pk.module.product.api.sku.ProductSkuApi;
import com.develop.mvp.pk.module.product.api.spu.ProductSpuApi;
import com.develop.mvp.pk.module.system.api.social.remote.SocialClientRemoteClient;
import com.develop.mvp.pk.module.system.api.user.remote.AdminUserRemoteClient;
import com.develop.mvp.pk.module.trade.api.order.TradeOrderApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "promotionRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {ProductSkuApi.class, ProductSpuApi.class, ProductCategoryApi.class,
        MemberUserApi.class, TradeOrderApi.class, AdminUserRemoteClient.class, SocialClientRemoteClient.class,
        WebSocketSenderApi.class})
public class RpcConfiguration {
}
