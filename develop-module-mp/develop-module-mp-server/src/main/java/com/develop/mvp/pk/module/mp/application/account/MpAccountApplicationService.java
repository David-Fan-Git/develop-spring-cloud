package com.develop.mvp.pk.module.mp.application.account;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.mp.domain.account.MpAccount;
import com.develop.mvp.pk.module.mp.domain.account.repository.MpAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service @RequiredArgsConstructor
public class MpAccountApplicationService {
    private final MpAccountRepository repo;
    @Transactional public Long create(String name, String appId, String appSecret, String token, Integer type, Integer status) { var a = MpAccount.of(null, name).appId(appId).appSecret(appSecret).token(token).type(type).status(status); repo.save(a); return a.id(); }
    @Transactional public void update(Long id, String name, String appId, String appSecret, String token, Integer type, Integer status) { repo.save(MpAccount.of(id, name).appId(appId).appSecret(appSecret).token(token).type(type).status(status)); }
    @Transactional public void delete(Long id) { repo.delete(id); }
    public MpAccount get(Long id) { return repo.findById(id); }
    public List<MpAccount> getList() { return repo.findAll(); }
    public PageResult<MpAccount> getPage(String name, String appId, Integer type, Integer status, Integer pageNo, Integer pageSize) { return repo.findPage(name, appId, type, status, pageNo, pageSize); }
}
