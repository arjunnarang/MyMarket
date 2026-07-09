package com.Arjun.MyMarket.cart_order.repository;


import com.Arjun.MyMarket.cart_order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
