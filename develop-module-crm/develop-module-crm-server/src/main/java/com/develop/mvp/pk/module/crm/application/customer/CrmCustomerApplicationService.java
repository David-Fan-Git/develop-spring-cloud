package com.develop.mvp.pk.module.crm.application.customer;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.crm.domain.customer.CrmCustomer;
import com.develop.mvp.pk.module.crm.domain.customer.repository.CrmCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class CrmCustomerApplicationService {
    private final CrmCustomerRepository repo;
    @Transactional public Long create(String name, Long ownerUserId, Integer status) { var c = CrmCustomer.of(null, name).ownerUserId(ownerUserId).status(status); repo.save(c); return c.id(); }
    @Transactional public void update(Long id, String name, Long ownerUserId, Integer status) { repo.save(CrmCustomer.of(id, name).ownerUserId(ownerUserId).status(status)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public CrmCustomer get(Long id) { return repo.findById(id); }
    public PageResult<CrmCustomer> getPage(String name, Long ownerUserId, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, ownerUserId, status, pageNo, pageSize); }
}
