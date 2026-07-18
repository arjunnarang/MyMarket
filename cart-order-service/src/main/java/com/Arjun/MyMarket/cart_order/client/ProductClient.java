package com.Arjun.MyMarket.cart_order.client;

import com.Arjun.MyMarket.cart_order.dto.ProductSnapshot;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "productClient", url = "${services.product.base-url}")
public interface ProductClient {

    @GetMapping("/api/products/{productId}")
    ProductSnapshot getProductById(@PathVariable UUID productId);
}
