package com.develop.mvp.pk.module.system.infrastructure.dict.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictTypeDO;
import com.develop.mvp.pk.module.system.dal.mysql.dict.DictTypeMapper;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictTypeRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class DictTypeRepositoryImpl implements DictTypeRepository {
    private final DictTypeMapper mapper;
    public DictTypeRepositoryImpl(DictTypeMapper mapper) { this.mapper = mapper; }

    @Override
    public DictType save(DictType t) {
        DictTypeDO d = new DictTypeDO(); d.setId(t.id().value()); d.setName(t.name().value());
        d.setType(t.type().value()); d.setStatus(t.status()); d.setRemark(t.remark());
        if (mapper.selectById(t.id().value()) == null) mapper.insert(d); else mapper.updateById(d);
        return t;
    }

    @Override
    public void delete(DictTypeId id) { mapper.updateToDelete(id.value(), LocalDateTime.now()); }

    @Override
    public DictType findById(DictTypeId id) {
        DictTypeDO d = mapper.selectById(id.value()); return d != null ? toDomain(d) : null;
    }

    @Override
    public Optional<DictType> findByType(DictTypeKey type) { return Optional.ofNullable(mapper.selectByType(type.value())).map(this::toDomain); }

    @Override
    public Optional<DictType> findByName(String name) { return Optional.ofNullable(mapper.selectByName(name)).map(this::toDomain); }

    @Override
    public List<DictType> findAll() { return mapper.selectList().stream().map(this::toDomain).toList(); }

    @Override
    public PageResult<DictType> findPage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        var reqVO = new DictTypePageReqVO(); reqVO.setName(name); reqVO.setType(type); reqVO.setStatus(status); reqVO.setCreateTime(createTime); reqVO.setPageNo(pageNo); reqVO.setPageSize(pageSize);
        var doPage = mapper.selectPage(reqVO);
        return new PageResult<>(doPage.getList().stream().map(this::toDomain).toList(), doPage.getTotal());
    }

    private DictType toDomain(DictTypeDO d) {
        return new DictType(DictTypeId.of(d.getId()), DictTypeName.of(d.getName()), DictTypeKey.of(d.getType()), d.getStatus(), d.getRemark());
    }
}
