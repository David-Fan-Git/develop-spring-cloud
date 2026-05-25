package com.develop.mvp.pk.module.system.application.mail.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.enums.UserTypeEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.application.mail.port.inbound.MailUseCase;
import com.develop.mvp.pk.module.system.application.member.service.MemberApplicationService;
import com.develop.mvp.pk.module.system.application.user.service.AdminUserApplicationService;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.account.MailAccountSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.log.MailLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.template.MailTemplateSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailAccountDO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailTemplateDO;
import com.develop.mvp.pk.module.system.dal.dataobject.user.AdminUserDO;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailAccountMapper;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailLogMapper;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailTemplateMapper;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.develop.mvp.pk.module.system.domain.mail.MailAccount;
import com.develop.mvp.pk.module.system.domain.mail.MailTemplate;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailAccountRepository;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailTemplateRepository;
import com.develop.mvp.pk.module.system.enums.mail.MailSendStatusEnum;
import com.develop.mvp.pk.module.system.mq.message.mail.MailSendMessage;
import com.develop.mvp.pk.module.system.mq.producer.mail.MailProducer;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.dromara.hutool.extra.mail.MailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.hutool.core.exceptions.ExceptionUtil.getRootCauseMessage;
import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
@Validated
public class MailApplicationService implements MailUseCase {

    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    @Autowired(required = false)
    private MailAccountRepository accountRepo;
    @Autowired(required = false)
    private MailTemplateRepository templateRepo;

    @Resource
    private MailAccountMapper mailAccountMapper;
    @Resource
    private MailTemplateMapper mailTemplateMapper;
    @Resource
    private MailLogMapper mailLogMapper;
    @Resource
    private AdminUserApplicationService adminUserService;
    @Resource
    private MemberApplicationService memberApplicationService;
    @Resource
    private MailProducer mailProducer;

    public Long createMailAccount(MailAccountSaveReqVO createReqVO) {
        MailAccountDO account = BeanUtils.toBean(createReqVO, MailAccountDO.class);
        mailAccountMapper.insert(account);
        return account.getId();
    }

    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#updateReqVO.id")
    public void updateMailAccount(MailAccountSaveReqVO updateReqVO) {
        validateMailAccountExists(updateReqVO.getId());
        MailAccountDO updateObj = BeanUtils.toBean(updateReqVO, MailAccountDO.class);
        mailAccountMapper.updateById(updateObj);
    }

    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id")
    public void deleteMailAccount(Long id) {
        validateMailAccountExists(id);
        if (getMailTemplateCountByAccountId(id) > 0) {
            throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
        }
        mailAccountMapper.deleteById(id);
    }

    @CacheEvict(value = RedisKeyConstants.MAIL_ACCOUNT, allEntries = true)
    public void deleteMailAccountList(List<Long> ids) {
        for (Long id : ids) {
            if (getMailTemplateCountByAccountId(id) > 0) {
                throw exception(MAIL_ACCOUNT_RELATE_TEMPLATE_EXISTS);
            }
        }
        mailAccountMapper.deleteByIds(ids);
    }

    private void validateMailAccountExists(Long id) {
        if (mailAccountMapper.selectById(id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
    }

    public MailAccountDO getMailAccount(Long id) {
        return mailAccountMapper.selectById(id);
    }

    @Cacheable(value = RedisKeyConstants.MAIL_ACCOUNT, key = "#id", unless = "#result == null")
    public MailAccountDO getMailAccountFromCache(Long id) {
        return getMailAccount(id);
    }

    public PageResult<MailAccountDO> getMailAccountPage(MailAccountPageReqVO pageReqVO) {
        return mailAccountMapper.selectPage(pageReqVO);
    }

    public List<MailAccountDO> getMailAccountList() {
        return mailAccountMapper.selectList();
    }

    public Long createMailTemplate(MailTemplateSaveReqVO createReqVO) {
        validateCodeUnique(null, createReqVO.getCode());
        MailTemplateDO template = BeanUtils.toBean(createReqVO, MailTemplateDO.class)
                .setParams(parseTemplateTitleAndContentParams(createReqVO.getTitle(), createReqVO.getContent()));
        mailTemplateMapper.insert(template);
        return template.getId();
    }

    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE, allEntries = true)
    public void updateMailTemplate(MailTemplateSaveReqVO updateReqVO) {
        validateMailTemplateExists(updateReqVO.getId());
        validateCodeUnique(updateReqVO.getId(), updateReqVO.getCode());
        MailTemplateDO updateObj = BeanUtils.toBean(updateReqVO, MailTemplateDO.class)
                .setParams(parseTemplateTitleAndContentParams(updateReqVO.getTitle(), updateReqVO.getContent()));
        mailTemplateMapper.updateById(updateObj);
    }

    @VisibleForTesting
    void validateCodeUnique(Long id, String code) {
        MailTemplateDO template = mailTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        if (id == null || ObjUtil.notEqual(id, template.getId())) {
            throw exception(MAIL_TEMPLATE_CODE_EXISTS);
        }
    }

    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE, allEntries = true)
    public void deleteMailTemplate(Long id) {
        validateMailTemplateExists(id);
        mailTemplateMapper.deleteById(id);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.MAIL_TEMPLATE, allEntries = true)
    public void deleteMailTemplateList(List<Long> ids) {
        mailTemplateMapper.deleteByIds(ids);
    }

