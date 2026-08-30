package com.Arjun.MyMarket.cart_order.service;

import com.Arjun.MyMarket.cart_order.client.ProductClient;
import com.Arjun.MyMarket.cart_order.dto.*;
import com.Arjun.MyMarket.cart_order.entity.Cart;
import com.Arjun.MyMarket.cart_order.entity.CartItem;
import com.Arjun.MyMarket.cart_order.entity.CartStatus;
import com.Arjun.MyMarket.cart_order.exception.BusinessRuleException;
import com.Arjun.MyMarket.cart_order.exception.ExternalServiceException;
import com.Arjun.MyMarket.cart_order.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.cart_order.repository.CartItemRepository;
import com.Arjun.MyMarket.cart_order.repository.CartRepository;
//import jakarta.transaction.Transactional;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    //private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    public CartServiceImpl(CartRepository cartRepository, ProductClient productClient){
        this.cartRepository = cartRepository;
        //this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
    }

    //to get the cart for particular user
    //also transactional with readOnly true means the transaction here is opened for reading like SELECT queries
    //any update in the entity should not be stored in database by hibernate
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId){
        return toResponse(getOrCreateActiveCart(userId));
    }


    //adding item to cart
    @Override
    public CartResponse addItem(String userId, AddCartItemRequest request){

        //this fetches active cart if present or creates a new cart
        Cart cart = getOrCreateActiveCart(userId);

        //fetching product from product service
        ProductSnapshot product = fetchProduct(request.productId());

       log.debug("This is the product: {}", product);

       //it finds the cart item which is required to be added more or if not present then creates the new item
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductId(request.productId());
                    cart.getCartItems().add(newItem);
                    return newItem;
                });

        //setting details of cart item
        cartItem.setProductTitle(product.title());
        cartItem.setUnitPrice(finalUnitPrice(product.price(), product.discount()));
        cartItem.setDiscountPercent(defaultZero(product.discount()));
        cartItem.setQuantity(safeQuantity(cartItem.getQuantity()) + request.quantity());

        //cartItemRepository.save(cartItem);

        //we dont need to save the cartItem in cartItemRepository as cart is our owning entity so hibernate does the dirty checking
        //and checks if children entity like cartitem is updated or not and it will save the cartItem in db while saving the cart
        return toResponse(cartRepository.save(cart));

    }

    //removing any cart item
    @Override
    public CartResponse removeItem(String userId, String productId) {

        //fetching the cart
        Cart cart = getOrCreateActiveCart(userId);

        //finding the cart item to be removed from the cart
        CartItem item = findCartItem(cart, parseProductId(productId));

        //removing the item
        cart.getCartItems().remove(item);

        return toResponse(cartRepository.save(cart));
    }

    //updating the items quantity
    @Override
    public CartResponse updateItem(String userId, String productId, UpdateCartItemRequest request) {

        //fetching the cart
        Cart cart = getOrCreateActiveCart(userId);

        //fetching the cart item whose quantity is to be adjusted
        CartItem item = findCartItem(cart, parseProductId(productId));

        //setting the quantity
        item.setQuantity(request.quantity());

        return toResponse(cartRepository.save(cart));
    }

    //clearing the cart completely
    @Override
    public void clearCart(String userId) {
        Cart cart = getOrCreateActiveCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    //finding cartItem from cart
    public CartItem findCartItem(Cart cart, UUID productId){
        try{
            return cart.getCartItems().stream()
                    .filter(item -> item.getProductId().equals(productId))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Item is not present: " + productId));
        }catch(ResourceNotFoundException ex){

            //throwing again so that caller can handle it
            throw ex;
        }catch(Exception ex){ // catching and throwing unexpected exceptions


            throw new RuntimeException("Cart item not found");
        }

    }

    //receiving the productId in string format and returning it in UUID format
    public UUID parseProductId(String productId){

        try{
            return UUID.fromString(productId);
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Invalid product id: " + productId);
        }
    }

    @Retry(name="createOrderRetry", fallbackMethod = "createOrderFallback")
    //fetching the product from the product service using productClient interface
    public ProductSnapshot fetchProduct(UUID productId){
        try{
            ProductSnapshot product= productClient.getProductById(productId);
            log.debug("This is product {}", product);

            if(product == null || Boolean.FALSE.equals(product.live())){
                throw new BusinessRuleException("Product is not available: " + productId);
            }

           
            return product;
        }catch(BusinessRuleException ex){
            throw ex;
        }catch(Exception ex){
            throw new ExternalServiceException("Failed to load the product: " + productId, ex);
        }
    }

    //fallback method - This fallback is for retry mechanism.
    // Method signature should be same as parent method where fallback is applied
    //Throwable has to be added as paramter in fallback method
    public ProductSnapshot createOrderFallback(UUID productId, Throwable t){
        log.info("Fallback method activated!!!");
        return null;
    }

    private int safeQuantity(Integer qty){
        return qty == null ? 0 : qty;
    }
    private BigDecimal finalUnitPrice(Double price, Integer discount){
        BigDecimal base = BigDecimal.valueOf(price == null ? 0.0 : price);
        BigDecimal discountFactor = BigDecimal.valueOf(100-defaultZero(discount)).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        return base.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }

    private int defaultZero(Integer value){
        return value == null ? 0 : value;
    }


    //get the cart if exists with Active status or create a new one if does not exists with ACTIVE status
    public Cart getOrCreateActiveCart(String userId){
        if(!StringUtils.hasText(userId)){
            throw new BusinessRuleException("User id is required");
        }

        //find the cart with ACTIVE status or creating a new cart
        return cartRepository.findByUserIdAndStatus(normalize(userId), CartStatus.ACTIVE)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(normalize(userId));
                    cart.setStatus(CartStatus.ACTIVE);
                    cart.setCartItems(new ArrayList<>());
                   return cartRepository.save(cart);
                });
    }

    //converting cart entity to dto response
    public CartResponse toResponse(Cart cart){
        //CartResponse cartResponse = new CartResponse();
        List<CartItemResponse> items = cart.getCartItems().stream().map(this::toItemResponse).toList();

        BigDecimal totalAmount = items.stream().map(CartItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);

        CartResponse cartResponse = new CartResponse(
                cart.getId(),
                cart.getUserId(),
                cart.getStatus(),
                totalAmount,
                items,
                cart.getCreatedAt(),
                cart.getUpdatedAt(),
                cart.getCheckedOutAt()
                );
        return cartResponse;
    }

    //converting CartItem entity to CartItemResponse dto
    public CartItemResponse toItemResponse(CartItem cartItem){
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getProductTitle(),
                cartItem.getUnitPrice(),
                cartItem.getDiscountPercent(),
                cartItem.getQuantity(),
                cartItem.getLineTotal()
        );
    }


    private String normalize(String userId){
        return userId.trim();
    }
}
