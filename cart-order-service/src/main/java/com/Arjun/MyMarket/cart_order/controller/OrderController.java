package com.Arjun.MyMarket.cart_order.controller;

import com.Arjun.MyMarket.cart_order.dto.CheckoutRequest;
import com.Arjun.MyMarket.cart_order.dto.OrderResponse;
import com.Arjun.MyMarket.cart_order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/orders")
public class OrderController {

    public final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    //check out of an order
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderResponse> checkout(@PathVariable String userId, @Valid @RequestBody CheckoutRequest request){
        return ResponseEntity.ok(orderService.checkout(userId, request));
    }

    //finding a particular order by order id
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId){
        return ResponseEntity.ok().body(orderService.getOrderById(orderId));
    }

    //find order by order number
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<OrderResponse> getOrderByNumber(@PathVariable String orderNumber){
        return ResponseEntity.ok().body(orderService.getOrderByNumber(orderNumber));
    }

    //finding list of orders associated to a particular user
    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable String userId){
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    //cancelling the order
    @DeleteMapping("/{orderId")
    public OrderResponse cancelOrder(@PathVariable Long orderId){
        return orderService.cancelOrder(orderId);
    }
}
