package com.develop.mvp.pk.module.pay.application.app;
// DDD 角色：支付应用应用服务 - AggregateRoot_Pay_Skill
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.domain.app.PayApp;
import com.develop.mvp.pk.module.pay.domain.app.PayAppFactory;
import com.develop.mvp.pk.module.pay.domain.app.repository.PayAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
@Service
@RequiredArgsConstructor
public class PayAppApplicationService {
    private final PayAppRepository repo;
    @Transactional public PayApp create(String name, String appKey) {
        PayApp app = PayAppFactory.create(null, name, appKey);
        return repo.save(app);
    }
    @Transactional public void update(Long id, String name, String appKey, String remark,
                                       String orderNotifyUrl, String refundNotifyUrl, String transferNotifyUrl) {
        PayApp app = repo.findById(id);
        if (app == null) throw new IllegalArgumentException("App not found: " + id);
        app.appKey(appKey).remark(remark).orderNotifyUrl(orderNotifyUrl)
                .refundNotifyUrl(refundNotifyUrl).transferNotifyUrl(transferNotifyUrl);
        repo.save(app);
    }
    @Transactional public void updateStatus(Long id, Integer status) {
        PayApp app = repo.findById(id);
        if (app == null) throw new IllegalArgumentException("App not found: " + id);
        app.status(status);
        repo.save(app);
    }
    @Transactional public void delete(Long id) { repo.deleteById(id); }
    public PayApp get(Long id) { return repo.findById(id); }
    public PayApp getByAppKey(String appKey) { return repo.findByAppKey(appKey).orElse(null); }
    public List<PayApp> getList() { return repo.findAll(); }
    public List<PayApp> getList(Collection<Long> ids) { return repo.findByIds(ids); }
    public PageResult<PayApp> getPage(String name, String appKey, Integer status, Integer pageNo, Integer pageSize) {
        return repo.findPage(name, appKey, status, pageNo, pageSize);
    }
}
