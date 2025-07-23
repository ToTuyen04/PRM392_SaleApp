package com.salesapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    
    private int id;
    private int cartID;
    private int userID;
    private String paymentMethod;
    private String billingAddress;
    private String orderStatus;
    private Instant orderDate;
    private List<PaymentResponse> payments;
    
    // User information
    private String username;
    private String phoneNumber;
    private String email;
    
    // Cart items (products in this order)
    private List<CartItemResponse> cartItems;
    
    // Calculated fields
    private BigDecimal totalAmount;
    private String formattedOrderDate;
    private String latestTransactionId;
    private String paymentStatus;
}
