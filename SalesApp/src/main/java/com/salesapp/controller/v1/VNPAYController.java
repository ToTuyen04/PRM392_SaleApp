package com.salesapp.controller.v1;

import com.salesapp.dto.request.OrderRequest;
import com.salesapp.dto.response.OrderResponse;
import com.salesapp.dto.response.ResponseObject;
import com.salesapp.dto.response.VNPayResponse;
import com.salesapp.service.OrderService;
import com.salesapp.service.VNPAYService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/v1/vnpay")
@RequiredArgsConstructor
@Tag(name = "VNPay", description = "VNPay Payment Integration")
public class VNPAYController {
    
    private final VNPAYService vnPayService;
    private final OrderService orderService;

    // Tạo URL thanh toán VNPay
    @PostMapping("/create-payment")
    public ResponseObject<String> createPayment(
            @RequestParam("amount") int totalAmount,
            @RequestParam("orderInfo") String orderInfo,
            @RequestParam(value = "returnUrl", defaultValue = "http://localhost:3000/payment-result") String returnUrl,
            HttpServletRequest request) {
        
        String vnpayUrl = vnPayService.createOrder(request, totalAmount, orderInfo, returnUrl);
        
        return ResponseObject.<String>builder()
                .status(1000)
                .message("VNPay payment URL created successfully")
                .data(vnpayUrl)
                .build();
    }

    // Xử lý callback từ VNPay sau khi thanh toán
    @GetMapping("/payment-callback")
    public ResponseObject<VNPayResponse> paymentCallback(HttpServletRequest request, HttpServletResponse response) throws IOException {
        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        // Lấy orderId từ vnp_OrderInfo hoặc parameter
        String orderInfo = request.getParameter("vnp_OrderInfo");
        int orderId = 0;
        try {
            orderId = Integer.parseInt(orderInfo);
        } catch (NumberFormatException e) {
            // Nếu orderInfo không phải là số, có thể cần parse khác
            System.out.println("Cannot parse orderId from orderInfo: " + orderInfo);
        }

        if (vnPayResponse.getStatus() == 1) {
            // Thanh toán thành công - cập nhật order nếu có orderId
            if (orderId > 0) {
                try {
                    orderService.updateOrderAfterVNPaySuccess(orderId, vnPayResponse.getTransactionID());
                } catch (Exception e) {
                    System.out.println("Error updating order: " + e.getMessage());
                }
            }

            return ResponseObject.<VNPayResponse>builder()
                    .status(1000)
                    .message("Payment successful")
                    .data(vnPayResponse)
                    .build();
        } else if (vnPayResponse.getStatus() == 0) {
            // Thanh toán thất bại - hủy order nếu có orderId
            if (orderId > 0) {
                try {
                    orderService.cancelOrder(orderId);
                } catch (Exception e) {
                    System.out.println("Error cancelling order: " + e.getMessage());
                }
            }

            return ResponseObject.<VNPayResponse>builder()
                    .status(2000)
                    .message("Payment failed")
                    .data(vnPayResponse)
                    .build();
        } else {
            // Chữ ký không hợp lệ
            return ResponseObject.<VNPayResponse>builder()
                    .status(3000)
                    .message("Invalid payment signature")
                    .data(vnPayResponse)
                    .build();
        }
    }

    // Endpoint để tạo thanh toán cho một order cụ thể
    @PostMapping("/create-payment-for-order/{orderId}")
    public ResponseObject<String> createPaymentForOrder(
            @PathVariable int orderId,
            @RequestParam(value = "returnUrl", defaultValue = "http://localhost:3000/payment-result") String returnUrl,
            HttpServletRequest request) {

        // Lấy thông tin order từ database
        var orderResponse = orderService.getOrderById(orderId);
        String orderInfo = String.valueOf(orderId); // Chỉ gửi orderId để dễ parse

        // Lấy amount từ cart của order (cần convert BigDecimal to int)
        int amount = orderResponse.getPayments().isEmpty() ?
            100000 : // Default nếu chưa có payment
            orderResponse.getPayments().get(0).getAmount().intValue();

        String vnpayUrl = vnPayService.createOrder(request, amount, orderInfo, returnUrl);

        return ResponseObject.<String>builder()
                .status(1000)
                .message("VNPay payment URL created for order #" + orderId)
                .data(vnpayUrl)
                .build();
    }

