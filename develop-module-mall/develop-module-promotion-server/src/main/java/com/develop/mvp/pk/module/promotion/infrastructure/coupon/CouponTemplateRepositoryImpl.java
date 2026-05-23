package com.develop.mvp.pk.module.promotion.infrastructure.coupon;

// Skill: AggregateRoot_CouponTemplate_Validation_Skill — 仓储实现

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.promotion.dal.dataobject.coupon.CouponTemplateDO;
import com.develop.mvp.pk.module.promotion.dal.mysql.coupon.CouponTemplateMapper;
import com.develop.mvp.pk.module.promotion.domain.coupon.CouponTemplate;
import com.develop.mvp.pk.module.promotion.domain.coupon.CouponTemplateFactory;
import com.develop.mvp.pk.module.promotion.domain.coupon.repository.CouponTemplateRepository;
import com.develop.mvp.pk.module.promotion.domain.coupon.valueobject.CouponTemplateId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CouponTemplateRepositoryImpl implements CouponTemplateRepository {

    private final CouponTemplateMapper couponTemplateMapper;

    public CouponTemplateRepositoryImpl(CouponTemplateMapper couponTemplateMapper) {
        this.couponTemplateMapper = couponTemplateMapper;
    }

    @Override
    @Transactional
    public CouponTemplate save(CouponTemplate template) {
        CouponTemplateDO templateDO = toDataObject(template);
        if (couponTemplateMapper.selectById(template.id().value()) == null) {
            couponTemplateMapper.insert(templateDO);
        } else {
            couponTemplateMapper.updateById(templateDO);
        }
        return template;
    }

    @Override
    @Transactional
    public void delete(CouponTemplateId id) {
        couponTemplateMapper.deleteById(id.value());
    }

    @Override
    public CouponTemplate findById(CouponTemplateId id) {
        CouponTemplateDO templateDO = couponTemplateMapper.selectById(id.value());
        return templateDO != null ? toDomain(templateDO) : null;
    }

    @Override
    public List<CouponTemplate> findByStatus(Integer status) {
        return couponTemplateMapper.selectList(CouponTemplateDO::getStatus, status).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<CouponTemplate> findAll() {
        return couponTemplateMapper.selectList().stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<CouponTemplate> findPage(String name, Integer status, Integer discountType,
                                                Integer pageNo, Integer pageSize) {
        var reqVO = new com.develop.mvp.pk.module.promotion.controller.admin.coupon.vo.template.CouponTemplatePageReqVO();
        reqVO.setName(name);
        reqVO.setStatus(status);
        reqVO.setDiscountType(discountType);
        reqVO.setPageNo(pageNo);
        reqVO.setPageSize(pageSize);
        PageResult<CouponTemplateDO> doPage = couponTemplateMapper.selectPage(reqVO);
        return new PageResult<>(
                doPage.getList().stream().map(this::toDomain).collect(Collectors.toList()),
                doPage.getTotal());
    }

    @Override
    public long count() {
        return couponTemplateMapper.selectCount();
    }

    private CouponTemplateDO toDataObject(CouponTemplate template) {
        CouponTemplateDO templateDO = new CouponTemplateDO();
        templateDO.setId(template.id().value());
        templateDO.setName(template.name());
        templateDO.setDescription(template.description());
        templateDO.setType(template.type());
        templateDO.setStatus(template.status());
        templateDO.setTotalCount(template.totalCount());
        templateDO.setLimitCount(template.limitCount());
        templateDO.setDistributeCount(template.distributeCount());
        templateDO.setUseCount(template.useCount());
        templateDO.setDiscountType(template.discountType());
        templateDO.setDiscountPercent(template.discountPercent());
        templateDO.setDiscountPrice(template.discountPrice());
        templateDO.setMinimumPrice(template.minimumPrice());
        templateDO.setMaximumPrice(template.maximumPrice());
        templateDO.setValidStartTime(template.validStartTime());
        templateDO.setValidEndTime(template.validEndTime());
        return templateDO;
    }

    private CouponTemplate toDomain(CouponTemplateDO templateDO) {
        return CouponTemplateFactory.reconstitute(
                templateDO.getId(), templateDO.getName(), templateDO.getDescription(),
                templateDO.getType(), templateDO.getStatus(),
                templateDO.getTotalCount(), templateDO.getLimitCount(),
                templateDO.getDistributeCount(), templateDO.getUseCount(),
                templateDO.getDiscountType(), templateDO.getDiscountPercent(),
                templateDO.getDiscountPrice(), templateDO.getMinimumPrice(),
                templateDO.getMaximumPrice(),
                templateDO.getValidStartTime(), templateDO.getValidEndTime());
    }
}
