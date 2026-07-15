package com.Arjun.MyMarket.cart_order.dto;

import com.Arjun.MyMarket.cart_order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        String userId,
        String shippingAddress,
        String paymentMethod,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        Instant cancelledAt,
        List<OrderItemResponse> items
) {
}
