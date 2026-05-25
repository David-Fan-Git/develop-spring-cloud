package com.develop.mvp.pk.module.system.application.dict.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.develop.mvp.pk.framework.common.enums.CommonStatusEnum;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.collection.CollectionUtils;
import com.develop.mvp.pk.module.system.application.dict.port.inbound.DictUseCase;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictDataDO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictTypeDO;
import com.develop.mvp.pk.module.system.domain.dict.DictData;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictDataRepository;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictTypeRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictDataValue;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeName;
import com.google.common.annotations.VisibleForTesting;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.develop.mvp.pk.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.develop.mvp.pk.module.system.enums.ErrorCodeConstants.*;

@Service
@RequiredArgsConstructor
public class DictApplicationService implements DictUseCase {

    private static final Comparator<DictDataDO> COMPARATOR_TYPE_AND_SORT = Comparator
            .comparing(DictDataDO::getDictType)
            .thenComparingInt(DictDataDO::getSort);

    private final DictTypeRepository dictTypeRepository;
    private final DictDataRepository dictDataRepository;

    @Transactional
    public Long createDictType(DictTypeSaveReqVO createReqVO) {
        validateDictTypeNameUnique(null, createReqVO.getName());
        validateDictTypeUnique(null, createReqVO.getType());
        DictTypeDO dictType = dictTypeRepository.insert(toDomain(createReqVO));
        return dictType.getId();
    }

    @Transactional
    public Long createDictType(String name, String type, Integer status, String remark) {
        DictTypeSaveReqVO reqVO = new DictTypeSaveReqVO();
        reqVO.setName(name);
        reqVO.setType(type);
        reqVO.setStatus(status);
        reqVO.setRemark(remark);
        return createDictType(reqVO);
    }

    @Transactional
    public void updateDictType(DictTypeSaveReqVO updateReqVO) {
        validateDictTypeExists(updateReqVO.getId());
        validateDictTypeNameUnique(updateReqVO.getId(), updateReqVO.getName());
        validateDictTypeUnique(updateReqVO.getId(), updateReqVO.getType());
        dictTypeRepository.update(toDomain(updateReqVO));
    }

    @Transactional
    public void updateDictType(Long id, String name, String type, Integer status, String remark) {
        DictTypeSaveReqVO reqVO = new DictTypeSaveReqVO();
        reqVO.setId(id);
        reqVO.setName(name);
        reqVO.setType(type);
        reqVO.setStatus(status);
        reqVO.setRemark(remark);
        updateDictType(reqVO);
    }

    @Transactional
    public void deleteDictType(Long id) {
        DictTypeDO dictType = validateDictTypeExists(id);
        if (dictDataRepository.countByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        dictTypeRepository.delete(id, LocalDateTime.now());
    }

    @Transactional
    public void deleteDictTypeList(List<Long> ids) {
        List<DictTypeDO> dictTypes = dictTypeRepository.findDoByIds(ids);
        dictTypes.forEach(dictType -> {
            if (dictDataRepository.countByDictType(dictType.getType()) > 0) {
                throw exception(DICT_TYPE_HAS_CHILDREN);
            }
        });
        LocalDateTime now = LocalDateTime.now();
        ids.forEach(id -> dictTypeRepository.delete(id, now));
    }

    public PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO) {
        return dictTypeRepository.findDoPage(pageReqVO);
    }

