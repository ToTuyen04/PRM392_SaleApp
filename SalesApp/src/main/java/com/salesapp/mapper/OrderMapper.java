package com.salesapp.mapper;

import com.salesapp.dto.response.OrderResponse;
import com.salesapp.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PaymentMapper.class, CartItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "cartID.id", target = "cartID")
    @Mapping(source = "userID.id", target = "userID")
    @Mapping(source = "cartID.cartItems", target = "cartItems")
    OrderResponse toOrder(Order order);

    List<OrderResponse> toOrders(List<Order> orders);
}


