package com.salesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemUpdateRequest {
    private Integer cartItemID;
    private Integer quantity;
}
