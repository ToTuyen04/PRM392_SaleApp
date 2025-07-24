package com.salesapp.mapper;

import com.salesapp.dto.response.CartItemResponse;
import com.salesapp.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "productID.id", target = "productID")
    @Mapping(source = "productID.productName", target = "productName")
    @Mapping(source = "productID.imageURL", target = "productImage")
    @Mapping(target = "subtotal", expression = "java(calculateSubtotal(cartItem.getPrice(), cartItem.getQuantity()))")
    CartItemResponse toCartItemResponse(CartItem cartItem);

    List<CartItemResponse> toCartItemResponses(List<CartItem> cartItems);

    default BigDecimal calculateSubtotal(BigDecimal price, Integer quantity) {
        if (price == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
