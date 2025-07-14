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
    private String productImage;      // ✅ Thêm
    private BigDecimal productPrice;  // ✅ Thêm
    private Integer quantity;
    private BigDecimal price;
}
