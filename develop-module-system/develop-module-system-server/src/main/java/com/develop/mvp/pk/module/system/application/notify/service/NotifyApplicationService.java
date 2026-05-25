package com.develop.mvp.pk.module.system.application.notify.service;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.enums.UserTypeEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.object.BeanUtils;
import com.develop.mvp.pk.module.system.application.notify.port.inbound.NotifyUseCase;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.develop.mvp.pk.module.system.dal.dataobject.notify.NotifyTemplateDO;
import com.develop.mvp.pk.module.system.dal.mysql.notify.NotifyMessageMapper;
import com.develop.mvp.pk.module.system.dal.mysql.notify.NotifyTemplateMapper;
import com.develop.mvp.pk.module.system.dal.redis.RedisKeyConstants;
import com.google.common.annotations.VisibleForTesting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Slf4j
public class NotifyApplicationService implements NotifyUseCase {

    private static final Pattern PATTERN_PARAMS = Pattern.compile("\\{(.*?)}");

    private final NotifyMessageMapper notifyMessageMapper;
    private final NotifyTemplateMapper notifyTemplateMapper;

    public NotifyApplicationService(NotifyMessageMapper notifyMessageMapper,
                                     NotifyTemplateMapper notifyTemplateMapper) {
        this.notifyMessageMapper = notifyMessageMapper;
        this.notifyTemplateMapper = notifyTemplateMapper;
    }

    public Long createNotifyMessage(Long userId, Integer userType,
                                    NotifyTemplateDO template, String templateContent, Map<String, Object> templateParams) {
        NotifyMessageDO message = new NotifyMessageDO().setUserId(userId).setUserType(userType)
                .setTemplateId(template.getId()).setTemplateCode(template.getCode())
                .setTemplateType(template.getType()).setTemplateNickname(template.getNickname())
                .setTemplateContent(templateContent).setTemplateParams(templateParams).setReadStatus(false);
        notifyMessageMapper.insert(message);
        return message.getId();
    }

    public PageResult<NotifyMessageDO> getNotifyMessagePage(NotifyMessagePageReqVO pageReqVO) {
        return notifyMessageMapper.selectPage(pageReqVO);
    }

    public PageResult<NotifyMessageDO> getMyMyNotifyMessagePage(NotifyMessageMyPageReqVO pageReqVO, Long userId, Integer userType) {
        return notifyMessageMapper.selectPage(pageReqVO, userId, userType);
    }

    public NotifyMessageDO getNotifyMessage(Long id) {
        return notifyMessageMapper.selectById(id);
    }

