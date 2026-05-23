package com.develop.mvp.pk.module.trade.infrastructure.aftersale;

// Skill: AggregateRoot_AfterSale_Validation_Skill

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.trade.dal.dataobject.aftersale.AfterSaleDO;
import com.develop.mvp.pk.module.trade.dal.mysql.aftersale.AfterSaleMapper;
import com.develop.mvp.pk.module.trade.domain.aftersale.AfterSale;
import com.develop.mvp.pk.module.trade.domain.aftersale.AfterSaleFactory;
import com.develop.mvp.pk.module.trade.domain.aftersale.repository.AfterSaleRepository;
import com.develop.mvp.pk.module.trade.domain.aftersale.valueobject.AfterSaleId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class AfterSaleRepositoryImpl implements AfterSaleRepository {

    private final AfterSaleMapper afterSaleMapper;

    public AfterSaleRepositoryImpl(AfterSaleMapper afterSaleMapper) {
        this.afterSaleMapper = afterSaleMapper;
    }

    @Override
    @Transactional
    public AfterSale save(AfterSale afterSale) {
        AfterSaleDO afterSaleDO = toDataObject(afterSale);
        if (afterSaleMapper.selectById(afterSale.id().value()) == null) {
            afterSaleMapper.insert(afterSaleDO);
        } else {
            afterSaleMapper.updateById(afterSaleDO);
        }
        return afterSale;
    }

    @Override
    public AfterSale findById(AfterSaleId id) {
        AfterSaleDO afterSaleDO = afterSaleMapper.selectById(id.value());
        return afterSaleDO != null ? toDomain(afterSaleDO) : null;
    }

    @Override
    public AfterSale findByNo(String no) {
        AfterSaleDO afterSaleDO = afterSaleMapper.selectByNo(no);
        return afterSaleDO != null ? toDomain(afterSaleDO) : null;
    }

    @Override
    public List<AfterSale> findByUserId(Long userId) {
        return afterSaleMapper.selectList(AfterSaleDO::getUserId, userId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<AfterSale> findByOrderId(Long orderId) {
        return afterSaleMapper.selectList(AfterSaleDO::getOrderId, orderId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public PageResult<AfterSale> findPage(Long userId, Integer status, Integer type, String no,
                                           Integer pageNo, Integer pageSize) {
        var reqVO = new com.develop.mvp.pk.module.trade.controller.admin.aftersale.vo.AfterSalePageReqVO();
        reqVO.setUserId(userId); reqVO.setStatus(status); reqVO.setType(type);
        reqVO.setNo(no); reqVO.setPageNo(pageNo); reqVO.setPageSize(pageSize);
        PageResult<AfterSaleDO> doPage = afterSaleMapper.selectPage(reqVO);
        return new PageResult<>(
                doPage.getList().stream().map(this::toDomain).collect(Collectors.toList()),
                doPage.getTotal());
    }

    @Override
    public long count() { return afterSaleMapper.selectCount(); }

    private AfterSaleDO toDataObject(AfterSale afterSale) {
        AfterSaleDO afterSaleDO = new AfterSaleDO();
        afterSaleDO.setId(afterSale.id().value()); afterSaleDO.setNo(afterSale.no());
        afterSaleDO.setUserId(afterSale.userId()); afterSaleDO.setOrderId(afterSale.orderId());
        afterSaleDO.setOrderItemId(afterSale.orderItemId());
        afterSaleDO.setSpuId(afterSale.spuId()); afterSaleDO.setSkuId(afterSale.skuId());
        afterSaleDO.setCount(afterSale.count()); afterSaleDO.setType(afterSale.type());
        afterSaleDO.setReason(afterSale.reason()); afterSaleDO.setDescription(afterSale.description());
        afterSaleDO.setProofPictures(afterSale.proofPictures());
        afterSaleDO.setStatus(afterSale.status()); afterSaleDO.setRefundPrice(afterSale.refundPrice());
        afterSaleDO.setRejectReason(afterSale.rejectReason());
        afterSaleDO.setPayChannelCode(afterSale.payChannelCode());
        afterSaleDO.setPayRefundId(afterSale.payRefundId());
        afterSaleDO.setAuditTime(afterSale.auditTime());
        afterSaleDO.setRefuseTime(afterSale.refuseTime());
        afterSaleDO.setRefundTime(afterSale.refundTime());
        return afterSaleDO;
    }

    private AfterSale toDomain(AfterSaleDO afterSaleDO) {
        return AfterSaleFactory.reconstitute(
                afterSaleDO.getId(), afterSaleDO.getNo(), afterSaleDO.getUserId(),
                afterSaleDO.getOrderId(), afterSaleDO.getOrderItemId(),
                afterSaleDO.getSpuId(), afterSaleDO.getSkuId(),
                afterSaleDO.getCount(), afterSaleDO.getType(), afterSaleDO.getReason(),
                afterSaleDO.getDescription(), afterSaleDO.getProofPictures(),
                afterSaleDO.getStatus(), afterSaleDO.getRefundPrice(),
                afterSaleDO.getRejectReason(), afterSaleDO.getPayChannelCode(),
                afterSaleDO.getPayRefundId(), afterSaleDO.getAuditTime(),
                afterSaleDO.getRefuseTime(), afterSaleDO.getRefundTime());
    }
}
