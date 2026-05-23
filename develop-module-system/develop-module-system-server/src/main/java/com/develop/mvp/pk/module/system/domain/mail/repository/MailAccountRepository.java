package com.develop.mvp.pk.module.system.domain.mail.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.mail.MailAccount;
import java.util.List;
import java.util.Optional;

public interface MailAccountRepository {
    MailAccount save(MailAccount a);
    void delete(Long id);
    MailAccount findById(Long id);
    Optional<MailAccount> findByMail(String mail);
    List<MailAccount> findAll();
    PageResult<MailAccount> findPage(String mail, String username, Integer pageNo, Integer pageSize);
}
