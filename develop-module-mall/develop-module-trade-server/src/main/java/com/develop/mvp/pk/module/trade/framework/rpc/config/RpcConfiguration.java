package com.develop.mvp.pk.module.trade.framework.rpc.config;

import com.develop.mvp.pk.module.member.api.address.MemberAddressApi;
import com.develop.mvp.pk.module.member.api.config.remote.MemberConfigRemoteClient;
import com.develop.mvp.pk.module.member.api.level.MemberLevelApi;
import com.develop.mvp.pk.module.member.api.point.MemberPointApi;
import com.develop.mvp.pk.module.member.api.user.MemberUserApi;
import com.develop.mvp.pk.module.pay.api.order.PayOrderApi;
import com.develop.mvp.pk.module.pay.api.refund.PayRefundApi;
import com.develop.mvp.pk.module.pay.api.transfer.PayTransferApi;
import com.develop.mvp.pk.module.pay.api.wallet.PayWalletApi;
import com.develop.mvp.pk.module.product.api.category.ProductCategoryApi;
import com.develop.mvp.pk.module.product.api.comment.ProductCommentApi;
import com.develop.mvp.pk.module.product.api.sku.ProductSkuApi;
import com.develop.mvp.pk.module.product.api.spu.ProductSpuApi;
import com.develop.mvp.pk.module.promotion.api.bargain.BargainActivityApi;
import com.develop.mvp.pk.module.promotion.api.bargain.BargainRecordApi;
import com.develop.mvp.pk.module.promotion.api.combination.CombinationRecordApi;
import com.develop.mvp.pk.module.promotion.api.coupon.CouponApi;
import com.develop.mvp.pk.module.promotion.api.discount.DiscountActivityApi;
import com.develop.mvp.pk.module.promotion.api.point.PointActivityApi;
import com.develop.mvp.pk.module.promotion.api.reward.RewardActivityApi;
import com.develop.mvp.pk.module.promotion.api.seckill.SeckillActivityApi;
import com.develop.mvp.pk.module.system.api.notify.remote.NotifyMessageSendRemoteClient;
import com.develop.mvp.pk.module.system.api.social.remote.SocialClientRemoteClient;
import com.develop.mvp.pk.module.system.api.social.remote.SocialUserRemoteClient;
import com.develop.mvp.pk.module.system.api.user.remote.AdminUserRemoteClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration(value = "tradeRpcConfiguration", proxyBeanMethods = false)
@EnableFeignClients(clients = {
        BargainActivityApi.class, BargainRecordApi.class, CombinationRecordApi.class,
        CouponApi.class, DiscountActivityApi.class, RewardActivityApi.class, SeckillActivityApi.class, PointActivityApi.class,
        MemberUserApi.class, MemberPointApi.class, MemberLevelApi.class, MemberAddressApi.class, MemberConfigRemoteClient.class,
        ProductSpuApi.class, ProductSkuApi.class, ProductCommentApi.class, ProductCategoryApi.class,
        PayOrderApi.class, PayRefundApi.class, PayTransferApi.class, PayWalletApi.class,
        AdminUserRemoteClient.class, NotifyMessageSendRemoteClient.class, SocialClientRemoteClient.class, SocialUserRemoteClient.class
})
public class RpcConfiguration {
}
