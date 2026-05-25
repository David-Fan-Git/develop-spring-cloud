package com.develop.mvp.pk.module.system.application.sms.port.inbound;

import com.develop.mvp.pk.framework.common.core.KeyValue;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import com.develop.mvp.pk.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import com.develop.mvp.pk.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.channel.SmsChannelPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.channel.SmsChannelSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.log.SmsLogPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.template.SmsTemplatePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.sms.vo.template.SmsTemplateSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.sms.SmsChannelDO;
import com.develop.mvp.pk.module.system.dal.dataobject.sms.SmsLogDO;
import com.develop.mvp.pk.module.system.dal.dataobject.sms.SmsTemplateDO;
import com.develop.mvp.pk.module.system.domain.sms.SmsChannel;
import com.develop.mvp.pk.module.system.framework.sms.core.client.SmsClient;
import com.develop.mvp.pk.module.system.mq.message.sms.SmsSendMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * sms aggregate use-case boundary for legacy entries and new adapters.
 */
public interface SmsUseCase {

    Long createChannel(String code, String signature, Integer status,
                       String apiKey, String apiSecret, String callbackUrl, String remark);

    void updateChannel(Long id, String code, String signature, Integer status,
                       String apiKey, String apiSecret, String callbackUrl, String remark);

    void deleteChannel(Long id);

    SmsChannel getChannel(Long id);

    SmsChannel getChannelByCode(String code);

    List<SmsChannel> getChannelList();

    PageResult<SmsChannel> getChannelPage(String signature, Integer status, Integer pageNo, Integer pageSize);

    Long createSmsChannel(SmsChannelSaveReqVO createReqVO);

    void updateSmsChannel(SmsChannelSaveReqVO updateReqVO);

    void deleteSmsChannel(Long id);

    void deleteSmsChannelList(List<Long> ids);

    SmsChannelDO getSmsChannel(Long id);

    List<SmsChannelDO> getSmsChannelList();

    PageResult<SmsChannelDO> getSmsChannelPage(SmsChannelPageReqVO pageReqVO);

    SmsClient getSmsClient(Long id);

    SmsClient getSmsClient(String code);

    Long createSmsTemplate(SmsTemplateSaveReqVO createReqVO);

    void updateSmsTemplate(SmsTemplateSaveReqVO updateReqVO);

    void deleteSmsTemplate(Long id);

    void deleteSmsTemplateList(List<Long> ids);

    SmsTemplateDO getSmsTemplate(Long id);

    SmsTemplateDO getSmsTemplateByCodeFromCache(String code);

    PageResult<SmsTemplateDO> getSmsTemplatePage(SmsTemplatePageReqVO pageReqVO);

    Long getSmsTemplateCountByChannelId(Long channelId);

    String formatSmsTemplateContent(String content, Map<String, Object> params);

    Long createSmsLog(String mobile, Long userId, Integer userType, Boolean isSend,
                      SmsTemplateDO template, String templateContent, Map<String, Object> templateParams);

    void updateSmsSendResult(Long id, Boolean success,
                             String apiSendCode, String apiSendMsg,
                             String apiRequestId, String apiSerialNo);

    void updateSmsReceiveResult(Long id, String apiSerialNo, Boolean success, LocalDateTime receiveTime,
                                String apiReceiveCode, String apiReceiveMsg);

    SmsLogDO getSmsLog(Long id);

    PageResult<SmsLogDO> getSmsLogPage(SmsLogPageReqVO pageReqVO);

    Long sendSingleSmsToAdmin(String mobile, Long userId, String templateCode, Map<String, Object> templateParams);

    Long sendSingleSmsToMember(String mobile, Long userId, String templateCode, Map<String, Object> templateParams);

    Long sendSingleSms(String mobile, Long userId, Integer userType,
                       String templateCode, Map<String, Object> templateParams);

    void sendBatchSms(List<String> mobiles, List<Long> userIds, Integer userType,
                      String templateCode, Map<String, Object> templateParams);

    void doSendSms(SmsSendMessage message);

    void receiveSmsStatus(String channelCode, String text) throws Throwable;

    void sendSmsCode(SmsCodeSendReqDTO reqDTO);

    void useSmsCode(SmsCodeUseReqDTO reqDTO);

    void validateSmsCode(SmsCodeValidateReqDTO reqDTO);
}
