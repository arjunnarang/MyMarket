package com.Arjun.MyMarket.cart_order.dto;

import com.Arjun.MyMarket.cart_order.entity.OrderStatus;
import com.Arjun.MyMarket.cart_order.entity.PaymentMethod;
import com.Arjun.MyMarket.cart_order.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String billingName,
        String billingPhone,
        String orderNumber,
        String userId,
        String shippingAddress,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        String extraInformation,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant createdAt,
        Instant updatedAt,
        Instant cancelledAt,
        List<OrderItemResponse> items
) {
}
