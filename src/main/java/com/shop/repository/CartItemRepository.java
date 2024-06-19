package com.shop.repository;

import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 1 장바구니 ID와 아이템 ID로 장바구니 항목을 찾는 메서드
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}