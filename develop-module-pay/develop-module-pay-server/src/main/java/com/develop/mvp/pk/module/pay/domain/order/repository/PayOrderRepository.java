package com.develop.mvp.pk.module.pay.domain.order.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.domain.order.PayOrder;
import java.util.Optional;

public interface PayOrderRepository {
    PayOrder save(PayOrder o); PayOrder findById(Long id);
    Optional<PayOrder> findByNo(String no); Optional<PayOrder> findByMerchantOrderId(String id);
    PageResult<PayOrder> findPage(Long appId, Long channelId, Integer status, Integer pageNo, Integer pageSize);
}
