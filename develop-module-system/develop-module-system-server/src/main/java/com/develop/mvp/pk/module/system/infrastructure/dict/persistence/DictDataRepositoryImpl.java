package com.develop.mvp.pk.module.system.infrastructure.dict.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictDataDO;
import com.develop.mvp.pk.module.system.dal.mysql.dict.DictDataMapper;
import com.develop.mvp.pk.module.system.domain.dict.DictData;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictDataRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataValue;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class DictDataRepositoryImpl implements DictDataRepository {

    private final DictDataMapper mapper;

    public DictDataRepositoryImpl(DictDataMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public DictDataDO insert(DictData dictData) {
        DictDataDO dictDataDO = toDO(dictData);
        mapper.insert(dictDataDO);
        return dictDataDO;
    }

    @Override
    public void update(DictData dictData) {
        mapper.updateById(toDO(dictData));
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
    public DictData findById(DictDataId id) {
        if (id == null) {
            return null;
        }
        DictDataDO dictData = mapper.selectById(id.value());
        return dictData != null ? toDomain(dictData) : null;
    }

    @Override
    public Optional<DictData> findByTypeAndValue(DictTypeKey type, String value) {
        return Optional.ofNullable(mapper.selectByDictTypeAndValue(type.value(), value)).map(this::toDomain);
    }

    @Override
    public Optional<DictData> findByTypeAndLabel(DictTypeKey type, String label) {
        return Optional.ofNullable(mapper.selectByDictTypeAndLabel(type.value(), label)).map(this::toDomain);
    }

    @Override
    public List<DictData> findByTypeAndValues(DictTypeKey type, Collection<String> values) {
        return mapper.selectByDictTypeAndValues(type.value(), values).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DictData> findByDictType(DictTypeKey type) {
        return mapper.selectList(DictDataDO::getDictType, type.value()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<DictData> findByStatusAndDictType(Integer status, DictTypeKey type) {
        return mapper.selectListByStatusAndDictType(status, type != null ? type.value() : null).stream().map(this::toDomain).toList();
    }

    @Override
    public PageResult<DictData> findPage(String label, DictTypeKey type, Integer status, Integer pageNo, Integer pageSize) {
        DictDataPageReqVO reqVO = new DictDataPageReqVO();
        reqVO.setLabel(label);
        reqVO.setDictType(type != null ? type.value() : null);
        reqVO.setStatus(status);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        PageResult<DictDataDO> doPage = mapper.selectPage(reqVO);
        return new PageResult<>(doPage.getList().stream().map(this::toDomain).toList(), doPage.getTotal());
    }

    @Override
    public long countByDictType(DictTypeKey type) {
        return countByDictType(type.value());
    }

    @Override
    public long countByDictType(String dictType) {
        return mapper.selectCountByDictType(dictType);
    }

    @Override
    public DictDataDO findDoById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public DictDataDO findDoByTypeAndValue(String dictType, String value) {
        return mapper.selectByDictTypeAndValue(dictType, value);
    }

    @Override
    public DictDataDO findDoByTypeAndLabel(String dictType, String label) {
        return mapper.selectByDictTypeAndLabel(dictType, label);
    }

    @Override
    public List<DictDataDO> findDoByTypeAndValues(String dictType, Collection<String> values) {
        return mapper.selectByDictTypeAndValues(dictType, values);
    }

    @Override
    public List<DictDataDO> findDoByDictType(String dictType) {
        return mapper.selectList(DictDataDO::getDictType, dictType);
    }

    @Override
    public List<DictDataDO> findDoByStatusAndDictType(Integer status, String dictType) {
        return mapper.selectListByStatusAndDictType(status, dictType);
    }

    @Override
    public PageResult<DictDataDO> findDoPage(DictDataPageReqVO pageReqVO) {
        return mapper.selectPage(pageReqVO);
    }

    private DictDataDO toDO(DictData dictData) {
        DictDataDO dictDataDO = new DictDataDO();
        dictDataDO.setId(dictData.id() != null ? dictData.id().value() : null);
        dictDataDO.setSort(dictData.sort());
        dictDataDO.setLabel(dictData.label());
        dictDataDO.setValue(dictData.value().value());
        dictDataDO.setDictType(dictData.dictType().value());
        dictDataDO.setStatus(dictData.status());
        dictDataDO.setColorType(dictData.colorType());
        dictDataDO.setCssClass(dictData.cssClass());
        dictDataDO.setRemark(dictData.remark());
        return dictDataDO;
    }

    private DictData toDomain(DictDataDO dictData) {
        return new DictData(DictDataId.of(dictData.getId()), DictTypeKey.of(dictData.getDictType()), DictDataValue.of(dictData.getValue()),
                dictData.getLabel(), dictData.getSort(), dictData.getStatus(), dictData.getColorType(), dictData.getCssClass(), dictData.getRemark());
    }

}
