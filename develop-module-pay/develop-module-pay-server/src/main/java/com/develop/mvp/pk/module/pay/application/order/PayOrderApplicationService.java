package com.develop.mvp.pk.module.pay.application.order;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.domain.order.PayOrder;
import com.develop.mvp.pk.module.pay.domain.order.repository.PayOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PayOrderApplicationService {
    private final PayOrderRepository repo;

    @Transactional public PayOrder create(String no, Long appId, Long channelId, String merchantOrderId, String subject, Integer price, String channelCode) {
        var o = PayOrder.of(null, no).appId(appId).channelId(channelId).merchantOrderId(merchantOrderId).subject(subject).price(price).channelCode(channelCode).status(0);
        return repo.save(o); }
    @Transactional public void update(Long id, Integer status, String channelOrderNo) {
        var o = repo.findById(id);
        if (o != null) repo.save(PayOrder.of(id, o.no()).status(status).channelOrderNo(channelOrderNo)); }
    public PayOrder get(Long id) { return repo.findById(id); }
    public PayOrder getByNo(String no) { return repo.findByNo(no).orElse(null); }
    public PageResult<PayOrder> getPage(Long appId, Long channelId, Integer status, Integer pageNo, Integer pageSize) {
        return repo.findPage(appId, channelId, status, pageNo, pageSize); }
}
