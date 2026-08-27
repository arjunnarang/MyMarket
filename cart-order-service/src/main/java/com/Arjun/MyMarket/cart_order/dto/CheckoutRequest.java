package com.Arjun.MyMarket.cart_order.dto;

import com.Arjun.MyMarket.cart_order.entity.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @NotBlank String billingName,
        @NotBlank String billingPhone,
        @NotBlank String shippingAddress,
        PaymentMethod paymentMethod,
        String extraInformation
) {
}
