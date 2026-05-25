package com.develop.mvp.pk.module.system.application.mail.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.account.MailAccountSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.template.MailTemplateSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailAccountDO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailTemplateDO;
import com.develop.mvp.pk.module.system.domain.mail.MailAccount;
import com.develop.mvp.pk.module.system.domain.mail.MailTemplate;
import com.develop.mvp.pk.module.system.mq.message.mail.MailSendMessage;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * mail aggregate use-case boundary for legacy entries and new adapters.
 */
public interface MailUseCase {

    Long createMailAccount(MailAccountSaveReqVO createReqVO);

    void updateMailAccount(MailAccountSaveReqVO updateReqVO);

    void deleteMailAccount(Long id);

    void deleteMailAccountList(List<Long> ids);

    MailAccountDO getMailAccount(Long id);

    MailAccountDO getMailAccountFromCache(Long id);

    PageResult<MailAccountDO> getMailAccountPage(MailAccountPageReqVO pageReqVO);

    List<MailAccountDO> getMailAccountList();

    Long createMailTemplate(MailTemplateSaveReqVO createReqVO);

    void updateMailTemplate(MailTemplateSaveReqVO updateReqVO);

    void deleteMailTemplate(Long id);

    void deleteMailTemplateList(List<Long> ids);

    MailTemplateDO getMailTemplate(Long id);

    MailTemplateDO getMailTemplateByCodeFromCache(String code);

    PageResult<MailTemplateDO> getMailTemplatePage(MailTemplatePageReqVO pageReqVO);

    List<MailTemplateDO> getMailTemplateList();

    String formatMailTemplateContent(String content, Map<String, Object> params);

    long getMailTemplateCountByAccountId(Long accountId);

    PageResult<MailLogDO> getMailLogPage(MailLogPageReqVO pageVO);

    MailLogDO getMailLog(Long id);

    Long createMailLog(Long userId, Integer userType,
                       Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                       MailAccountDO account, MailTemplateDO template,
                       String templateContent, Map<String, Object> templateParams, Boolean isSend);

    void updateMailSendResult(Long logId, String messageId, Exception exception);

    Long sendSingleMailToAdmin(Long userId,
                               Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                               String templateCode, Map<String, Object> templateParams,
                               File... attachments);

    Long sendSingleMailToMember(Long userId,
                                Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                String templateCode, Map<String, Object> templateParams,
                                File... attachments);

    Long sendSingleMail(Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                        Long userId, Integer userType,
                        String templateCode, Map<String, Object> templateParams,
                        File... attachments);

    void doSendMail(MailSendMessage message);

    Long createAccountDomain(String mail, String username, String password, String host,
                            String port, Boolean sslEnable, Boolean starttlsEnable);

    void updateAccountDomain(Long id, String mail, String username, String password,
                            String host, String port, Boolean sslEnable, Boolean starttlsEnable);

    void deleteAccountDomain(Long id);

    MailAccount getAccountDomain(Long id);

    List<MailAccount> getAccountDomainList();

    PageResult<MailAccount> getAccountDomainPage(String mail, String username, Integer pageNo, Integer pageSize);

    Long createTemplateDomain(String code, String name, Long accountId, String nickname,
                             String title, String content, Integer status, String remark);

    void updateTemplateDomain(Long id, String code, String name, Long accountId, String nickname,
                             String title, String content, Integer status, String remark);

    void deleteTemplateDomain(Long id);

    MailTemplate getTemplateDomain(Long id);

    MailTemplate getTemplateDomainByCode(String code);

    List<MailTemplate> getTemplateDomainList();

    PageResult<MailTemplate> getTemplateDomainPage(String name, String code, Integer status,
                                                   Integer pageNo, Integer pageSize);

    long getTemplateDomainCountByAccountId(Long accountId);
}
