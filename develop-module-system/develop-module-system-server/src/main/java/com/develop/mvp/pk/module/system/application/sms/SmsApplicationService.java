package com.develop.mvp.pk.module.system.application.sms;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.sms.SmsChannel;
import com.develop.mvp.pk.module.system.domain.sms.repository.SmsChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SmsApplicationService {
    private final SmsChannelRepository channelRepo;

    @Transactional public Long createChannel(String code, String signature, Integer status, String apiKey, String apiSecret, String callbackUrl, String remark) {
        var c = SmsChannel.of(null, code, signature).status(status).apiKey(apiKey).apiSecret(apiSecret).callbackUrl(callbackUrl).remark(remark);
        channelRepo.save(c); return c.id();
    }
    @Transactional public void updateChannel(Long id, String code, String signature, Integer status, String apiKey, String apiSecret, String callbackUrl, String remark) {
        channelRepo.save(SmsChannel.of(id, code, signature).status(status).apiKey(apiKey).apiSecret(apiSecret).callbackUrl(callbackUrl).remark(remark));
    }
    @Transactional public void deleteChannel(Long id) { channelRepo.delete(id); }
    public SmsChannel getChannel(Long id) { return channelRepo.findById(id); }
    public SmsChannel getChannelByCode(String code) { return channelRepo.findByCode(code); }
    public List<SmsChannel> getChannelList() { return channelRepo.findAll(); }
    public PageResult<SmsChannel> getChannelPage(String signature, Integer status, Integer pageNo, Integer pageSize) { return channelRepo.findPage(signature, status, pageNo, pageSize); }
}