    public PageResult<DictTypeDO> getDictTypePage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        DictTypePageReqVO reqVO = new DictTypePageReqVO();
        reqVO.setName(name);
        reqVO.setType(type);
        reqVO.setStatus(status);
        reqVO.setCreateTime(createTime);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        return getDictTypePage(reqVO);
    }

    public DictTypeDO getDictType(Long id) {
        return dictTypeRepository.findDoById(id);
    }

    public DictTypeDO getDictType(String type) {
        return dictTypeRepository.findDoByType(type);
    }

    public DictTypeDO getDictTypeByType(String type) {
        return getDictType(type);
    }

    public List<DictTypeDO> getDictTypeList() {
        return dictTypeRepository.findAllDo();
    }

    @Transactional
    public Long createDictData(DictDataSaveReqVO createReqVO) {
        validateDictTypeExists(createReqVO.getDictType());
        validateDictDataValueUnique(null, createReqVO.getDictType(), createReqVO.getValue());
        DictDataDO dictData = dictDataRepository.insert(toDomain(createReqVO));
        return dictData.getId();
    }

    @Transactional
    public Long createDictData(Integer sort, String label, String value, String dictType, Integer status, String colorType, String cssClass, String remark) {
        DictDataSaveReqVO reqVO = new DictDataSaveReqVO();
        reqVO.setSort(sort);
        reqVO.setLabel(label);
        reqVO.setValue(value);
        reqVO.setDictType(dictType);
        reqVO.setStatus(status);
        reqVO.setColorType(colorType);
        reqVO.setCssClass(cssClass);
        reqVO.setRemark(remark);
        return createDictData(reqVO);
    }

    @Transactional
    public void updateDictData(DictDataSaveReqVO updateReqVO) {
        validateDictDataExists(updateReqVO.getId());
        validateDictTypeExists(updateReqVO.getDictType());
        validateDictDataValueUnique(updateReqVO.getId(), updateReqVO.getDictType(), updateReqVO.getValue());
        dictDataRepository.update(toDomain(updateReqVO));
    }

    @Transactional
    public void updateDictData(Long id, Integer sort, String label, String value, String dictType, Integer status, String colorType, String cssClass, String remark) {
        DictDataSaveReqVO reqVO = new DictDataSaveReqVO();
        reqVO.setId(id);
        reqVO.setSort(sort);
        reqVO.setLabel(label);
        reqVO.setValue(value);
        reqVO.setDictType(dictType);
        reqVO.setStatus(status);
        reqVO.setColorType(colorType);
        reqVO.setCssClass(cssClass);
        reqVO.setRemark(remark);
        updateDictData(reqVO);
    }

    @Transactional
    public void deleteDictData(Long id) {
        validateDictDataExists(id);
        dictDataRepository.delete(id);
    }

    @Transactional
    public void deleteDictDataList(List<Long> ids) {
        dictDataRepository.deleteByIds(ids);
    }

    public List<DictDataDO> getDictDataList(Integer status, String dictType) {
        List<DictDataDO> list = dictDataRepository.findDoByStatusAndDictType(status, dictType);
        list.sort(COMPARATOR_TYPE_AND_SORT);
        return list;
    }

    public PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO) {
        return dictDataRepository.findDoPage(pageReqVO);
    }

    public PageResult<DictDataDO> getDictDataPage(String label, String dictType, Integer status, Integer pageNo, Integer pageSize) {
        DictDataPageReqVO reqVO = new DictDataPageReqVO();
        reqVO.setLabel(label);
        reqVO.setDictType(dictType);
        reqVO.setStatus(status);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        return getDictDataPage(reqVO);
    }

    public DictDataDO getDictData(Long id) {
        return dictDataRepository.findDoById(id);
    }

    public DictDataDO getDictData(String dictType, String value) {
        return dictDataRepository.findDoByTypeAndValue(dictType, value);
    }

    public DictDataDO parseDictData(String dictType, String label) {
        return dictDataRepository.findDoByTypeAndLabel(dictType, label);
    }

    public List<DictDataDO> getDictDataListByDictType(String dictType) {
        List<DictDataDO> list = dictDataRepository.findDoByDictType(dictType);
        list.sort(Comparator.comparing(DictDataDO::getSort));
        return list;
    }

    public long getDictDataCountByDictType(String dictType) {
        return dictDataRepository.countByDictType(dictType);
    }

    public void validateDictDataList(String dictType, Collection<String> values) {
        if (CollUtil.isEmpty(values)) {
            return;
        }
        Map<String, DictDataDO> dictDataMap = CollectionUtils.convertMap(
                dictDataRepository.findDoByTypeAndValues(dictType, values), DictDataDO::getValue);
        values.forEach(value -> {
            DictDataDO dictData = dictDataMap.get(value);
            if (dictData == null) {
                throw exception(DICT_DATA_NOT_EXISTS);
            }
            if (!CommonStatusEnum.ENABLE.getStatus().equals(dictData.getStatus())) {
                throw exception(DICT_DATA_NOT_ENABLE, dictData.getLabel());
            }
        });
    }

    @VisibleForTesting
    public DictTypeDO validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = dictTypeRepository.findDoById(id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

    @VisibleForTesting
    public void validateDictTypeExists(String type) {
        DictTypeDO dictType = dictTypeRepository.findDoByType(type);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        if (!CommonStatusEnum.ENABLE.getStatus().equals(dictType.getStatus())) {
            throw exception(DICT_TYPE_NOT_ENABLE);
        }
    }

    @VisibleForTesting
    public void validateDictTypeNameUnique(Long id, String name) {
        DictTypeDO dictType = dictTypeRepository.findDoByName(name);
        if (dictType == null) {
            return;
        }
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictTypeDO dictType = dictTypeRepository.findDoByType(type);
        if (dictType == null) {
            return;
        }
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictDataValueUnique(Long id, String dictType, String value) {
        DictDataDO dictData = dictDataRepository.findDoByTypeAndValue(dictType, value);
        if (dictData == null) {
            return;
        }
        if (id == null) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
        if (!dictData.getId().equals(id)) {
            throw exception(DICT_DATA_VALUE_DUPLICATE);
        }
    }

    @VisibleForTesting
    public void validateDictDataExists(Long id) {
        if (id == null) {
            return;
        }
        DictDataDO dictData = dictDataRepository.findDoById(id);
        if (dictData == null) {
            throw exception(DICT_DATA_NOT_EXISTS);
        }
    }

    private DictType toDomain(DictTypeSaveReqVO reqVO) {
        return new DictType(DictTypeId.of(reqVO.getId()), DictTypeName.of(reqVO.getName()), DictTypeKey.of(reqVO.getType()),
                reqVO.getStatus(), reqVO.getRemark());
    }

    private DictData toDomain(DictDataSaveReqVO reqVO) {
        return new DictData(DictDataId.of(reqVO.getId()), DictTypeKey.of(reqVO.getDictType()), DictDataValue.of(reqVO.getValue()),
                reqVO.getLabel(), reqVO.getSort(), reqVO.getStatus(), reqVO.getColorType(), reqVO.getCssClass(), reqVO.getRemark());
    }

}
