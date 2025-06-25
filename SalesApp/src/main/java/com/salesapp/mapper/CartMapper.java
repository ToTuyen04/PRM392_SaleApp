package com.salesapp.mapper;

import com.salesapp.dto.response.CartItemResponse;
import com.salesapp.dto.response.CartResponse;
import com.salesapp.entity.Cart;
import com.salesapp.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(source = "userID.id", target = "userID")
    @Mapping(source = "cartItems", target = "cartItems")
    CartResponse toDto(Cart cart);

    List<CartResponse> toCarts(List<Cart> carts);

    @Mapping(source = "productID.id", target = "productID")
    @Mapping(source = "productID.productName", target = "productName")
    CartItemResponse toCartItem(CartItem cartItem);

    List<CartItemResponse> toCartItems(List<CartItem> cartItems);
}
