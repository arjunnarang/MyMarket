package com.Arjun.MyMarket.cart_order.client;

import com.Arjun.MyMarket.cart_order.dto.InventorySnapshot;
import com.Arjun.MyMarket.cart_order.dto.ReleaseStockRequest;
import com.Arjun.MyMarket.cart_order.dto.ReserveStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@FeignClient(name = "${inventory.service.id}")
public interface InventoryClient {

    @PostMapping("/api/inventories/product/{productId}/reserve")
    InventorySnapshot reserveByProductId(@PathVariable UUID productId, ReserveStockRequest request);

    @PostMapping("/api/inventories/product/{productId}/release")
    InventorySnapshot releaseByProductId(UUID productId, ReleaseStockRequest request);
}
