package com.develop.mvp.pk.module.erp.domain.product.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.erp.domain.product.ErpProduct;
import java.util.Optional;
public interface ErpProductRepository {
    ErpProduct save(ErpProduct p); void delete(Long id);
    ErpProduct findById(Long id); Optional<ErpProduct> findByNo(String no);
    PageResult<ErpProduct> findPage(String name, String no, Integer status, Integer pageNo, Integer pageSize);
}
