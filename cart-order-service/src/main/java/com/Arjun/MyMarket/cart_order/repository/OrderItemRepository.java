package com.Arjun.MyMarket.cart_order.repository;

import com.Arjun.MyMarket.cart_order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
