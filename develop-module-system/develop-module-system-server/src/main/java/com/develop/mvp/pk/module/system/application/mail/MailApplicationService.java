package com.develop.mvp.pk.module.system.application.mail;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.mail.MailAccount;
import com.develop.mvp.pk.module.system.domain.mail.MailTemplate;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailAccountRepository;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class MailApplicationService {
    private final MailAccountRepository accountRepo;
    private final MailTemplateRepository templateRepo;

    @Transactional public Long createAccount(String mail, String username, String password, String host, String port, Boolean sslEnable, Boolean starttlsEnable) {
        MailAccount a = MailAccount.of(null, mail).username(username).password(password).host(host).port(port).sslEnable(sslEnable).starttlsEnable(starttlsEnable);
        accountRepo.save(a); return a.id();
    }
    @Transactional public void updateAccount(Long id, String mail, String username, String password, String host, String port, Boolean sslEnable, Boolean starttlsEnable) {
        if (accountRepo.findById(id) == null) throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        accountRepo.save(MailAccount.of(id, mail).username(username).password(password).host(host).port(port).sslEnable(sslEnable).starttlsEnable(starttlsEnable));
    }
    @Transactional public void deleteAccount(Long id) { accountRepo.delete(id); }
    public MailAccount getAccount(Long id) { return accountRepo.findById(id); }
    public List<MailAccount> getAccountList() { return accountRepo.findAll(); }
    public PageResult<MailAccount> getAccountPage(String mail, String username, Integer pageNo, Integer pageSize) { return accountRepo.findPage(mail, username, pageNo, pageSize); }

    @Transactional public Long createTemplate(String code, String name, Long accountId, String nickname, String title, String content, Integer status, String remark) {
        MailTemplate t = MailTemplate.of(null, code, name).accountId(accountId).nickname(nickname).title(title).content(content).status(status).remark(remark);
        templateRepo.save(t); return t.id();
    }
    @Transactional public void updateTemplate(Long id, String code, String name, Long accountId, String nickname, String title, String content, Integer status, String remark) {
        if (templateRepo.findById(id) == null) throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        templateRepo.save(MailTemplate.of(id, code, name).accountId(accountId).nickname(nickname).title(title).content(content).status(status).remark(remark));
    }
    @Transactional public void deleteTemplate(Long id) { templateRepo.delete(id); }
    public MailTemplate getTemplate(Long id) { return templateRepo.findById(id); }
    public MailTemplate getTemplateByCode(String code) { return templateRepo.findByCode(code).orElse(null); }
    public List<MailTemplate> getTemplateList() { return templateRepo.findAll(); }
    public PageResult<MailTemplate> getTemplatePage(String name, String code, Integer status, Integer pageNo, Integer pageSize) { return templateRepo.findPage(name, code, status, pageNo, pageSize); }
    public long getTemplateCountByAccountId(Long accountId) { return templateRepo.countByAccountId(accountId); }
}
