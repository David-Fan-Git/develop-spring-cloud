package com.develop.mvp.pk.module.system.infrastructure.notice;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notice.NoticeDO;
import com.develop.mvp.pk.module.system.dal.mysql.notice.NoticeMapper;
import com.develop.mvp.pk.module.system.domain.notice.Notice;
import com.develop.mvp.pk.module.system.domain.notice.repository.NoticeRepository;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {
    private final NoticeMapper mapper;
    public NoticeRepositoryImpl(NoticeMapper mapper) { this.mapper = mapper; }

    @Override
    public Notice save(Notice n) {
        NoticeDO d = new NoticeDO();
        d.setId(n.id());
        d.setTitle(n.title());
        d.setContent(n.content());
        d.setType(Integer.valueOf(n.type()));
        d.setStatus(n.status());
        if (mapper.selectById(n.id()) == null) mapper.insert(d);
        else mapper.updateById(d);
        return n;
    }

    @Override
    public void delete(Long id) { mapper.deleteById(id); }

    @Override
    public Notice findById(Long id) {
        NoticeDO d = mapper.selectById(id);
        return d != null ? toDomain(d) : null;
    }

    @Override
    public PageResult<Notice> findPage(String title, Integer status, Integer pageNo, Integer pageSize) {
        var reqVO = new NoticePageReqVO();
        reqVO.setTitle(title);
        reqVO.setStatus(status);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        var dp = mapper.selectPage(reqVO);
        return new PageResult<>(dp.getList().stream().map(this::toDomain).toList(), dp.getTotal());
    }

    private Notice toDomain(NoticeDO d) {
        return Notice.of(d.getId(), d.getTitle())
                .content(d.getContent())
                .type(d.getType() != null ? String.valueOf(d.getType()) : null)
                .status(d.getStatus());
    }
}
