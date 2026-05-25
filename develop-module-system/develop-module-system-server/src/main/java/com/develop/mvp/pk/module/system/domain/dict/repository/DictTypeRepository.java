package com.develop.mvp.pk.module.system.domain.dict.repository;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictTypeDO;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DictTypeRepository {

    DictTypeDO insert(DictType dictType);

    void update(DictType dictType);

    void delete(Long id, LocalDateTime deletedTime);

    DictType findById(DictTypeId id);

    Optional<DictType> findByType(DictTypeKey type);

    Optional<DictType> findByName(String name);

    List<DictType> findAll();

    PageResult<DictType> findPage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize);

    DictTypeDO findDoById(Long id);

    DictTypeDO findDoByType(String type);

    DictTypeDO findDoByName(String name);

    List<DictTypeDO> findDoByIds(List<Long> ids);

    List<DictTypeDO> findAllDo();

    PageResult<DictTypeDO> findDoPage(DictTypePageReqVO pageReqVO);

}
