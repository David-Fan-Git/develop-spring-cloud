package com.develop.mvp.pk.module.mp.domain.account.repository;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.mp.domain.account.MpAccount;
import java.util.*;
public interface MpAccountRepository {
    MpAccount save(MpAccount a); void delete(Long id);
    MpAccount findById(Long id); Optional<MpAccount> findByAppId(String appId);
    List<MpAccount> findAll();
    PageResult<MpAccount> findPage(String name, String appId, Integer type, Integer status, Integer pageNo, Integer pageSize);
}
