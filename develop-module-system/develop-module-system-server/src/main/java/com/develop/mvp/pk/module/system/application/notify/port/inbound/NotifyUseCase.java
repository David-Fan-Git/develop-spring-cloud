package com.develop.mvp.pk.module.system.application.notify.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.message.NotifyMessageMyPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.message.NotifyMessagePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.template.NotifyTemplateSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notify.NotifyMessageDO;
import com.develop.mvp.pk.module.system.dal.dataobject.notify.NotifyTemplateDO;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * notify use-case boundary for legacy service compatibility and future adapters.
 */
public interface NotifyUseCase {

    Long createNotifyMessage(Long userId, Integer userType,
                             NotifyTemplateDO template, String templateContent, Map<String, Object> templateParams);

    PageResult<NotifyMessageDO> getNotifyMessagePage(NotifyMessagePageReqVO pageReqVO);

    PageResult<NotifyMessageDO> getMyMyNotifyMessagePage(NotifyMessageMyPageReqVO pageReqVO,
                                                          Long userId, Integer userType);

    NotifyMessageDO getNotifyMessage(Long id);

    List<NotifyMessageDO> getUnreadNotifyMessageList(Long userId, Integer userType, Integer size);

    Long getUnreadNotifyMessageCount(Long userId, Integer userType);

    int updateNotifyMessageRead(Collection<Long> ids, Long userId, Integer userType);

    int updateAllNotifyMessageRead(Long userId, Integer userType);

    Long createNotifyTemplate(NotifyTemplateSaveReqVO createReqVO);

    void updateNotifyTemplate(NotifyTemplateSaveReqVO updateReqVO);

    void deleteNotifyTemplate(Long id);

    void deleteNotifyTemplateList(List<Long> ids);

    NotifyTemplateDO getNotifyTemplate(Long id);

    NotifyTemplateDO getNotifyTemplateByCodeFromCache(String code);

    PageResult<NotifyTemplateDO> getNotifyTemplatePage(NotifyTemplatePageReqVO pageReqVO);

    String formatNotifyTemplateContent(String content, Map<String, Object> params);

    Long sendSingleNotifyToAdmin(Long userId, String templateCode, Map<String, Object> templateParams);

    Long sendSingleNotifyToMember(Long userId, String templateCode, Map<String, Object> templateParams);

    Long sendSingleNotify(Long userId, Integer userType, String templateCode, Map<String, Object> templateParams);

    void sendBatchNotify(List<String> mobiles, List<Long> userIds, Integer userType,
                         String templateCode, Map<String, Object> templateParams);
}
