package com.Arjun.MyMarket.cart_order.service;

import com.Arjun.MyMarket.cart_order.dto.CheckoutRequest;
import com.Arjun.MyMarket.cart_order.dto.OrderResponse;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(String userId, CheckoutRequest request);

    OrderResponse getOrderById(Long orderId);

    OrderResponse getOrderByNumber(String orderNumber);

    List<OrderResponse> getOrdersByUserId(String userId);

    OrderResponse cancelOrder(Long orderId);

    void releaseReservedStock(UUID productId, Integer quantity);

}
