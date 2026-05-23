package com.develop.mvp.pk.module.system.domain.notify.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.notify.NotifyMessage;
import java.util.*;

public interface NotifyMessageRepository {
    NotifyMessage save(NotifyMessage m);
    NotifyMessage findById(Long id);
    PageResult<NotifyMessage> findPage(Long userId, Integer userType, Integer readStatus, Integer pageNo, Integer pageSize);
    void updateReadStatus(Collection<Long> ids, Integer readStatus);
}
