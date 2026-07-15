package com.Arjun.MyMarket.cart_order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotBlank String shippingAddress,
        String paymentMethod
) {
}
