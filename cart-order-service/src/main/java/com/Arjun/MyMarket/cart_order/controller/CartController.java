package com.Arjun.MyMarket.cart_order.controller;

import com.Arjun.MyMarket.cart_order.dto.AddCartItemRequest;
import com.Arjun.MyMarket.cart_order.dto.CartResponse;
import com.Arjun.MyMarket.cart_order.dto.UpdateCartItemRequest;
import com.Arjun.MyMarket.cart_order.service.CartService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/api/carts")
public class CartController {

    private final CartService cartService;

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    public CartController(CartService cartService){
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable String userId){
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable String userId, @Valid @RequestBody AddCartItemRequest request){
        log.debug("Code is coming till here");
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/{userId}/items/{productId}")
    public CartResponse updateitem(@PathVariable String userId, @PathVariable String productId, @RequestBody UpdateCartItemRequest request){
        return cartService.updateItem(userId, productId, request);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public CartResponse removeItem(@PathVariable String userId, @PathVariable String productId){

        return cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable String userId){
        return cartService.clearCart(userId);
    }
}
