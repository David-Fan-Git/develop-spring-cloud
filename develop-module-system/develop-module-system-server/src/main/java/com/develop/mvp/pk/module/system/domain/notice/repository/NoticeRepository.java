package com.develop.mvp.pk.module.system.domain.notice.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.notice.Notice;
import java.util.List;

public interface NoticeRepository {
    Notice save(Notice n);
    void delete(Long id);
    Notice findById(Long id);
    PageResult<Notice> findPage(String title, Integer status, Integer pageNo, Integer pageSize);
}
