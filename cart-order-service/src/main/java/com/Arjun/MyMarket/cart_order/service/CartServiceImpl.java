package com.Arjun.MyMarket.cart_order.service;

import com.Arjun.MyMarket.cart_order.dto.AddCartItemRequest;
import com.Arjun.MyMarket.cart_order.dto.CartItemResponse;
import com.Arjun.MyMarket.cart_order.dto.CartResponse;
import com.Arjun.MyMarket.cart_order.dto.UpdateCartItemRequest;
import com.Arjun.MyMarket.cart_order.entity.Cart;
import com.Arjun.MyMarket.cart_order.entity.CartItem;
import com.Arjun.MyMarket.cart_order.entity.CartStatus;
import com.Arjun.MyMarket.cart_order.exception.BusinessRuleException;
import com.Arjun.MyMarket.cart_order.repository.CartItemRepository;
import com.Arjun.MyMarket.cart_order.repository.CartRepository;
//import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository){
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    //to get the cart for particular user
    //also transactional with readOnly true means the transaction here is opened for reading like SELECT queries
    //any update in the entity should not be stored in database by hibernate
    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId){
        return toResponse(getOrCreateActiveCart(userId));
    }

    @Override
    public CartResponse addItem(String userId, AddCartItemRequest request){
        Cart cart = getOrCreateActiveCart(userId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(request.productId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProductId(request.productId());
                    cart.getCartItems().add(newitem);
                    return newItem;
                });

        cartItem.setProductTitle(product.title);
        cartItem.setUnitPrice(finalUnitPrice(product.price(), product.discount()));
        cartItem.setDiscountPercent(defaultZero(product.discount()));
        cartItem.setQuantity(safeQuantity(cartItem.getQuantity()) + request.quantity());

        //cartItemRepository.save(cartItem);

        //we dont need to save the cartItem in cartItemRepository as cart is our owning entity so hibernate does the dirty checking
        //and checks if children entity like cartitem is updated or not and it will save the cartItem in db while saving the cart
        return toResponse(cartRepository.save(cart));

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

    @Override
    public CartResponse updateItem(String userId, String productId, UpdateCartItemRequest request) {
        return null;
    }

    @Override
    public CartResponse removeItem(String userId, String productId) {
        return null;
    }

    @Override
    public void clearCart(String userId) {

    }

    //get the cart if exists with Active status or create a new one if does not exists with ACTIVE status
    public Cart getOrCreateActiveCart(String userId){
        if(!StringUtils.hasText(userId)){
            throw new BusinessRuleException("User id is required");
        }

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
