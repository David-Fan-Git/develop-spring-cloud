package com.develop.mvp.pk.module.pay.application.wallet;

import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWallet;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWalletFactory;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWalletTransaction;
import com.develop.mvp.pk.module.pay.domain.wallet.repository.PayWalletRepository;
import com.develop.mvp.pk.module.pay.domain.wallet.repository.PayWalletTransactionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PayWalletApplicationServiceTest {

    @Test
    void getOrCreate_returnsPersistedWallet() {
        PayWalletApplicationService applicationService = new PayWalletApplicationService(
                new StubWalletRepository(), new StubTransactionRepository());

        PayWallet wallet = applicationService.getOrCreate(1L, 1);

        assertEquals(100L, wallet.id());
    }

    @Test
    void addBalance_createsTransientTransaction() {
        PayWalletApplicationService applicationService = new PayWalletApplicationService(
                new StubWalletRepository(), new StubTransactionRepository());

        PayWalletTransaction transaction = applicationService.addBalance(100L, "biz-1", 1, 50, "充值");

        assertEquals(200L, transaction.id());
    }

    private static final class StubWalletRepository implements PayWalletRepository {

        @Override
        public PayWallet save(PayWallet wallet) {
            return PayWalletFactory.restore(100L, wallet.userId(), wallet.userType(), wallet.balance(),
                    wallet.freezePrice(), wallet.totalRecharge(), wallet.totalExpense());
        }

        @Override
        public PayWallet findById(Long id) {
            return PayWalletFactory.restore(id, 1L, 1, 100, 0, 0, 0);
        }

        @Override
        public Optional<PayWallet> findByUserIdAndType(Long userId, Integer userType) { return Optional.empty(); }

        @Override
        public PageResult<PayWallet> findPage(Long userId, Integer userType, Integer pageNo, Integer pageSize) {
            return PageResult.empty();
        }

        @Override
        public int updateBalance(Long id, int balanceDelta) { return 0; }

        @Override
        public int updateWhenConsumption(Long id, Integer price) { return 0; }

        @Override
        public int updateWhenConsumptionRefund(Long id, Integer price) { return 0; }

        @Override
        public int updateWhenRecharge(Long id, Integer price) { return 0; }

        @Override
        public int updateWhenAdd(Long id, Integer price) { return 0; }

        @Override
        public int freezePrice(Long id, Integer price) { return 0; }

        @Override
        public int unFreezePrice(Long id, Integer price) { return 0; }

        @Override
        public int updateWhenRechargeRefund(Long id, Integer price) { return 0; }
    }

    private static final class StubTransactionRepository implements PayWalletTransactionRepository {

        @Override
        public PayWalletTransaction save(PayWalletTransaction transaction) {
            return new PayWalletTransaction(200L)
                    .walletId(transaction.walletId()).bizType(transaction.bizType()).bizId(transaction.bizId())
                    .title(transaction.title()).price(transaction.price()).balance(transaction.balance());
        }

        @Override
        public Optional<PayWalletTransaction> findByNo(String no) { return Optional.empty(); }

        @Override
        public Optional<PayWalletTransaction> findByBiz(String bizId, Integer bizType) { return Optional.empty(); }

        @Override
        public PageResult<PayWalletTransaction> findPage(Long walletId, Integer type, Integer pageNo,
                                                         Integer pageSize, LocalDateTime[] createTime) {
            return PageResult.empty();
        }

        @Override
        public Integer sumPriceByType(Long walletId, Integer type, LocalDateTime[] createTime) { return 0; }
    }
}
