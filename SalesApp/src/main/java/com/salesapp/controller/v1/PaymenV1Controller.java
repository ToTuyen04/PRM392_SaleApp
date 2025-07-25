package com.salesapp.controller.v1;

import com.salesapp.dto.request.PaymentRequest;
import com.salesapp.dto.request.PaymentStatusUpdateRequest;
import com.salesapp.dto.response.PaymentResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Manage Payment")
public class PaymenV1Controller {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseObject<List<PaymentResponse>> getAllPayments() {
        return ResponseObject.<List<PaymentResponse>>builder()
                .status(1000)
                .message("Payments retrieved")
                .data(paymentService.getAllPayments())
                .build();
    }

    @GetMapping("/{id}")
    public ResponseObject<PaymentResponse> getPaymentById(@PathVariable int id) {
        return ResponseObject.<PaymentResponse>builder()
                .status(1000)
                .message("Payment retrieved")
                .data(paymentService.getPaymentById(id))
                .build();
    }

    @PostMapping
    public ResponseObject<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        return ResponseObject.<PaymentResponse>builder()
                .status(1000)
                .message("Payment created")
                .data(paymentService.createPayment(request))
                .build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update payment status", description = "Update payment status to Paid, Cancelled, or Pending")
    public ResponseObject<PaymentResponse> updatePaymentStatus(
            @PathVariable int id, 
            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        return ResponseObject.<PaymentResponse>builder()
                .status(1000)
                .message("Payment status updated successfully")
                .data(paymentService.updatePaymentStatus(id, request))
                .build();
    }

    @PutMapping
    public ResponseObject<PaymentResponse> updatePayment(@RequestBody PaymentRequest request) {
        // TODO: Implement full payment update if needed
        return null;
    }
}
