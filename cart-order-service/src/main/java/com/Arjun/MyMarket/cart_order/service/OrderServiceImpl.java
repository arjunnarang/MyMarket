package com.Arjun.MyMarket.cart_order.service;

import com.Arjun.MyMarket.cart_order.client.InventoryClient;
import com.Arjun.MyMarket.cart_order.dto.*;
import com.Arjun.MyMarket.cart_order.entity.*;
import com.Arjun.MyMarket.cart_order.exception.BusinessRuleException;
import com.Arjun.MyMarket.cart_order.exception.ExternalServiceException;
import com.Arjun.MyMarket.cart_order.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.cart_order.repository.CartRepository;
import com.Arjun.MyMarket.cart_order.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService{

   private final OrderRepository orderRepository;
   private final CartRepository cartRepository;
   private final InventoryClient inventoryClient;

   public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository, InventoryClient inventoryClient){
       this.orderRepository = orderRepository;
       this.cartRepository = cartRepository;
       this.inventoryClient = inventoryClient;
   }

   @Override
   public OrderResponse checkout(String userId, CheckoutRequest checkoutRequest){
       Cart cart = cartRepository.findByUserIdAndStatus(normalizeUserId(userId), CartStatus.ACTIVE)
               .orElseThrow(() -> new ResourceNotFoundException("Active cart not found for userId: " + userId));


       if(cart.getCartItems().isEmpty()){
           throw new BusinessRuleException("Cart is empty");
       }

       //this is the list for reserved items
       List<InventorySnapshot> reservedSnapshot = new ArrayList<>();

       try{

           for(CartItem item : cart.getCartItems()){
               reservedSnapshot.add(inventoryClient.reserveByProductId(item.getProductId(), new ReserveStockRequest(item.getQuantity())));
           }

           Order order = buildOrderFromCart(cart, checkoutRequest);

           Order orderSaved = orderRepository.save(order);

           cart.setStatus(CartStatus.CHECKED_OUT);
           cart.setCheckedOutAt(Instant.now());
           cart.getCartItems().clear();
           cartRepository.save(cart);

           return toResponse(orderSaved);

       } catch (RuntimeException e) {
           for(int i = reservedSnapshot.size(); i>=0; i--){
               CartItem item = cart.getCartItems().get(i);
               try{
                   inventoryClient.releaseByProductId(item.getProductId(), new ReleaseStockRequest(item.getQuantity()));
               }catch(Exception releaseEx){
                   throw new ExternalServiceException("Checkout failed and stock rollback also failed for productId: " + item.getProductId(), releaseEx);
               }
           }
           if(e instanceof ExternalServiceException externalServiceException){
               throw externalServiceException;
           }
           throw new ExternalServiceException("Checkout failed", e);
       }

   }


   //fetching order by order id
   @Override
   @Transactional(readOnly = true)
   public OrderResponse getOrderById(Long orderId){
        return toResponse(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found for order id: " + orderId)));
   }

   //fetching order by ordernumber
   @Override
   @Transactional(readOnly = true)
   public OrderResponse getOrderByNumber(String orderNumber){
       return toResponse(orderRepository.findByOrderNumber(orderNumber).orElseThrow(() -> new ResourceNotFoundException("Order not found for order number: " + orderNumber)));

   }

   //fetching all orders of single user id
   @Override
   @Transactional( readOnly = true)
   public List<OrderResponse> getOrdersByUserId(String userId){
       return orderRepository.findByUserIdOrderByCreatedAtDesc(normalizeUserId(userId))
               .stream()
               .map(orderResponse -> toResponse(orderResponse))
               .toList();
   }

   //cancellation of an order using order id
   @Override
   public OrderResponse cancelOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("Order not found of id: " + orderId));

        if(order.getStatus() == OrderStatus.CANCELLED){
            return toResponse(order);
        }

        for(OrderItem item : order.getOrderItems()){
            try{
                inventoryClient.releaseByProductId(item.getProductId(), new ReleaseStockRequest(item.getQuantity()));
            }catch(Exception ex){
                throw new ExternalServiceException("Failed to release stock for product id: " + item.getProductId());
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());

        return toResponse(order);
   }

   //releasing the reserved stock if cart is cleared or item is removed
    @Override
    public void releaseReservedStock(UUID productId, Integer quantity) {
        try {
            inventoryClient.releaseByProductId(productId, new ReleaseStockRequest(quantity));
        } catch (Exception ex) {
            throw new ExternalServiceException("Failed to release stock for productId: " + productId, ex);
        }
    }

    private OrderResponse toResponse(Order order){
       List<OrderItemResponse> items = order.getOrderItems().stream().map(item -> toItemResponse(item)).toList();

       return new OrderResponse( order.getId(),
               order.getBillingName(),
               order.getBillingPhone(),
               order.getOrderNumber(),
               order.getUserId(),
               order.getShippingAddress(),
               order.getPaymentMethod(),
               order.getPaymentStatus(),
               order.getExtraInformation(),
               order.getStatus(),
               order.getTotalAmount(),
               order.getCreatedAt(),
               order.getUpdatedAt(),
               order.getCancelledAt(),
               items);
   }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductTitle(),
                item.getUnitPrice(),
                item.getDiscountPercent(),
                item.getQuantity(),
                item.getLineTotal());
    }
   private Order buildOrderFromCart(Cart cart, CheckoutRequest checkoutRequest){
       Order order = new Order();
       order.setOrderNumber(UUID.randomUUID().toString());
       order.setUserId(cart.getUserId());
       order.setBillingName(checkoutRequest.billingName());
       order.setBillingPhone(checkoutRequest.billingPhone());
       order.setShippingAddress(checkoutRequest.shippingAddress());
       order.setPaymentMethod(checkoutRequest.paymentMethod());
       order.setExtraInformation(checkoutRequest.extraInformation());
       order.setStatus(OrderStatus.CONFIRMED);
       order.setOrderItems(new ArrayList<>());

       BigDecimal total = BigDecimal.ZERO;

       for(CartItem cartItem : cart.getCartItems()){
           OrderItem orderItem = new OrderItem();

           orderItem.setOrder(order);
           orderItem.setProductId(cartItem.getProductId());
           orderItem.setProductTitle(cartItem.getProductTitle());
           orderItem.setUnitPrice(cartItem.getUnitPrice());
           orderItem.setDiscountPercent(cartItem.getDiscountPercent());
           orderItem.setQuantity(cartItem.getQuantity());
           orderItem.setLineTotal(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
           order.getOrderItems().add(orderItem);
           total = total.add(orderItem.getLineTotal());


       }
       order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
       return order;
   }

   private String normalizeUserId(String userId){
        if(userId == null || userId.isBlank()){
            throw new BusinessRuleException("UserId is required");
        }

        return userId.trim();
   }


}
