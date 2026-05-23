package com.develop.mvp.pk.module.system.infrastructure.notify;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.notify.vo.template.NotifyTemplatePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.notify.NotifyTemplateDO;
import com.develop.mvp.pk.module.system.dal.mysql.notify.NotifyTemplateMapper;
import com.develop.mvp.pk.module.system.domain.notify.NotifyTemplate;
import com.develop.mvp.pk.module.system.domain.notify.repository.NotifyTemplateRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class NotifyTemplateRepositoryImpl implements NotifyTemplateRepository {
    private final NotifyTemplateMapper mapper;
    public NotifyTemplateRepositoryImpl(NotifyTemplateMapper mapper) { this.mapper = mapper; }

    @Override
    public NotifyTemplate save(NotifyTemplate t) {
        NotifyTemplateDO d = new NotifyTemplateDO(); d.setId(t.id()); d.setCode(t.code()); d.setName(t.name());
        d.setNickname(t.nickname()); d.setContent(t.content()); d.setType(t.type()); d.setStatus(t.status()); d.setRemark(t.remark());
        if (mapper.selectById(t.id()) == null) mapper.insert(d); else mapper.updateById(d);
        return t;
    }
    @Override public void delete(Long id) { mapper.deleteById(id); }
    @Override public NotifyTemplate findById(Long id) { NotifyTemplateDO d = mapper.selectById(id); return d != null ? toDomain(d) : null; }
    @Override public Optional<NotifyTemplate> findByCode(String code) { return Optional.ofNullable(mapper.selectOne(NotifyTemplateDO::getCode, code)).map(this::toDomain); }
    @Override public List<NotifyTemplate> findAll() { return mapper.selectList().stream().map(this::toDomain).toList(); }
    @Override
    public PageResult<NotifyTemplate> findPage(String name, String code, Integer status, Integer pageNo, Integer pageSize) {
        var reqVO = new NotifyTemplatePageReqVO(); reqVO.setName(name); reqVO.setCode(code); reqVO.setStatus(status); reqVO.setPageNo(pageNo); reqVO.setPageSize(pageSize);
        var dp = mapper.selectPage(reqVO); return new PageResult<>(dp.getList().stream().map(this::toDomain).toList(), dp.getTotal());
    }
    private NotifyTemplate toDomain(NotifyTemplateDO d) {
        return NotifyTemplate.of(d.getId(), d.getCode(), d.getName()).nickname(d.getNickname()).content(d.getContent()).type(d.getType()).status(d.getStatus()).remark(d.getRemark());
    }
}
