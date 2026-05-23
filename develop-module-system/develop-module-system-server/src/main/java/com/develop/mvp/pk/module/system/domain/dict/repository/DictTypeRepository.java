package com.develop.mvp.pk.module.system.domain.dict.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DictTypeRepository {
    DictType save(DictType dictType);
    void delete(DictTypeId id);
    DictType findById(DictTypeId id);
    Optional<DictType> findByType(DictTypeKey type);
    Optional<DictType> findByName(String name);
    List<DictType> findAll();
    PageResult<DictType> findPage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize);
}
