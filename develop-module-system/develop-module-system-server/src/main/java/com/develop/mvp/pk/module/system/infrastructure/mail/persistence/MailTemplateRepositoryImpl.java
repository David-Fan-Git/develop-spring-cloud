package com.develop.mvp.pk.module.system.infrastructure.mail.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.mail.vo.template.MailTemplatePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.mail.MailTemplateDO;
import com.develop.mvp.pk.module.system.dal.mysql.mail.MailTemplateMapper;
import com.develop.mvp.pk.module.system.domain.mail.MailTemplate;
import com.develop.mvp.pk.module.system.domain.mail.repository.MailTemplateRepository;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public class MailTemplateRepositoryImpl implements MailTemplateRepository {
    private final MailTemplateMapper mapper;
    public MailTemplateRepositoryImpl(MailTemplateMapper mapper) { this.mapper = mapper; }

    @Override
    public MailTemplate save(MailTemplate t) {
        MailTemplateDO d = new MailTemplateDO(); d.setId(t.id()); d.setCode(t.code()); d.setName(t.name());
        d.setAccountId(t.accountId()); d.setNickname(t.nickname()); d.setTitle(t.title()); d.setContent(t.content());
        d.setStatus(t.status()); d.setRemark(t.remark());
        if (mapper.selectById(t.id()) == null) mapper.insert(d); else mapper.updateById(d);
        return t;
    }

    @Override public void delete(Long id) { mapper.deleteById(id); }
    @Override public MailTemplate findById(Long id) { MailTemplateDO d = mapper.selectById(id); return d != null ? toDomain(d) : null; }
    @Override public Optional<MailTemplate> findByCode(String code) { return Optional.ofNullable(mapper.selectByCode(code)).map(this::toDomain); }
    @Override public List<MailTemplate> findAll() { return mapper.selectList().stream().map(this::toDomain).toList(); }

    @Override
    public PageResult<MailTemplate> findPage(String name, String code, Integer status, Integer pageNo, Integer pageSize) {
        var reqVO = new MailTemplatePageReqVO(); reqVO.setName(name); reqVO.setCode(code); reqVO.setStatus(status); reqVO.setPageNo(pageNo); reqVO.setPageSize(pageSize);
        var dp = mapper.selectPage(reqVO); return new PageResult<>(dp.getList().stream().map(this::toDomain).toList(), dp.getTotal());
    }

    @Override public long countByAccountId(Long accountId) { return mapper.selectCountByAccountId(accountId); }

    private MailTemplate toDomain(MailTemplateDO d) {
        return MailTemplate.of(d.getId(), d.getCode(), d.getName()).accountId(d.getAccountId())
                .nickname(d.getNickname()).title(d.getTitle()).content(d.getContent()).status(d.getStatus()).remark(d.getRemark());
    }
}
