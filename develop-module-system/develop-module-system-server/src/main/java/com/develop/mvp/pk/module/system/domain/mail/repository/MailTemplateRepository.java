package com.develop.mvp.pk.module.system.domain.mail.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.mail.MailTemplate;
import java.util.List;
import java.util.Optional;

public interface MailTemplateRepository {
    MailTemplate save(MailTemplate t);
    void delete(Long id);
    MailTemplate findById(Long id);
    Optional<MailTemplate> findByCode(String code);
    List<MailTemplate> findAll();
    PageResult<MailTemplate> findPage(String name, String code, Integer status, Integer pageNo, Integer pageSize);
    long countByAccountId(Long accountId);
}