    public List<NotifyMessageDO> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size) {
        return notifyMessageMapper.selectUnreadListByUserIdAndUserType(userId, userType, size);
    }

    public Long getUnreadNotifyMessageCount(Long userId, Integer userType) {
        return notifyMessageMapper.selectUnreadCountByUserIdAndUserType(userId, userType);
    }

    public int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(ids, userId, userType);
    }

    public int updateAllNotifyMessageRead(Long userId, Integer userType) {
        return notifyMessageMapper.updateListRead(userId, userType);
    }

    public Long createNotifyTemplate(NotifyTemplateSaveReqVO createReqVO) {
        validateNotifyTemplateCodeDuplicate(null, createReqVO.getCode());
        NotifyTemplateDO notifyTemplate = BeanUtils.toBean(createReqVO, NotifyTemplateDO.class);
        notifyTemplate.setParams(parseTemplateContentParams(notifyTemplate.getContent()));
        notifyTemplateMapper.insert(notifyTemplate);
        return notifyTemplate.getId();
    }

    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, allEntries = true)
    public void updateNotifyTemplate(NotifyTemplateSaveReqVO updateReqVO) {
        validateNotifyTemplateExists(updateReqVO.getId());
        validateNotifyTemplateCodeDuplicate(updateReqVO.getId(), updateReqVO.getCode());
        NotifyTemplateDO updateObj = BeanUtils.toBean(updateReqVO, NotifyTemplateDO.class);
        updateObj.setParams(parseTemplateContentParams(updateObj.getContent()));
        notifyTemplateMapper.updateById(updateObj);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, allEntries = true)
    public void deleteNotifyTemplate(Long id) {
        validateNotifyTemplateExists(id);
        notifyTemplateMapper.deleteById(id);
    }

    @CacheEvict(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, allEntries = true)
    public void deleteNotifyTemplateList(List<Long> ids) {
        notifyTemplateMapper.deleteByIds(ids);
    }

    public NotifyTemplateDO getNotifyTemplate(Long id) {
        return notifyTemplateMapper.selectById(id);
    }

    @Cacheable(cacheNames = RedisKeyConstants.NOTIFY_TEMPLATE, key = "#code", unless = "#result == null")
    public NotifyTemplateDO getNotifyTemplateByCodeFromCache(String code) {
        return notifyTemplateMapper.selectByCode(code);
    }

    public PageResult<NotifyTemplateDO> getNotifyTemplatePage(NotifyTemplatePageReqVO pageReqVO) {
        return notifyTemplateMapper.selectPage(pageReqVO);
    }

    public String formatNotifyTemplateContent(String content, Map<String, Object> params) {
        return StrUtil.format(content, params);
    }

    public Long sendSingleNotifyToAdmin(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.ADMIN.getValue(), templateCode, templateParams);
    }

    public Long sendSingleNotifyToMember(Long userId, String templateCode, Map<String, Object> templateParams) {
        return sendSingleNotify(userId, UserTypeEnum.MEMBER.getValue(), templateCode, templateParams);
    }

    public Long sendSingleNotify(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams) {
        NotifyTemplateDO template = validateNotifyTemplate(templateCode);
        if (Objects.equals(template.getStatus(), CommonStatusEnum.DISABLE.getStatus())) {
            log.info("[sendSingleNotify][模版({})已经关闭，无法给用户({}/{})发送]", templateCode, userId, userType);
            return null;
        }
        validateTemplateParams(template, templateParams);
        String content = formatNotifyTemplateContent(template.getContent(), templateParams);
        return createNotifyMessage(userId, userType, template, content, templateParams);
    }

    public void sendBatchNotify(List<String> mobiles, List<Long> userIds, Integer userType,
                                String templateCode, Map<String, Object> templateParams) {
        throw new UnsupportedOperationException("暂时不支持该操作，感兴趣可以实现该功能哟！");
    }

    @VisibleForTesting
    public List<String> parseTemplateContentParams(String content) {
        return ReUtil.findAllGroup1(PATTERN_PARAMS, content);
    }

    @VisibleForTesting
    public void validateNotifyTemplateCodeDuplicate(Long id, String code) {
        NotifyTemplateDO template = notifyTemplateMapper.selectByCode(code);
        if (template == null) {
            return;
        }
        if (id == null) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
        if (!template.getId().equals(id)) {
            throw exception(NOTIFY_TEMPLATE_CODE_DUPLICATE, code);
        }
    }

    @VisibleForTesting
    public NotifyTemplateDO validateNotifyTemplate(String templateCode) {
        NotifyTemplateDO template = getNotifyTemplateByCodeFromCache(templateCode);
        if (template == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
        return template;
    }

    @VisibleForTesting
    public void validateTemplateParams(NotifyTemplateDO template, Map<String, Object> templateParams) {
        template.getParams().forEach(key -> {
            Object value = templateParams.get(key);
            if (value == null) {
                throw exception(NOTIFY_SEND_TEMPLATE_PARAM_MISS, key);
            }
        });
    }

    private void validateNotifyTemplateExists(Long id) {
        if (notifyTemplateMapper.selectById(id) == null) {
            throw exception(NOTIFY_TEMPLATE_NOT_EXISTS);
        }
    }
}
