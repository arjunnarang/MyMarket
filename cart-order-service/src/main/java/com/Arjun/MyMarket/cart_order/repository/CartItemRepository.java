package com.Arjun.MyMarket.cart_order.repository;

import com.Arjun.MyMarket.cart_order.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
