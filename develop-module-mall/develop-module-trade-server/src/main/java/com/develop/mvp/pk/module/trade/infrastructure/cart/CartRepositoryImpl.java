package com.develop.mvp.pk.module.trade.infrastructure.cart;

// Skill: AggregateRoot_Cart_Validation_Skill — 仓储实现 CartRepositoryImpl

import com.develop.mvp.pk.module.trade.dal.dataobject.cart.CartDO;
import com.develop.mvp.pk.module.trade.dal.mysql.cart.CartMapper;
import com.develop.mvp.pk.module.trade.domain.cart.Cart;
import com.develop.mvp.pk.module.trade.domain.cart.CartFactory;
import com.develop.mvp.pk.module.trade.domain.cart.repository.CartRepository;
import com.develop.mvp.pk.module.trade.domain.cart.valueobject.CartId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CartRepositoryImpl implements CartRepository {

    private final CartMapper cartMapper;

    public CartRepositoryImpl(CartMapper cartMapper) { this.cartMapper = cartMapper; }

    @Override
    @Transactional
    public Cart save(Cart cart) {
        CartDO cartDO = toDataObject(cart);
        if (cartMapper.selectById(cart.id().value()) == null) {
            cartMapper.insert(cartDO);
        } else {
            cartMapper.updateById(cartDO);
        }
        return cart;
    }

    @Override
    @Transactional
    public void delete(CartId id) { cartMapper.deleteById(id.value()); }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) { cartMapper.deleteByUserId(userId); }

    @Override
    public Cart findById(CartId id) {
        CartDO cartDO = cartMapper.selectById(id.value());
        return cartDO != null ? toDomain(cartDO) : null;
    }

    @Override
    public List<Cart> findByUserId(Long userId) {
        return cartMapper.selectListByUserId(userId).stream()
                .map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Cart findByUserIdAndSkuId(Long userId, Long skuId) {
        CartDO cartDO = cartMapper.selectByUserIdAndSkuId(userId, skuId);
        return cartDO != null ? toDomain(cartDO) : null;
    }

    @Override
    @Transactional
    public void deleteSelectedByUserId(Long userId) {
        cartMapper.deleteByUserIdAndSelected(userId, true);
    }

    @Override
    @Transactional
    public void selectAllByUserId(Long userId) {
        cartMapper.updateSelectedByUserId(userId, true);
    }

    @Override
    @Transactional
    public void unselectAllByUserId(Long userId) {
        cartMapper.updateSelectedByUserId(userId, false);
    }

    private CartDO toDataObject(Cart cart) {
        CartDO cartDO = new CartDO();
        cartDO.setId(cart.id().value());
        cartDO.setUserId(cart.userId());
        cartDO.setSpuId(cart.spuId());
        cartDO.setSkuId(cart.skuId());
        cartDO.setCount(cart.count());
        cartDO.setSelected(cart.selected());
        return cartDO;
    }

    private Cart toDomain(CartDO cartDO) {
        return CartFactory.reconstitute(
                cartDO.getId(), cartDO.getUserId(), cartDO.getSpuId(),
                cartDO.getSkuId(), cartDO.getCount(), cartDO.getSelected());
    }
}
