package com.develop.mvp.pk.module.system.application.notice.service;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.application.notice.port.inbound.NoticeUseCase;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticeSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notice.NoticeDO;
import com.develop.mvp.pk.module.system.domain.notice.Notice;
import com.develop.mvp.pk.module.system.domain.notice.repository.NoticeRepository;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.NOTICE_NOT_FOUND;

@RequiredArgsConstructor
public class NoticeApplicationService implements NoticeUseCase {

    private final NoticeRepository noticeRepository;

    @Transactional
    public Long createNotice(NoticeSaveReqVO createReqVO) {
        NoticeDO notice = noticeRepository.insert(toDomain(createReqVO));
        return notice.getId();
    }

    @Transactional
    public void updateNotice(NoticeSaveReqVO updateReqVO) {
        validateNoticeExists(updateReqVO.getId());
        noticeRepository.update(toDomain(updateReqVO));
    }

    @Transactional
    public void deleteNotice(Long id) {
        validateNoticeExists(id);
        noticeRepository.delete(id);
    }

    @Transactional
    public void deleteNoticeList(List<Long> ids) {
        noticeRepository.deleteByIds(ids);
    }

    public PageResult<NoticeDO> getNoticePage(NoticePageReqVO reqVO) {
        return noticeRepository.findPage(reqVO);
    }

    public NoticeDO getNotice(Long id) {
        return noticeRepository.findDoById(id);
    }

    @VisibleForTesting
    public void validateNoticeExists(Long id) {
        if (id == null) {
            return;
        }
        NoticeDO notice = noticeRepository.findDoById(id);
        if (notice == null) {
            throw exception(NOTICE_NOT_FOUND);
        }
    }

    private Notice toDomain(NoticeSaveReqVO reqVO) {
        return Notice.of(reqVO.getId(), reqVO.getTitle(), reqVO.getType(), reqVO.getContent(), reqVO.getStatus());
    }
}
