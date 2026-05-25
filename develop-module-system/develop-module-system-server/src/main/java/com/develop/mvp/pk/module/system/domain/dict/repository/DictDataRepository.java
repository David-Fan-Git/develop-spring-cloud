package com.develop.mvp.pk.module.system.domain.dict.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictDataDO;
import com.develop.mvp.pk.module.system.domain.dict.DictData;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DictDataRepository {

    DictDataDO insert(DictData dictData);

    void update(DictData dictData);

    void delete(Long id);

    void deleteByIds(List<Long> ids);

    DictData findById(DictDataId id);

    Optional<DictData> findByTypeAndValue(DictTypeKey dictType, String value);

    Optional<DictData> findByTypeAndLabel(DictTypeKey dictType, String label);

    List<DictData> findByTypeAndValues(DictTypeKey dictType, Collection<String> values);

    List<DictData> findByDictType(DictTypeKey dictType);

    List<DictData> findByStatusAndDictType(Integer status, DictTypeKey dictType);

    PageResult<DictData> findPage(String label, DictTypeKey dictType, Integer status, Integer pageNo, Integer pageSize);

    long countByDictType(DictTypeKey dictType);

    long countByDictType(String dictType);

    DictDataDO findDoById(Long id);

    DictDataDO findDoByTypeAndValue(String dictType, String value);

    DictDataDO findDoByTypeAndLabel(String dictType, String label);

    List<DictDataDO> findDoByTypeAndValues(String dictType, Collection<String> values);

    List<DictDataDO> findDoByDictType(String dictType);

    List<DictDataDO> findDoByStatusAndDictType(Integer status, String dictType);

    PageResult<DictDataDO> findDoPage(DictDataPageReqVO pageReqVO);

}
