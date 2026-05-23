package com.develop.mvp.pk.module.crm.domain.customer.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.crm.domain.customer.CrmCustomer;
import java.util.*;
public interface CrmCustomerRepository {
    CrmCustomer save(CrmCustomer c); void delete(Long id);
    CrmCustomer findById(Long id); List<CrmCustomer> findByIds(Collection<Long> ids);
    PageResult<CrmCustomer> findPage(String name, Long ownerUserId, Integer status, Integer pageNo, Integer pageSize);
}
