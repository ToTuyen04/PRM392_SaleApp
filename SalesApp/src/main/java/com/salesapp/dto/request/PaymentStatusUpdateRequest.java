package com.salesapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatusUpdateRequest {
    
    @NotBlank(message = "Payment status cannot be blank")
    @Pattern(regexp = "^(Paid|Cancelled|Pending)$", message = "Payment status must be Paid, Cancelled, or Pending")
    private String paymentStatus;
    
    // Optional field for additional notes or transaction reference
    private String note;
}
