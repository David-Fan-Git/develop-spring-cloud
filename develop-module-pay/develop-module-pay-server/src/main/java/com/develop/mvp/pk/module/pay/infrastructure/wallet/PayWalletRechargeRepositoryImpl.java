package com.develop.mvp.pk.module.pay.infrastructure.wallet;
// DDD 角色：钱包充值仓储实现 - AggregateRoot_Pay_Skill
import com.develop.mvp.pk.framework.common.pojo.PageParam;
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.dal.dataobject.wallet.PayWalletRechargeDO;
import com.develop.mvp.pk.module.pay.dal.mysql.wallet.PayWalletRechargeMapper;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWalletRecharge;
import com.develop.mvp.pk.module.pay.domain.wallet.repository.PayWalletRechargeRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.stream.Collectors;
@Repository
public class PayWalletRechargeRepositoryImpl implements PayWalletRechargeRepository {
    @Resource private PayWalletRechargeMapper mapper;
    static PayWalletRecharge toDomain(PayWalletRechargeDO doObj) {
        if (doObj == null) return null;
        PayWalletRecharge r = new PayWalletRecharge(doObj.getId());
        r.walletId(doObj.getWalletId()).totalPrice(doObj.getTotalPrice()).payPrice(doObj.getPayPrice())
                .bonusPrice(doObj.getBonusPrice()).packageId(doObj.getPackageId())
                .payStatus(doObj.getPayStatus()).payOrderId(doObj.getPayOrderId())
                .payChannelCode(doObj.getPayChannelCode()).payTime(doObj.getPayTime())
                .payRefundId(doObj.getPayRefundId())
                .refundTotalPrice(doObj.getRefundTotalPrice()).refundPayPrice(doObj.getRefundPayPrice())
                .refundBonusPrice(doObj.getRefundBonusPrice()).refundTime(doObj.getRefundTime())
                .refundStatus(doObj.getRefundStatus());
        return r;
    }
    @Override public PayWalletRecharge save(PayWalletRecharge recharge) {
        if (recharge.id() == null) {
            PayWalletRechargeDO doObj = new PayWalletRechargeDO();
            doObj.setWalletId(recharge.walletId()); doObj.setTotalPrice(recharge.totalPrice());
            doObj.setPayPrice(recharge.payPrice()); doObj.setBonusPrice(recharge.bonusPrice());
            doObj.setPackageId(recharge.packageId()); doObj.setPayStatus(recharge.payStatus());
            mapper.insert(doObj);
            return recharge;
        }
        return recharge;
    }
    @Override public PayWalletRecharge findById(Long id) { return toDomain(mapper.selectById(id)); }
    @Override public PageResult<PayWalletRecharge> findPage(Long walletId, Boolean payStatus, Integer pageNo, Integer pageSize) {
        PageResult<PayWalletRechargeDO> page = mapper.selectPage(
                new PageParam().setPageNo(pageNo).setPageSize(pageSize), walletId, payStatus);
        return new PageResult<>(page.getList().stream().map(PayWalletRechargeRepositoryImpl::toDomain).collect(Collectors.toList()), page.getTotal());
    }
    @Override public int updateByIdAndPaid(Long id, boolean wherePayStatus, PayWalletRecharge recharge) {
        PayWalletRechargeDO update = new PayWalletRechargeDO();
        update.setPayStatus(recharge.payStatus()); update.setPayTime(recharge.payTime());
        update.setPayChannelCode(recharge.payChannelCode());
        return mapper.updateByIdAndPaid(id, wherePayStatus, update);
    }
    @Override public int updateByIdAndRefunded(Long id, Integer whereRefundStatus, PayWalletRecharge recharge) {
        PayWalletRechargeDO update = new PayWalletRechargeDO();
        update.setRefundStatus(recharge.refundStatus()); update.setRefundTime(recharge.refundTime());
        update.setRefundTotalPrice(recharge.refundTotalPrice());
        update.setRefundPayPrice(recharge.refundPayPrice()); update.setRefundBonusPrice(recharge.refundBonusPrice());
        return mapper.updateByIdAndRefunded(id, whereRefundStatus, update);
    }
}
