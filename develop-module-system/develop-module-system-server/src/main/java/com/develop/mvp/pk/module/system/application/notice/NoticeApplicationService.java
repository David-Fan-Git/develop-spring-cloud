package com.develop.mvp.pk.module.system.application.notice;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.notice.Notice;
import com.develop.mvp.pk.module.system.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class NoticeApplicationService {
    private final NoticeRepository noticeRepo;

    @Transactional public Long createNotice(String title, String content, String type, Integer status) {
        var n = Notice.of(null, title).content(content).type(type).status(status); noticeRepo.save(n); return n.id();
    }
    @Transactional public void updateNotice(Long id, String title, String content, String type, Integer status) {
        noticeRepo.save(Notice.of(id, title).content(content).type(type).status(status));
    }
    @Transactional public void deleteNotice(Long id) { noticeRepo.delete(id); }
    public Notice getNotice(Long id) { return noticeRepo.findById(id); }
    public PageResult<Notice> getNoticePage(String title, Integer status, Integer pageNo, Integer pageSize) { return noticeRepo.findPage(title, status, pageNo, pageSize); }
}
