package com.salesapp.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
public class PaymentResponse {
    private Integer id;
    private Integer orderID;
    private BigDecimal amount;
    private Instant paymentDate;
    private String paymentStatus;

    //code...
}
