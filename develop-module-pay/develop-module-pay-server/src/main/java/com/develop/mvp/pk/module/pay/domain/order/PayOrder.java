package com.develop.mvp.pk.module.pay.domain.order;
// DDD 角色：支付订单聚合根
import java.util.Objects;
public final class PayOrder {
    private final Long id; private final String no; private Long appId, channelId; private Integer status, price, refundPrice;
    private String channelCode, merchantOrderId, subject, body, userIp, channelOrderNo, channelUserId;
    public PayOrder(Long id, String no) { this.id = id; this.no = Objects.requireNonNull(no); }
    public static PayOrder of(Long id, String no) { return new PayOrder(id, no); }
    public Long id() { return id; } public String no() { return no; }
    public Long appId() { return appId; } public Long channelId() { return channelId; }
    public Integer status() { return status; } public Integer price() { return price; }
    public String merchantOrderId() { return merchantOrderId; } public String subject() { return subject; }
    public PayOrder appId(Long v) { appId = v; return this; } public PayOrder channelId(Long v) { channelId = v; return this; }
    public PayOrder status(Integer v) { status = v; return this; } public PayOrder price(Integer v) { price = v; return this; }
    public PayOrder merchantOrderId(String v) { merchantOrderId = v; return this; } public PayOrder subject(String v) { subject = v; return this; }
    public PayOrder channelCode(String v) { channelCode = v; return this; } public PayOrder channelOrderNo(String v) { channelOrderNo = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof PayOrder or && id.equals(or.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
