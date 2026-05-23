package com.develop.mvp.pk.module.pay.application.wallet;
// DDD 角色：钱包应用服务 - AggregateRoot_Pay_Skill
import com.develop.mvp.pk.framework.common.pojo.PageResult;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWallet;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWalletFactory;
import com.develop.mvp.pk.module.pay.domain.wallet.PayWalletTransaction;
import com.develop.mvp.pk.module.pay.domain.wallet.repository.PayWalletRepository;
import com.develop.mvp.pk.module.pay.domain.wallet.repository.PayWalletTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
public class PayWalletApplicationService {
    private final PayWalletRepository walletRepo;
    private final PayWalletTransactionRepository transactionRepo;
    @Transactional public PayWallet getOrCreate(Long userId, Integer userType) {
        return walletRepo.findByUserIdAndType(userId, userType)
                .orElseGet(() -> walletRepo.save(PayWalletFactory.create(null, userId, userType)));
    }
    @Transactional public PayWalletTransaction addBalance(Long walletId, String bizId, Integer bizType,
                                                           Integer price, String title) {
        PayWallet wallet = walletRepo.findById(walletId);
        if (wallet == null) throw new IllegalArgumentException("Wallet not found: " + walletId);
        wallet.addBalance(price);
        walletRepo.save(wallet);
        PayWalletTransaction tx = new PayWalletTransaction(null)
                .walletId(walletId).bizType(bizType).bizId(bizId)
                .price(price).balance(wallet.balance()).title(title);
        return transactionRepo.save(tx);
    }
    @Transactional public PayWalletTransaction deductBalance(Long walletId, Long bizId, Integer bizType, Integer price) {
        PayWallet wallet = walletRepo.findById(walletId);
        if (wallet == null) throw new IllegalArgumentException("Wallet not found: " + walletId);
        wallet.deductBalance(price);
        wallet.addExpense(price);
        walletRepo.save(wallet);
        PayWalletTransaction tx = new PayWalletTransaction(null)
                .walletId(walletId).bizType(bizType).bizId(String.valueOf(bizId))
                .price(-price).balance(wallet.balance()).title(bizType.toString());
        return transactionRepo.save(tx);
    }
    @Transactional public void freezePrice(Long walletId, Integer price) {
        PayWallet wallet = walletRepo.findById(walletId);
        if (wallet == null) throw new IllegalArgumentException("Wallet not found: " + walletId);
        wallet.freeze(price);
        walletRepo.save(wallet);
    }
    @Transactional public void unfreezePrice(Long walletId, Integer price) {
        PayWallet wallet = walletRepo.findById(walletId);
        if (wallet == null) throw new IllegalArgumentException("Wallet not found: " + walletId);
        wallet.unfreeze(price);
        walletRepo.save(wallet);
    }
    public PayWallet get(Long id) { return walletRepo.findById(id); }
    public PayWallet getByUserId(Long userId, Integer userType) {
        return walletRepo.findByUserIdAndType(userId, userType).orElse(null);
    }
    public PageResult<PayWallet> getPage(Long userId, Integer userType, Integer pageNo, Integer pageSize) {
        return walletRepo.findPage(userId, userType, pageNo, pageSize);
    }
}
