package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {
    private Integer id;
    private Integer productID;
    private String productName;
    private String productImage;
    private Integer quantity;
    private BigDecimal price; // Giá gốc của 1 sản phẩm
    private BigDecimal subtotal; // Tổng tiền của cart item (price × quantity)
}
