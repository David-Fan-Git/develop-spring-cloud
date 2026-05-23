package com.develop.mvp.pk.module.pay.application.channel;
// DDD 角色：支付渠道应用服务 - AggregateRoot_Pay_Skill
import com.develop.mvp.pk.module.pay.domain.channel.PayChannel;
import com.develop.mvp.pk.module.pay.domain.channel.PayChannelFactory;
import com.develop.mvp.pk.module.pay.domain.channel.repository.PayChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PayChannelApplicationService {
    private final PayChannelRepository repo;
    @Transactional public PayChannel create(String code, Long appId, Double feeRate, Object config) {
        PayChannel channel = PayChannelFactory.create(null, code, appId, feeRate, config);
        return repo.save(channel);
    }
    @Transactional public void update(Long id, Double feeRate, String remark, Object config) {
        PayChannel channel = repo.findById(id);
        if (channel == null) throw new IllegalArgumentException("Channel not found: " + id);
        channel.feeRate(feeRate).remark(remark).config(config);
        repo.save(channel);
    }
    @Transactional public void delete(Long id) { repo.deleteById(id); }
    public PayChannel get(Long id) { return repo.findById(id); }
    public PayChannel getByAppIdAndCode(Long appId, String code) {
        return repo.findByAppIdAndCode(appId, code).orElse(null);
    }
    public List<PayChannel> getListByAppIds(Collection<Long> appIds) { return repo.findByAppIds(appIds); }
    public List<PayChannel> getEnabledList(Long appId) { return repo.findEnabledByAppId(appId); }
}
