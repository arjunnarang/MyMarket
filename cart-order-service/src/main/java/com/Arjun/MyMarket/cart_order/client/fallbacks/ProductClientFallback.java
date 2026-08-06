package com.Arjun.MyMarket.cart_order.client.fallbacks;

import com.Arjun.MyMarket.cart_order.client.ProductClient;
import com.Arjun.MyMarket.cart_order.dto.ProductSnapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ProductClientFallback implements ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    @Override
    public ProductSnapshot getProductById(UUID productId) {
        log.info("product fallback");

        return new ProductSnapshot(UUID.randomUUID(),
                "demo product",
                "this is a demo product for all back",
                "this is a demo product for fallback",
                0.0,
                0,
                false);
    }
}
