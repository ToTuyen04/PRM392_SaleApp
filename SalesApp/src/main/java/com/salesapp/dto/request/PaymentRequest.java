package com.salesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequest {
    private Integer orderID;
    private BigDecimal amount;
    private String paymentStatus;
}
