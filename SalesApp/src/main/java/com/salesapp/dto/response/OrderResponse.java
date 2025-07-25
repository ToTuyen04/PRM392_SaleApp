package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Integer id;
    private Integer cartID;
    private Integer userID;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;
    private Instant orderDate;

    // Danh sách payment nếu cần hiển thị ra
    private List<PaymentResponse> payments;
    
    // Thêm danh sách sản phẩm trong order
    private List<CartItemResponse> cartItems;
    
    // User information để hiển thị customer details
    private String username;
    private String phoneNumber;
    private String email;
    
    // Calculated fields
    private BigDecimal totalAmount;
    private String formattedOrderDate;
}
