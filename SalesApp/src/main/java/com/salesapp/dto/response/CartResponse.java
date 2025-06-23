package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Integer id;
    private Integer userID;
    private BigDecimal totalPrice;
    private String status;
    private List<CartItemResponse> cartItems;
}
