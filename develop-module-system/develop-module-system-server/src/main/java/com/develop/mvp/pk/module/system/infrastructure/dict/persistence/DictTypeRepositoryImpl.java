package com.develop.mvp.pk.module.system.infrastructure.dict.persistence;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.framework.common.util.date.LocalDateTimeUtils;
import com.develop.mvp.pk.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import com.develop.mvp.pk.module.system.dal.dataobject.dict.DictTypeDO;
import com.develop.mvp.pk.module.system.dal.mysql.dict.DictTypeMapper;
import com.develop.mvp.pk.module.system.domain.dict.DictType;
import com.develop.mvp.pk.module.system.domain.dict.repository.DictTypeRepository;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeId;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeKey;
import com.develop.mvp.pk.module.system.domain.dict.valueobject.DictTypeName;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class DictTypeRepositoryImpl implements DictTypeRepository {

    private final DictTypeMapper mapper;

    public DictTypeRepositoryImpl(DictTypeMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public DictTypeDO insert(DictType dictType) {
        DictTypeDO dictTypeDO = toDO(dictType);
        dictTypeDO.setDeletedTime(LocalDateTimeUtils.EMPTY);
        mapper.insert(dictTypeDO);
        return dictTypeDO;
    }

    @Override
    public void update(DictType dictType) {
        mapper.updateById(toDO(dictType));
    }

    @Override
    public void delete(Long id, LocalDateTime deletedTime) {
        mapper.updateToDelete(id, deletedTime);
    }

    @Override
    public DictType findById(DictTypeId id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = mapper.selectById(id.value());
        return dictType != null ? toDomain(dictType) : null;
    }

    @Override
    public Optional<DictType> findByType(DictTypeKey type) {
        return Optional.ofNullable(mapper.selectByType(type.value())).map(this::toDomain);
    }

    @Override
    public Optional<DictType> findByName(String name) {
        return Optional.ofNullable(mapper.selectByName(name)).map(this::toDomain);
    }

    @Override
    public List<DictType> findAll() {
        return mapper.selectList().stream().map(this::toDomain).toList();
    }

    @Override
    public PageResult<DictType> findPage(String name, String type, Integer status, LocalDateTime[] createTime, Integer pageNo, Integer pageSize) {
        DictTypePageReqVO reqVO = new DictTypePageReqVO();
        reqVO.setName(name);
        reqVO.setType(type);
        reqVO.setStatus(status);
        reqVO.setCreateTime(createTime);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        PageResult<DictTypeDO> doPage = mapper.selectPage(reqVO);
        return new PageResult<>(doPage.getList().stream().map(this::toDomain).toList(), doPage.getTotal());
    }

    @Override
    public DictTypeDO findDoById(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public DictTypeDO findDoByType(String type) {
        return mapper.selectByType(type);
    }

    @Override
    public DictTypeDO findDoByName(String name) {
        return mapper.selectByName(name);
    }

    @Override
    public List<DictTypeDO> findDoByIds(List<Long> ids) {
        return mapper.selectByIds(ids);
    }

    @Override
    public List<DictTypeDO> findAllDo() {
        return mapper.selectList();
    }

    @Override
    public PageResult<DictTypeDO> findDoPage(DictTypePageReqVO pageReqVO) {
        return mapper.selectPage(pageReqVO);
    }

    private DictTypeDO toDO(DictType dictType) {
        DictTypeDO dictTypeDO = new DictTypeDO();
        dictTypeDO.setId(dictType.id() != null ? dictType.id().value() : null);
        dictTypeDO.setName(dictType.name().value());
        dictTypeDO.setType(dictType.type().value());
        dictTypeDO.setStatus(dictType.status());
        dictTypeDO.setRemark(dictType.remark());
        return dictTypeDO;
    }

    private DictType toDomain(DictTypeDO dictType) {
        return new DictType(DictTypeId.of(dictType.getId()), DictTypeName.of(dictType.getName()), DictTypeKey.of(dictType.getType()),
                dictType.getStatus(), dictType.getRemark());
    }

}
