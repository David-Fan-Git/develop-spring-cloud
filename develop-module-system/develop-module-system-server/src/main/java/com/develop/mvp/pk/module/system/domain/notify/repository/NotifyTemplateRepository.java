package com.develop.mvp.pk.module.system.domain.notify.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.notify.NotifyTemplate;
import java.util.List;
import java.util.Optional;

public interface NotifyTemplateRepository {
    NotifyTemplate save(NotifyTemplate t);
    void delete(Long id);
    NotifyTemplate findById(Long id);
    Optional<NotifyTemplate> findByCode(String code);
    List<NotifyTemplate> findAll();
    PageResult<NotifyTemplate> findPage(String name, String code, Integer status, Integer pageNo, Integer pageSize);
}
