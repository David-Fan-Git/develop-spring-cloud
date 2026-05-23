package com.develop.mvp.pk.module.system.application.dict;

import cn.hutool.core.collection.CollUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.module.system.domain.dict.DictData;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictDataRepository;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictTypeRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class DictApplicationService {
    private final DictTypeRepository dictTypeRepository;
    private final DictDataRepository dictDataRepository;

    // ========== DictType CRUD ==========

    @Transactional
    public Long createDictType(String name, String type, Integer status, String remark) {
        assertDictTypeNameUnique(null, name);
        assertDictTypeKeyUnique(null, type);
        DictType dt = new DictType(null, DictTypeName.of(name), DictTypeKey.of(type), status, remark);
        dictTypeRepository.save(dt);
        return dt.id().value();
    }

    @Transactional
    public void updateDictType(Long id, String name, String type, Integer status, String remark) {
        assertDictTypeExists(id);
        assertDictTypeNameUnique(id, name);
        assertDictTypeKeyUnique(id, type);
        dictTypeRepository.save(new DictType(DictTypeId.of(id), DictTypeName.of(name), DictTypeKey.of(type), status, remark));
    }

    @Transactional
    public void deleteDictType(Long id) {
        DictType dt = assertDictTypeExists(id);
        if (dictDataRepository.countByDictType(dt.type()) > 0) throw exception(DICT_TYPE_HAS_CHILDREN);
        dictTypeRepository.delete(DictTypeId.of(id));
    }

    @Transactional
    public void deleteDictTypeList(List<Long> ids) {
        for (Long id : ids) {
            DictType dt = assertDictTypeExists(id);
            if (dictDataRepository.countByDictType(dt.type()) > 0) throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        ids.forEach(id -> dictTypeRepository.delete(DictTypeId.of(id)));
    }

    public DictType getDictType(Long id) { return dictTypeRepository.findById(DictTypeId.of(id)); }
    public DictType getDictTypeByType(String type) { return dictTypeRepository.findByType(DictTypeKey.of(type)).orElse(null); }
    public List<DictType> getDictTypeList() { return dictTypeRepository.findAll(); }
    public PageResult<DictType> getDictTypePage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        return dictTypeRepository.findPage(name, type, status, createTime, pageNo, pageSize);
    }

    // ========== DictData CRUD ==========

    @Transactional
    public Long createDictData(Integer sort, String label, String value, String dictType, Integer status, String colorType, String cssClass, String remark) {
        assertDictTypeExists(dictType);
        assertDictDataValueUnique(null, dictType, value);
        DictData dd = new DictData(null, DictTypeKey.of(dictType), DictDataValue.of(value), label, sort, status, colorType, cssClass, remark);
        dictDataRepository.save(dd);
        return dd.id().value();
    }

    @Transactional
    public void updateDictData(Long id, Integer sort, String label, String value, String dictType, Integer status, String colorType, String cssClass, String remark) {
        DictData existing = assertDictDataExists(id);
        assertDictTypeExists(dictType);
        assertDictDataValueUnique(id, dictType, value);
        dictDataRepository.save(new DictData(DictDataId.of(id), DictTypeKey.of(dictType), DictDataValue.of(value), label, sort, status, colorType, cssClass, remark));
    }

    @Transactional
    public void deleteDictData(Long id) { assertDictDataExists(id); dictDataRepository.delete(DictDataId.of(id)); }

    @Transactional
    public void deleteDictDataList(List<Long> ids) { ids.forEach(id -> dictDataRepository.delete(DictDataId.of(id))); }

    public DictData getDictData(Long id) { return dictDataRepository.findById(DictDataId.of(id)); }
    public DictData getDictData(String dictType, String value) { return dictDataRepository.findByTypeAndValue(DictTypeKey.of(dictType), value).orElse(null); }
    public DictData parseDictData(String dictType, String label) { return dictDataRepository.findByTypeAndLabel(DictTypeKey.of(dictType), label).orElse(null); }
    public List<DictData> getDictDataListByDictType(String dictType) { return dictDataRepository.findByDictType(DictTypeKey.of(dictType)); }
    public List<DictData> getDictDataList(Integer status, String dictType) { return dictDataRepository.findByStatusAndDictType(status, DictTypeKey.of(dictType)); }
    public PageResult<DictData> getDictDataPage(String label, String dictType, Integer status, Integer pageNo, Integer pageSize) {
        return dictDataRepository.findPage(label, dictType != null ? DictTypeKey.of(dictType) : null, status, pageNo, pageSize);
    }
    public long getDictDataCountByDictType(String dictType) { return dictDataRepository.countByDictType(DictTypeKey.of(dictType)); }

    public void validateDictDataList(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) return;
        List<DictData> data = dictDataRepository.findByTypeAndValues(DictTypeKey.of(dictType), values);
        Map<String, DictData> map = CollectionUtils.convertMap(data, d -> d.value().value());
        for (String v : values) {
            DictData d = map.get(v);
            if (d == null) throw exception(DICT_DATA_NOT_EXISTS);
            if (!CommonStatusEnum.ENABLE.getStatus().equals(d.status())) throw exception(DICT_DATA_NOT_ENABLE, d.label());
        }
    }

    // ========== helpers ==========

    private DictType assertDictTypeExists(Long id) {
        DictType dt = dictTypeRepository.findById(DictTypeId.of(id));
        if (dt == null) throw exception(DICT_TYPE_NOT_EXISTS);
        return dt;
    }

    private void assertDictTypeExists(String type) {
        DictType dt = dictTypeRepository.findByType(DictTypeKey.of(type)).orElse(null);
        if (dt == null) throw exception(DICT_TYPE_NOT_EXISTS);
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dt.status())) throw exception(DICT_TYPE_NOT_ENABLE);
    }

    private DictData assertDictDataExists(Long id) {
        DictData d = dictDataRepository.findById(DictDataId.of(id));
        if (d == null) throw exception(DICT_DATA_NOT_EXISTS);
        return d;
    }

    private void assertDictTypeNameUnique(Long excludeId, String name) {
        if (name == null || name.isBlank()) return;
        dictTypeRepository.findByName(name).ifPresent(dt -> {
            if (excludeId == null || !dt.id().value().equals(excludeId)) throw exception(DICT_TYPE_NAME_DUPLICATE);
        });
    }

    private void assertDictTypeKeyUnique(Long excludeId, String type) {
        if (type == null || type.isBlank()) return;
        dictTypeRepository.findByType(DictTypeKey.of(type)).ifPresent(dt -> {
            if (excludeId == null || !dt.id().value().equals(excludeId)) throw exception(DICT_TYPE_TYPE_DUPLICATE);
        });
    }

    private void assertDictDataValueUnique(Long excludeId, String dictType, String value) {
        dictDataRepository.findByTypeAndValue(DictTypeKey.of(dictType), value).ifPresent(d -> {
            if (excludeId == null || !d.id().value().equals(excludeId)) throw exception(DICT_DATA_VALUE_DUPLICATE);
        });
    }
}
