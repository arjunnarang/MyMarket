package com.Arjun.MyMarket.cart_order.dto;

import com.Arjun.MyMarket.cart_order.entity.CartItem;
import com.Arjun.MyMarket.cart_order.entity.CartStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record CartResponse(
        Long id,
        String userId,
        CartStatus staus,
        BigDecimal totalAMount,
        List<CartItem> cartItems,
        Instant createdAt,
        Instant updatedAt,
        Instant checkedOutAt

) {
}
