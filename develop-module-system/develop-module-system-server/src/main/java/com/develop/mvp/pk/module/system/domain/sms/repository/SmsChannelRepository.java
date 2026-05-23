package com.develop.mvp.pk.module.system.domain.sms.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.sms.SmsChannel;
import java.util.List;

public interface SmsChannelRepository {
    SmsChannel save(SmsChannel c);
    void delete(Long id);
    SmsChannel findById(Long id);
    SmsChannel findByCode(String code);
    List<SmsChannel> findAll();
    PageResult<SmsChannel> findPage(String signature, Integer status, Integer pageNo, Integer pageSize);
}
