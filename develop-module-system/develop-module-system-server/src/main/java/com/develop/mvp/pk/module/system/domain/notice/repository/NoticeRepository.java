package com.develop.mvp.pk.module.system.domain.notice.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notice.NoticeDO;
import com.develop.mvp.pk.module.system.domain.notice.Notice;

import java.util.List;

public interface NoticeRepository {

    NoticeDO insert(Notice notice);

    void update(Notice notice);

    void delete(Long id);

    void deleteByIds(List<Long> ids);

    NoticeDO findDoById(Long id);

    PageResult<NoticeDO> findPage(NoticePageReqVO reqVO);

}
