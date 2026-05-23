package com.develop.mvp.pk.module.pay.domain.wallet;
// DDD 角色：支付钱包聚合根
import java.util.Objects;
public final class PayWallet {
    private final Long id; private final Long userId; private Integer balance, totalRecharge, totalExpense, status;
    public PayWallet(Long id, Long userId) { this.id = id; this.userId = Objects.requireNonNull(userId); }
    public static PayWallet of(Long id, Long userId) { return new PayWallet(id, userId); }
    public Long id() { return id; } public Long userId() { return userId; }
    public Integer balance() { return balance; } public Integer status() { return status; }
    public PayWallet balance(Integer v) { balance = v; return this; } public PayWallet totalRecharge(Integer v) { totalRecharge = v; return this; }
    public PayWallet totalExpense(Integer v) { totalExpense = v; return this; } public PayWallet status(Integer v) { status = v; return this; }
    @Override public boolean equals(Object o) { return o instanceof PayWallet w && id.equals(w.id); }
    @Override public int hashCode() { return Objects.hash(id); }
}
