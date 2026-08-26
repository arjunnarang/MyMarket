package com.Arjun.MyMarket.cart_order.repository;


import com.Arjun.MyMarket.cart_order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    //name of the method like this so we dont have to write jpql queries
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    Optional<Order> findByOrderNumber(String orderNumber);
}