    private void validateMailTemplateExists(Long id) {
        if (mailTemplateMapper.selectById(id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
    }

    public MailTemplateDO getMailTemplate(Long id) {
        return mailTemplateMapper.selectById(id);
    }

    @Cacheable(value = RedisKeyConstants.MAIL_TEMPLATE, key = "#code", unless = "#result == null")
    public MailTemplateDO getMailTemplateByCodeFromCache(String code) {
        return mailTemplateMapper.selectByCode(code);
    }

    public PageResult<MailTemplateDO> getMailTemplatePage(MailTemplatePageReqVO pageReqVO) {
        return mailTemplateMapper.selectPage(pageReqVO);
    }

    public List<MailTemplateDO> getMailTemplateList() {
        return mailTemplateMapper.selectList();
    }

    public String formatMailTemplateContent(String content, Map<String, Object> params) {
        String formattedContent = StrUtil.format(content, params);
        formattedContent = unescapeHtml(formattedContent);
        formattedContent = formatHtmlCodeBlocks(formattedContent);
        return replaceOuterPreWithDiv(formattedContent);
    }

    private String replaceOuterPreWithDiv(String content) {
        if (StrUtil.isEmpty(content)) {
            return content;
        }
        Matcher matcher = Pattern.compile("(?s)<pre[^>]*>(.*?)</pre>").matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "<div>" + matcher.group(1) + "</div>");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private String unescapeHtml(String input) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return input.replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&#39;", "'")
                .replace("&nbsp;", " ");
    }

    private String formatHtmlCodeBlocks(String content) {
        Matcher matcher = Pattern.compile("<pre\\s*.*?><code\\s*.*?>(.*?)</code></pre>", Pattern.DOTALL).matcher(content);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String codeBlock = matcher.group(1);
            String replacement = "<pre style=\"background-color: #f5f5f5; padding: 10px; border-radius: 5px; overflow-x: auto;\"><code>" + codeBlock + "</code></pre>";
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public long getMailTemplateCountByAccountId(Long accountId) {
        return mailTemplateMapper.selectCountByAccountId(accountId);
    }

    @VisibleForTesting
    public List<String> parseTemplateTitleAndContentParams(String title, String content) {
        List<String> titleParams = ReUtil.findAllGroup1(PATTERN_PARAMS, title);
        List<String> contentParams = ReUtil.findAllGroup1(PATTERN_PARAMS, content);
        List<String> allParams = new ArrayList<>(titleParams);
        for (String param : contentParams) {
            if (!allParams.contains(param)) {
                allParams.add(param);
            }
        }
        return allParams;
    }

    List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    public PageResult<MailLogDO> getMailLogPage(MailLogPageReqVO pageVO) {
        return mailLogMapper.selectPage(pageVO);
    }

    public MailLogDO getMailLog(Long id) {
        return mailLogMapper.selectById(id);
    }

    public Long createMailLog(Long userId, Integer userType,
                              Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                              MailAccountDO account, MailTemplateDO template,
                              String templateContent, Map<String, Object> templateParams, Boolean isSend) {
        MailLogDO logDO = MailLogDO.builder()
                .sendStatus(Objects.equals(isSend, true) ? MailSendStatusEnum.INIT.getStatus() : MailSendStatusEnum.IGNORE.getStatus())
                .userId(userId).userType(userType)
                .toMails(ListUtil.toList(toMails)).ccMails(ListUtil.toList(ccMails)).bccMails(ListUtil.toList(bccMails))
                .accountId(account.getId()).fromMail(account.getMail())
                .templateId(template.getId()).templateCode(template.getCode()).templateNickname(template.getNickname())
                .templateTitle(template.getTitle()).templateContent(templateContent).templateParams(templateParams)
                .build();
        mailLogMapper.insert(logDO);
        return logDO.getId();
    }

    public void updateMailSendResult(Long logId, String messageId, Exception exception) {
        if (exception == null) {
            mailLogMapper.updateById(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                    .setSendStatus(MailSendStatusEnum.SUCCESS.getStatus()).setSendMessageId(messageId));
            return;
        }
        mailLogMapper.updateById(new MailLogDO().setId(logId).setSendTime(LocalDateTime.now())
                .setSendStatus(MailSendStatusEnum.FAILURE.getStatus()).setSendException(getRootCauseMessage(exception)));
    }

    public Long sendSingleMailToAdmin(Long userId,
                                      Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                      String templateCode, Map<String, Object> templateParams,
                                      File... attachments) {
        return sendSingleMail(toMails, ccMails, bccMails, userId, UserTypeEnum.ADMIN.getValue(),
                templateCode, templateParams, attachments);
    }

    public Long sendSingleMailToMember(Long userId,
                                       Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                                       String templateCode, Map<String, Object> templateParams,
                                       File... attachments) {
        return sendSingleMail(toMails, ccMails, bccMails, userId, UserTypeEnum.MEMBER.getValue(),
                templateCode, templateParams, attachments);
    }

    public Long sendSingleMail(Collection<String> toMails, Collection<String> ccMails, Collection<String> bccMails,
                               Long userId, Integer userType,
                               String templateCode, Map<String, Object> templateParams,
                               File... attachments) {
        MailTemplateDO template = validateMailTemplate(templateCode);
        MailAccountDO account = validateMailAccount(template.getAccountId());
        validateTemplateParams(template, templateParams);
        String userMail = getUserMail(userId, userType);
        Collection<String> toMailSet = new LinkedHashSet<>();
        Collection<String> ccMailSet = new LinkedHashSet<>();
        Collection<String> bccMailSet = new LinkedHashSet<>();
        if (Validator.isEmail(userMail)) {
            toMailSet.add(userMail);
        }
        if (CollUtil.isNotEmpty(toMails)) {
            toMails.stream().filter(Validator::isEmail).forEach(toMailSet::add);
        }
        if (CollUtil.isNotEmpty(ccMails)) {
            ccMails.stream().filter(Validator::isEmail).forEach(ccMailSet::add);
        }
        if (CollUtil.isNotEmpty(bccMails)) {
            bccMails.stream().filter(Validator::isEmail).forEach(bccMailSet::add);
        }
        if (CollUtil.isEmpty(toMailSet)) {
            throw exception(MAIL_SEND_MAIL_NOT_EXISTS);
        }
        Boolean isSend = CommonStatusEnum.ENABLE.getStatus().equals(template.getStatus());
        String title = formatMailTemplateContent(template.getTitle(), templateParams);
        String content = formatMailTemplateContent(template.getContent(), templateParams);
        Long sendLogId = createMailLog(userId, userType, toMailSet, ccMailSet, bccMailSet,
                account, template, content, templateParams, isSend);
        if (isSend) {
            mailProducer.sendMailSendMessage(sendLogId, toMailSet, ccMailSet, bccMailSet,
                    account.getId(), template.getNickname(), title, content, attachments);
        }
        return sendLogId;
    }

    private String getUserMail(Long userId, Integer userType) {
        if (userId == null || userType == null) {
            return null;
        }
        if (UserTypeEnum.ADMIN.getValue().equals(userType)) {
            AdminUserDO user = adminUserService.getUser(userId);
            return user != null ? user.getEmail() : null;
        }
        if (UserTypeEnum.MEMBER.getValue().equals(userType)) {
            return memberApplicationService.getMemberUserEmail(userId);
        }
        return null;
    }

    public void doSendMail(MailSendMessage message) {
        MailAccountDO account = validateMailAccount(message.getAccountId());
        org.dromara.hutool.extra.mail.MailAccount mailAccount = buildMailAccount(account, message.getNickname());
        try {
            String messageId = MailUtil.send(mailAccount, message.getToMails(), message.getCcMails(), message.getBccMails(),
                    message.getTitle(), message.getContent(), true, message.getAttachments());
            updateMailSendResult(message.getLogId(), messageId, null);
        } catch (Exception e) {
            updateMailSendResult(message.getLogId(), null, e);
        }
    }

    private org.dromara.hutool.extra.mail.MailAccount buildMailAccount(MailAccountDO account, String nickname) {
        String from = StrUtil.isNotEmpty(nickname) ? nickname + " <" + account.getMail() + ">" : account.getMail();
        return new org.dromara.hutool.extra.mail.MailAccount().setFrom(from).setAuth(true)
                .setUser(account.getUsername()).setPass(account.getPassword().toCharArray())
                .setHost(account.getHost()).setPort(account.getPort())
                .setSslEnable(account.getSslEnable()).setStarttlsEnable(account.getStarttlsEnable());
    }

    @VisibleForTesting
    MailTemplateDO validateMailTemplate(String templateCode) {
        MailTemplateDO template = getMailTemplateByCodeFromCache(templateCode);
        if (template == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    @VisibleForTesting
    MailAccountDO validateMailAccount(Long accountId) {
        MailAccountDO account = getMailAccountFromCache(accountId);
        if (account == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
        return account;
    }

    @VisibleForTesting
    void validateTemplateParams(MailTemplateDO template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(MAIL_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

    @Transactional
    public Long createAccountDomain(String mail, String username, String password, String host, String port, Boolean sslEnable, Boolean starttlsEnable) {
        MailAccount account = MailAccount.of(null, mail).username(username).password(password).host(host).port(port).sslEnable(sslEnable).starttlsEnable(starttlsEnable);
        accountRepo.save(account);
        return account.id();
    }

    @Transactional
    public void updateAccountDomain(Long id, String mail, String username, String password, String host, String port, Boolean sslEnable, Boolean starttlsEnable) {
        if (accountRepo.findById(id) == null) {
            throw exception(MAIL_ACCOUNT_NOT_EXISTS);
        }
        accountRepo.save(MailAccount.of(id, mail).username(username).password(password).host(host).port(port).sslEnable(sslEnable).starttlsEnable(starttlsEnable));
    }

    @Transactional
    public void deleteAccountDomain(Long id) {
        accountRepo.delete(id);
    }

    public MailAccount getAccountDomain(Long id) {
        return accountRepo.findById(id);
    }

    public List<MailAccount> getAccountDomainList() {
        return accountRepo.findAll();
    }

    public PageResult<MailAccount> getAccountDomainPage(String mail, String username, Integer pageNo, Integer pageSize) {
        return accountRepo.findPage(mail, username, pageNo, pageSize);
    }

    @Transactional
    public Long createTemplateDomain(String code, String name, Long accountId, String nickname, String title, String content, Integer status, String remark) {
        MailTemplate template = MailTemplate.of(null, code, name).accountId(accountId).nickname(nickname).title(title).content(content).status(status).remark(remark);
        templateRepo.save(template);
        return template.id();
    }

    @Transactional
    public void updateTemplateDomain(Long id, String code, String name, Long accountId, String nickname, String title, String content, Integer status, String remark) {
        if (templateRepo.findById(id) == null) {
            throw exception(MAIL_TEMPLATE_NOT_EXISTS);
        }
        templateRepo.save(MailTemplate.of(id, code, name).accountId(accountId).nickname(nickname).title(title).content(content).status(status).remark(remark));
    }

    @Transactional
    public void deleteTemplateDomain(Long id) {
        templateRepo.delete(id);
    }

    public MailTemplate getTemplateDomain(Long id) {
        return templateRepo.findById(id);
    }

    public MailTemplate getTemplateDomainByCode(String code) {
        return templateRepo.findByCode(code).orElse(null);
    }

    public List<MailTemplate> getTemplateDomainList() {
        return templateRepo.findAll();
    }

    public PageResult<MailTemplate> getTemplateDomainPage(String name, String code, Integer status, Integer pageNo, Integer pageSize) {
        return templateRepo.findPage(name, code, status, pageNo, pageSize);
    }

    public long getTemplateDomainCountByAccountId(Long accountId) {
        return templateRepo.countByAccountId(accountId);
    }
}
