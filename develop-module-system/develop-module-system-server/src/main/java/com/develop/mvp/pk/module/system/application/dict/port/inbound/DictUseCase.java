package com.develop.mvp.pk.module.system.application.dict.port.inbound;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataPageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictDataDO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictTypeDO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * dict aggregate use-case boundary for legacy entries and new adapters.
 */
public interface DictUseCase {

    Long createDictType(DictTypeSaveReqVO createReqVO);

    Long createDictType(String name, String type, Integer status, String remark);

    void updateDictType(DictTypeSaveReqVO updateReqVO);

    void updateDictType(Long id, String name, String type, Integer status, String remark);

    void deleteDictType(Long id);

    void deleteDictTypeList(List<Long> ids);

    PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO);

    PageResult<DictTypeDO> getDictTypePage(String name, String type, Integer status,
                                           LocalDateTime[] createTime, Integer pageNo, Integer pageSize);

    DictTypeDO getDictType(Long id);

    DictTypeDO getDictType(String type);

    DictTypeDO getDictTypeByType(String type);

    List<DictTypeDO> getDictTypeList();

    Long createDictData(DictDataSaveReqVO createReqVO);

    Long createDictData(Integer sort, String label, String value, String dictType,
                        Integer status, String colorType, String cssClass, String remark);

    void updateDictData(DictDataSaveReqVO updateReqVO);

    void updateDictData(Long id, Integer sort, String label, String value, String dictType,
                        Integer status, String colorType, String cssClass, String remark);

    void deleteDictData(Long id);

    void deleteDictDataList(List<Long> ids);

    List<DictDataDO> getDictDataList(Integer status, String dictType);

    PageResult<DictDataDO> getDictDataPage(DictDataPageReqVO pageReqVO);

    PageResult<DictDataDO> getDictDataPage(String label, String dictType, Integer status,
                                           Integer pageNo, Integer pageSize);

    DictDataDO getDictData(Long id);

    DictDataDO getDictData(String dictType, String value);

    DictDataDO parseDictData(String dictType, String label);

    List<DictDataDO> getDictDataListByDictType(String dictType);

    long getDictDataCountByDictType(String dictType);

    void validateDictDataList(String dictType, Collection<String> values);
}
