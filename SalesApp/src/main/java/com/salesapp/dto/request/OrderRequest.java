package com.salesapp.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequest {
    private String paymentMethod;
    private String billingAddress;
}