    // Endpoint để xử lý kết quả thanh toán và cập nhật order
    @GetMapping("/payment-result")
    public ResponseObject<VNPayResponse> handlePaymentResult(
            @RequestParam int orderId,
            HttpServletRequest request) {

        VNPayResponse vnPayResponse = vnPayService.orderReturn(request);

        if (vnPayResponse.getStatus() == 1) {
            // Thanh toán thành công - cập nhật order
            orderService.updateOrderAfterVNPaySuccess(orderId, vnPayResponse.getTransactionID());

            return ResponseObject.<VNPayResponse>builder()
                    .status(1000)
                    .message("Payment successful and order updated")
                    .data(vnPayResponse)
                    .build();
        } else if (vnPayResponse.getStatus() == 0) {
            // Thanh toán thất bại - hủy order
            orderService.cancelOrder(orderId);

            return ResponseObject.<VNPayResponse>builder()
                    .status(2000)
                    .message("Payment failed and order cancelled")
                    .data(vnPayResponse)
                    .build();
        } else {
            // Chữ ký không hợp lệ
            return ResponseObject.<VNPayResponse>builder()
                    .status(3000)
                    .message("Invalid payment signature")
                    .data(vnPayResponse)
                    .build();
        }
    }

    // Endpoint để tạo order và payment URL cùng lúc
    @PostMapping("/create-order-and-payment/{userId}")
    public ResponseObject<String> createOrderAndPayment(
            @PathVariable int userId,
            @RequestBody OrderRequest orderRequest,
            @RequestParam(value = "returnUrl", defaultValue = "http://localhost:3000/payment-result") String returnUrl,
            HttpServletRequest request) {

        // Tạo order trước
        OrderResponse orderResponse = orderService.createOrderForVNPay(userId, orderRequest);

        // Tạo payment URL
        String orderInfo = "Thanh toan don hang #" + orderResponse.getId();
        // Lấy amount từ cart (cần implement logic lấy cart total)
        int amount = 100000; // Tạm thời hardcode, nên lấy từ cart thực tế

        String vnpayUrl = vnPayService.createOrder(request, amount, orderInfo,
                returnUrl + "?orderId=" + orderResponse.getId());

        return ResponseObject.<String>builder()
                .status(1000)
                .message("Order created and VNPay payment URL generated")
                .data(vnpayUrl)
                .build();
    }

    // ========== TEST ENDPOINTS ==========

    @PostMapping("/test-callback")
    public ResponseObject<String> testCallback(
            @RequestParam int orderId,
            @RequestParam(defaultValue = "00") String responseCode,
            @RequestParam(defaultValue = "00") String transactionStatus) {

        // Tạo mock VNPay parameters
        String testUrl = String.format(
            "/v1/vnpay/payment-callback?vnp_Amount=%d&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=%s&vnp_TransactionStatus=%s&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
            100000 * 100, // amount * 100
            orderId,
            responseCode,
            transactionStatus,
            System.currentTimeMillis()
        );

        return ResponseObject.<String>builder()
                .status(1000)
                .message("Test callback URL generated")
                .data("http://localhost:8080" + testUrl)
                .build();
    }

    @GetMapping("/test-payment-scenarios")
    public ResponseObject<Object> testPaymentScenarios(@RequestParam int orderId) {
        String baseUrl = "http://localhost:8080";
        long timestamp = System.currentTimeMillis();

        // Tạo các scenario test khác nhau
        Object scenarios = new Object() {
            public final String successCallback = String.format(
                "%s/v1/vnpay/payment-callback?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp
            );

            public final String failedCallback = String.format(
                "%s/v1/vnpay/payment-callback?vnp_Amount=10000000&vnp_BankCode=NCB&vnp_OrderInfo=%d&vnp_ResponseCode=24&vnp_TransactionStatus=02&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 1
            );

            public final String successResult = String.format(
                "%s/v1/vnpay/payment-result?orderId=%d&vnp_Amount=10000000&vnp_ResponseCode=00&vnp_TransactionStatus=00&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 2
            );

            public final String failedResult = String.format(
                "%s/v1/vnpay/payment-result?orderId=%d&vnp_Amount=10000000&vnp_ResponseCode=24&vnp_TransactionStatus=02&vnp_TransactionNo=%d&vnp_SecureHash=test_hash",
                baseUrl, orderId, timestamp + 3
            );
        };

        return ResponseObject.builder()
                .status(1000)
                .message("Test scenarios generated for order " + orderId)
                .data(scenarios)
                .build();
    }
}
