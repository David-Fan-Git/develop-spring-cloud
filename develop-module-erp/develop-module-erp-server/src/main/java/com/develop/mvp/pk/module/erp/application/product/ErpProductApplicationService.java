package com.develop.mvp.pk.module.erp.application.product;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.erp.domain.product.ErpProduct;
import com.develop.mvp.pk.module.erp.domain.product.repository.ErpProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class ErpProductApplicationService {
    private final ErpProductRepository repo;
    @Transactional public Long create(String name, String no, String unit, Integer status, Integer price) { var p = ErpProduct.of(null, name).no(no).unit(unit).status(status).price(price); repo.save(p); return p.id(); }
    @Transactional public void update(Long id, String name, String no, String unit, Integer status, Integer price) { repo.save(ErpProduct.of(id, name).no(no).unit(unit).status(status).price(price)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public ErpProduct get(Long id) { return repo.findById(id); }
    public PageResult<ErpProduct> getPage(String name, String no, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, no, status, pageNo, pageSize); }
}
