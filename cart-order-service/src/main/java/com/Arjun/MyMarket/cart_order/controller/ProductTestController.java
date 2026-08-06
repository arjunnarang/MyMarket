package com.Arjun.MyMarket.cart_order.controller;

import com.Arjun.MyMarket.cart_order.client.ProductClient;
import com.Arjun.MyMarket.cart_order.dto.ProductSnapshot;
import com.Arjun.MyMarket.cart_order.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/test/api/fetch")
public class ProductTestController {

    public final CartService cartService;
    public final ProductClient productClient;

    public ProductTestController(CartService cartService, ProductClient productClient){
        this.cartService = cartService;
        this.productClient = productClient;
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductSnapshot> fetchProduct(@PathVariable UUID id){

        return ResponseEntity.ok(cartService.fetchProduct(id));
    }
}
