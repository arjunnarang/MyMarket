package com.Arjun.MyMarket.cart_order.repository;

import com.Arjun.MyMarket.cart_order.entity.Cart;
import com.Arjun.MyMarket.cart_order.entity.CartItem;
import com.Arjun.MyMarket.cart_order.entity.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserIdAndStatus(String userId, CartStatus status);
}
