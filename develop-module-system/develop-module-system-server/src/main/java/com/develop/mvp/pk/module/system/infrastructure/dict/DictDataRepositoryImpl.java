package com.develop.mvp.pk.module.system.infrastructure.dict;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictDataDO;
import com.develop.mvp.pk.module.system.dal.mysql.dict.DictDataMapper;
import com.develop.mvp.pk.module.system.domain.dict.DictData;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictDataRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DictDataRepositoryImpl implements DictDataRepository {
    private final DictDataMapper mapper;
    public DictDataRepositoryImpl(DictDataMapper mapper) { this.mapper = mapper; }

    @Override
    public DictData save(DictData d) {
        DictDataDO dd = new DictDataDO(); dd.setId(d.id().value()); dd.setSort(d.sort()); dd.setLabel(d.label());
        dd.setValue(d.value().value()); dd.setDictType(d.dictType().value()); dd.setStatus(d.status());
        dd.setColorType(d.colorType()); dd.setCssClass(d.cssClass()); dd.setRemark(d.remark());
        if (mapper.selectById(d.id().value()) == null) mapper.insert(dd); else mapper.updateById(dd);
        return d;
    }

    @Override
    public void delete(DictDataId id) { mapper.deleteById(id.value()); }

    @Override
    public DictData findById(DictDataId id) { DictDataDO d = mapper.selectById(id.value()); return d != null ? toDomain(d) : null; }

    @Override
    public Optional<DictData> findByTypeAndValue(DictTypeKey type, String value) { return Optional.ofNullable(mapper.selectByDictTypeAndValue(type.value(), value)).map(this::toDomain); }

    @Override
    public Optional<DictData> findByTypeAndLabel(DictTypeKey type, String label) { return Optional.ofNullable(mapper.selectByDictTypeAndLabel(type.value(), label)).map(this::toDomain); }

    @Override
    public List<DictData> findByTypeAndValues(DictTypeKey type, Collection<String> values) { return mapper.selectByDictTypeAndValues(type.value(), values).stream().map(this::toDomain).toList(); }

    @Override
    public List<DictData> findByDictType(DictTypeKey type) { return mapper.selectList(DictDataDO::getDictType, type.value()).stream().map(this::toDomain).toList(); }

    @Override
    public List<DictData> findByStatusAndDictType(Integer status, DictTypeKey type) { return mapper.selectListByStatusAndDictType(status, type.value()).stream().map(this::toDomain).toList(); }

    @Override
    public PageResult<DictData> findPage(String label, DictTypeKey type, Integer status, Integer pageNo, Integer pageSize) {
        var reqVO = new DictDataPageReqVO(); reqVO.setLabel(label); reqVO.setDictType(type != null ? type.value() : null); reqVO.setStatus(status); reqVO.setPageNo(pageNo); reqVO.setPageSize(pageSize);
        var doPage = mapper.selectPage(reqVO);
        return new PageResult<>(doPage.getList().stream().map(this::toDomain).toList(), doPage.getTotal());
    }

    @Override
    public long countByDictType(DictTypeKey type) { return mapper.selectCountByDictType(type.value()); }

    private DictData toDomain(DictDataDO d) {
        return new DictData(DictDataId.of(d.getId()), DictTypeKey.of(d.getDictType()), DictDataValue.of(d.getValue()), d.getLabel(), d.getSort(), d.getStatus(), d.getColorType(), d.getCssClass(), d.getRemark());
    }
}
