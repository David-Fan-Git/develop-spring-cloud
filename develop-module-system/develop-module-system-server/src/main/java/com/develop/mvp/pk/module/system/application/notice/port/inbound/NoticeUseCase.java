package com.develop.mvp.pk.module.system.application.notice.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notice.NoticeDO;

import java.util.List;

/**
 * notice aggregate use-case boundary for legacy entries and new adapters.
 */
public interface NoticeUseCase {

    Long createNotice(NoticeSaveReqVO createReqVO);

    void updateNotice(NoticeSaveReqVO updateReqVO);

    void deleteNotice(Long id);

    void deleteNoticeList(List<Long> ids);

    PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO);

    NoticeDO getNotice(Long id);

    void validateNoticeExists(Long id);
}
