package com.salesapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemSnapshot {
    
    private Integer cartItemId;
    private Integer productId;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;
    
    // Constructor từ CartItem entity
    public static CartItemSnapshot fromCartItem(com.salesapp.entity.CartItem cartItem) {
        return CartItemSnapshot.builder()
                .cartItemId(cartItem.getId())
                .productId(cartItem.getProductID().getId())
                .productName(cartItem.getProductID().getProductName())
                .productImage(cartItem.getProductID().getImageURL())
                .quantity(cartItem.getQuantity())
                .price(cartItem.getProductID().getPrice())
                .subtotal(cartItem.getPrice())
                .build();
    }
}
