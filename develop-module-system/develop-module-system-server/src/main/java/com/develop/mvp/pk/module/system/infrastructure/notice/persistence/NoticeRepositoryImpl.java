package com.develop.mvp.pk.module.system.infrastructure.notice.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notice.vo.NoticePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notice.NoticeDO;
import com.develop.mvp.pk.module.system.dal.mysql.notice.NoticeMapper;
import com.develop.mvp.pk.module.system.domain.notice.Notice;
import com.develop.mvp.pk.module.system.domain.notice.repository.NoticeRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class NoticeRepositoryImpl implements NoticeRepository {

    private final NoticeMapper mapper;

    public NoticeRepositoryImpl(NoticeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public NoticeDO insert(Notice notice) {
        NoticeDO noticeDO = toDataObject(notice);
        mapper.insert(noticeDO);
        return noticeDO;
    }

    @Override
    public void update(Notice notice) {
        mapper.updateById(toDataObject(notice));
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        mapper.deleteByIds(ids);
    }

    @Override
    public NoticeDO findDoById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<NoticeDO> findPage(NoticePageReqVO reqVO) {
        return mapper.selectPage(reqVO);
    }

    private NoticeDO toDataObject(Notice notice) {
        NoticeDO noticeDO = new NoticeDO();
        noticeDO.setId(notice.id());
        noticeDO.setTitle(notice.title());
        noticeDO.setType(notice.type());
        noticeDO.setContent(notice.content());
        noticeDO.setStatus(notice.status());
        return noticeDO;
    }
}
