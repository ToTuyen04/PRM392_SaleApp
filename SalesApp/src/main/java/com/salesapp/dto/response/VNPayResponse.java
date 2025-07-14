package com.salesapp.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VNPayResponse {
    String orderInfo;
    String paymentTime;
    String transactionID;
    String totalPrice;
    int status;
}
