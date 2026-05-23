package com.develop.mvp.pk.module.promotion.infrastructure.seckill;

// Skill: AggregateRoot_SeckillActivity_Validation_Skill — 仓储实现

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.promotion.dal.dataobject.seckill.SeckillActivityDO;
import com.develop.mvp.pk.module.promotion.dal.dataobject.seckill.SeckillProductDO;
import com.develop.mvp.pk.module.promotion.dal.mysql.seckill.seckillactivity.SeckillActivityMapper;
import com.develop.mvp.pk.module.promotion.dal.mysql.seckill.seckillactivity.SeckillProductMapper;
import com.develop.mvp.pk.module.promotion.domain.seckill.SeckillActivity;
import com.develop.mvp.pk.module.promotion.domain.seckill.SeckillActivityFactory;
import com.develop.mvp.pk.module.promotion.domain.seckill.repository.SeckillActivityRepository;
import com.develop.mvp.pk.module.promotion.domain.seckill.valueobject.SeckillActivityId;
import com.develop.mvp.pk.module.promotion.domain.seckill.valueobject.SeckillProduct;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {

    private final SeckillActivityMapper seckillActivityMapper;
    private final SeckillProductMapper seckillProductMapper;

    public SeckillActivityRepositoryImpl(SeckillActivityMapper seckillActivityMapper,
                                          SeckillProductMapper seckillProductMapper) {
        this.seckillActivityMapper = seckillActivityMapper;
        this.seckillProductMapper = seckillProductMapper;
    }

    @Override
    @Transactional
    public SeckillActivity save(SeckillActivity activity) {
        SeckillActivityDO activityDO = toDataObject(activity);
        if (seckillActivityMapper.selectById(activity.id().value()) == null) {
            seckillActivityMapper.insert(activityDO);
        } else {
            seckillActivityMapper.updateById(activityDO);
        }
        for (SeckillProduct product : activity.products()) {
            SeckillProductDO productDO = toProductDataObject(product, activity.id().value(), activity.status());
            if (product.id() != null && seckillProductMapper.selectById(product.id()) != null) {
                seckillProductMapper.updateById(productDO);
            } else {
                seckillProductMapper.insert(productDO);
            }
        }
        return activity;
    }

    @Override
    @Transactional
    public void delete(SeckillActivityId id) {
        seckillActivityMapper.deleteById(id.value());
        seckillProductMapper.deleteByActivityId(id.value());
    }

    @Override
    public SeckillActivity findById(SeckillActivityId id) {
        SeckillActivityDO activityDO = seckillActivityMapper.selectById(id.value());
        return activityDO != null ? toDomain(activityDO) : null;
    }

    @Override
    public List<SeckillActivity> findByStatus(Integer status) {
        return seckillActivityMapper.selectList(SeckillActivityDO::getStatus, status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<SeckillActivity> findActiveActivities() {
        return seckillActivityMapper.selectListByStatus(
                com.develop.mvp.pk.framework.common.enums.CommonStatusEnum.ENABLE.getStatus()).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<SeckillActivity> findPage(String name, Integer status, Long spuId,
                                                 Integer pageNo, Integer pageSize) {
        var reqVO = new com.develop.mvp.pk.module.promotion.controller.admin.seckill.vo.SeckillActivityPageReqVO();
        reqVO.setName(name);
        reqVO.setStatus(status);
        reqVO.setSpuId(spuId);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        PageResult<SeckillActivityDO> doPage = seckillActivityMapper.selectPage(reqVO);
        return new PageResult<>(
                doPage.getList().stream().map(this::toDomain).collect(Collectors.toList()),
                doPage.getTotal());
    }

    @Override
    public long count() {
        return seckillActivityMapper.selectCount();
    }

    private SeckillActivity toDomain(SeckillActivityDO activityDO) {
        List<SeckillProductDO> productDOs = seckillProductMapper.selectListByActivityId(activityDO.getId());
        List<SeckillProduct> products = productDOs.stream()
                .map(p -> new SeckillProduct(p.getId(), p.getConfigIds(), p.getSpuId(),
                        p.getSkuId(), p.getSeckillPrice(), p.getStock()))
                .collect(Collectors.toList());
        return SeckillActivityFactory.reconstitute(
                activityDO.getId(), activityDO.getSpuId(), activityDO.getName(),
                activityDO.getStatus(), activityDO.getRemark(),
                activityDO.getStartTime(), activityDO.getEndTime(), activityDO.getSort(),
                activityDO.getConfigIds(), activityDO.getTotalLimitCount(),
                activityDO.getSingleLimitCount(), activityDO.getStock(),
                activityDO.getTotalStock(), products);
    }

    private SeckillActivityDO toDataObject(SeckillActivity activity) {
        SeckillActivityDO activityDO = new SeckillActivityDO();
        activityDO.setId(activity.id().value());
        activityDO.setSpuId(activity.spuId());
        activityDO.setName(activity.name());
        activityDO.setStatus(activity.status());
        activityDO.setRemark(activity.remark());
        activityDO.setStartTime(activity.startTime());
        activityDO.setEndTime(activity.endTime());
        activityDO.setSort(activity.sort());
        activityDO.setConfigIds(activity.configIds());
        activityDO.setTotalLimitCount(activity.totalLimitCount());
        activityDO.setSingleLimitCount(activity.singleLimitCount());
        activityDO.setStock(activity.stock());
        activityDO.setTotalStock(activity.totalStock());
        return activityDO;
    }

    private SeckillProductDO toProductDataObject(SeckillProduct product, Long activityId, Integer activityStatus) {
        SeckillProductDO productDO = new SeckillProductDO();
        productDO.setId(product.id());
        productDO.setActivityId(activityId);
        productDO.setConfigIds(product.configIds());
        productDO.setSpuId(product.spuId());
        productDO.setSkuId(product.skuId());
        productDO.setSeckillPrice(product.seckillPrice());
        productDO.setStock(product.stock());
        productDO.setActivityStatus(activityStatus);
        return productDO;
    }
}
